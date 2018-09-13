package edu.kit.ipd.pp.joframes.shrike;

import com.ibm.wala.shrikeBT.ConstantInstruction;
import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;
import com.ibm.wala.shrikeBT.shrikeCT.OfflineInstrumenter;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Wrapper for the OfflineInstrumenter: this is the entry point for the abstraction API and collects all classes.
 *
 * @author Martin Armbruster
 */
public class InstrumenterWrapper {
	/**
	 * Stores the actual bytecode instrumenter.
	 */
	private OfflineInstrumenter offInstr;
	/**
	 * Stores the output jar file.
	 */
	private String output;
	/**
	 * Stores all wrapped class instrumenter.
	 */
	private ArrayList<ClassInstrumenterWrapper> clInstrs;

	/**
	 * Creates a new instance.
	 */
	public InstrumenterWrapper() {
		offInstr = new OfflineInstrumenter();
		offInstr.setPassUnmodifiedClasses(true);
	}

	/**
	 * Adds a directory with class files to the instrumenter. Subfolders are also scanned.
	 *
	 * @param directory the directory path.
	 * @throws IOException if an IOException occurs.
	 * @throws IllegalStateException when the method is called after all classes have been read.
	 * @throws IllegalArgumentException when the given directory is not valid.
	 */
	public void addInputDirectory(final String directory) throws IOException {
		if (clInstrs != null) {
			throw new IllegalStateException("A directory can only be added directly after the object construction.");
		}
		File dir = new File(directory);
		if (!dir.exists() || !dir.isDirectory()) {
			throw new IllegalArgumentException("The given directory (" + directory + ") is not valid.");
		}
		offInstr.addInputDirectory(dir, dir);
	}

	/**
	 * Adds a jar file to the instrumenter.
	 *
	 * @param jarFile path to the jar file.
	 * @throws IOException if an IOException occurs.
	 * @throws IllegalStateException when the method is called after all classes have been read.
	 * @throws IllegalArgumentException when the given jar file is not valid.
	 */
	public void addInputJar(final String jarFile) throws IOException {
		if (clInstrs != null) {
			throw new IllegalStateException("A jar file can only be added directly after the object construction.");
		}
		File jar = new File(jarFile);
		if (!jarFile.endsWith(".jar") || !jar.exists()) {
			throw new IllegalArgumentException("The given jar file (" + jarFile + ") is not valid.");
		}
		JarFile jo = new JarFile(jarFile);
		Enumeration<JarEntry> entries = jo.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			if (entry.getName().endsWith(".class")) {
				offInstr.addInputJarEntry(jar, entry.getName());
			}
		}
		jo.close();
	}

	/**
	 * Adds an entry within a jar file to the instrumenter.
	 *
	 * @param jarEntry the jar entry in the form "path-to-jar-file".jar#"path-to-entry".
	 * @throws IOException if an IOException occurs.
	 * @throws IllegalStateException when the method is called after all classes have been read.
	 * @throws IllegalArgumentException when the given jar entry is not valid.
	 */
	public void addInputJarEntry(final String jarEntry) throws IOException {
		if (clInstrs != null) {
			throw new IllegalStateException("A jar entry can only be added directly after the obejct construction.");
		}
		if (!jarEntry.contains(".jar#") && !jarEntry.startsWith(".jar")) {
			throw new IllegalArgumentException("The given jar entry (" + jarEntry + ") is not valid.");
		}
		offInstr.addInputElement(new File(""), jarEntry);
	}

	/**
	 * Sets the output jar file for the resulting classes.
	 *
	 * @param outputJar the output jar file.
	 */
	public void setOutput(final String outputJar) {
		output = outputJar;
	}

	/**
	 * Returns the path to the output jar file.
	 *
	 * @return the path.
	 */
	public String getOutput() {
		return output;
	}

	/**
	 * Creates the wrappers for all input classes.
	 */
	private void createClassInstrumenterWrapper() {
		clInstrs = new ArrayList<>();
		try {
			offInstr.beginTraversal();
			for (int i = 0; i < offInstr.getNumInputClasses(); i++) {
				ClassInstrumenter clInstr = offInstr.nextClass();
				if (clInstr == null) {
					continue;
				}
				clInstrs.add(new ClassInstrumenterWrapper(clInstr));
			}
		} catch (IOException | InvalidClassFileException e) {
		}
	}

	/**
	 * Returns an instrumenter for a class.
	 *
	 * @param className name of the class for which the instrumenter is returned.
	 * @return the wrapped instrumenter or null if it cannot be found.
	 */
	public ClassInstrumenterWrapper getClassInstrumenter(final String className) {
		if (clInstrs == null) {
			createClassInstrumenterWrapper();
		}
		for (ClassInstrumenterWrapper wrap : clInstrs) {
			if (wrap.getClassInputName().contains(className)) {
				return wrap;
			}
		}
		return null;
	}

	/**
	 * Visit all classes.
	 *
	 * @param visitor the visitor.
	 */
	public void visitClasses(final ClassVisitor visitor) {
		if (clInstrs == null) {
			createClassInstrumenterWrapper();
		}
		for (ClassInstrumenterWrapper wrapper : clInstrs) {
			visitor.visitClass(wrapper);
		}
	}

	/**
	 * Outputs all classes.
	 *
	 * @throws IOException if a class cannot be written.
	 * @throws InvalidClassFileException if a class is invalid.
	 */
	public void outputClasses() throws IOException, InvalidClassFileException {
		String tempEnd = "-temp.jar";
		try {
			offInstr.setOutputJar(new File(output + tempEnd));
			HashSet<String> addedMethods = new HashSet<>();
			for (ClassInstrumenterWrapper wrapper : clInstrs) {
				wrapper.outputClass(offInstr, addedMethods);
			}
			offInstr.writeUnmodifiedClasses();
			offInstr.close();
			// For newly created methods, no stack map table is created. Therefore, all classes are loaded,
			// instrumented and written once more.
			offInstr = new OfflineInstrumenter();
			offInstr.addInputJar(new File(output + tempEnd));
			createClassInstrumenterWrapper();
			visitClasses(classWrapper -> {
				classWrapper.visitMethods(methodWrapper -> {
					if (!addedMethods.contains(methodWrapper.getClassType() + methodWrapper.getMethodName()
						+ methodWrapper.getMethodSignature())) {
						return;
					}
					methodWrapper.addInstructionAtLast(ConstantInstruction.make(0));
					methodWrapper.addInstructionAtLast(InstructionFactory.makePop());
					methodWrapper.instrumentMethod();
				});
			});
			offInstr.setOutputJar(new File(output));
			for (ClassInstrumenterWrapper wrapper : clInstrs) {
				wrapper.outputClass(offInstr, addedMethods);
			}
			offInstr.writeUnmodifiedClasses();
			offInstr.close();
			new File(output + tempEnd).delete();
		} catch (IOException e) {
			new File(output + tempEnd).delete();
			throw e;
		} catch (InvalidClassFileException e) {
			new File(output + tempEnd).delete();
			throw e;
		}
	}
}
