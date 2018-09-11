package edu.kit.ipd.pp.joframes.shrike;

import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;
import com.ibm.wala.shrikeBT.shrikeCT.OfflineInstrumenter;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
	 * Stores all wrapped class instrumenter.
	 */
	private ArrayList<ClassInstrumenterWrapper> clInstrs;

	/**
	 * Creates a new instance.
	 */
	public InstrumenterWrapper() {
		offInstr = new OfflineInstrumenter();
	}

	/**
	 * Adds a directory with class files to the instrumenter. Subfolders are also scanned.
	 *
	 * @param directory the directory path.
	 * @throws IOException if an IOException occurs.
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
	 */
	public void addInputJar(final String jarFile) throws IOException {
		if (clInstrs != null) {
			throw new IllegalStateException("A jar file can only be added .");
		}
		File jar = new File(jarFile);
		if (!jarFile.endsWith(".jar") || !jar.exists()) {
			throw new IllegalArgumentException("The given jar file (" + jarFile + ") is not valid.");
		}
		offInstr.addInputJar(jar);
	}

	/**
	 * Returns an instrumenter for a class.
	 *
	 * @param className name of the class for which the instrumenter is returned.
	 * @return the wrapped instrumenter or null if it cannot be found.
	 */
	public ClassInstrumenterWrapper getClassInstrumenter(final String className) {
		if (clInstrs == null) {
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
		for (ClassInstrumenterWrapper wrap : clInstrs) {
			if (wrap.getClassInputName().contains(className)) {
				return wrap;
			}
		}
		return null;
	}
}
