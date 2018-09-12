package edu.kit.ipd.pp.joframes.shrike;

import com.ibm.wala.shrikeBT.shrikeCT.CTUtils;
import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;
import com.ibm.wala.shrikeBT.shrikeCT.OfflineInstrumenter;
import com.ibm.wala.shrikeCT.ClassWriter;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Wraps a ClassInstrumenter and, therefore, a class.
 *
 * @author Martin Armbruster
 */
public class ClassInstrumenterWrapper {
	/**
	 * Stores the wrapped ClassInstrumenter.
	 */
	private ClassInstrumenter clInstr;
	/**
	 * Stores all original methods of the class.
	 */
	private ArrayList<MethodWrapper> methods;
	/**
	 * Stores all newly created methods.
	 */
	private ArrayList<MethodWrapper> additionalMethods;

	/**
	 * Creates a new instance.
	 *
	 * @param clInstrWrap class instrumenter that will be wrapped.
	 * @throws InvalidClassFileException if the class is invalid.
	 */
	ClassInstrumenterWrapper(final ClassInstrumenter clInstrWrap) throws InvalidClassFileException {
		clInstr = clInstrWrap;
		methods = new ArrayList<>();
		clInstr.visitMethods(data -> methods.add(new MethodWrapper(data)));
		additionalMethods = new ArrayList<>();
	}

	/**
	 * Returns the file input name of the class file.
	 *
	 * @return the class file name.
	 */
	public String getClassInputName() {
		return clInstr.getInputName();
	}

	/**
	 * Creates a method and adds it to the class.
	 *
	 * @param access access restriction for the method.
	 * @param name name of the new method.
	 * @param signature signature of the method.
	 * @return wrapper of the newly created method.
	 */
	public MethodWrapper createMethod(final int access, final String name, final String signature) {
		MethodWrapper wrapper = new MethodWrapper(clInstr.createEmptyMethodData(name, signature, access));
		additionalMethods.add(wrapper);
		return wrapper;
	}

	/**
	 * Returns a method of the class.
	 *
	 * @param name name of the method.
	 * @param signature signature of the method.
	 * @return the method or null if it cannot be found.
	 */
	public MethodWrapper getMethod(final String name, final String signature) {
		for (MethodWrapper m : methods) {
			if (m.getMethodName().equals(name) && m.getMethodSignature().equals(signature)) {
				return m;
			}
		}
		for (MethodWrapper m : additionalMethods) {
			if (m.getMethodName().equals(name) && m.getMethodSignature().equals(signature)) {
				return m;
			}
		}
		return null;
	}

	/**
	 * Visits all methods of the class.
	 *
	 * @param visitor visitor for the visit.
	 */
	public void visitMethods(final MethodVisitor visitor) {
		for (MethodWrapper m : methods) {
			visitor.visitMethod(m);
		}
		for (MethodWrapper m : additionalMethods) {
			visitor.visitMethod(m);
		}
	}

	/**
	 * Outputs the class.
	 *
	 * @param offInstr general instrumenter that writes the class out.
	 * @param addedMethods a set of and for all newly created methods.
	 * @throws IOException if the class cannot be written.
	 * @throws InvalidClassFileException if the class is invalid.
	 */
	void outputClass(final OfflineInstrumenter offInstr, final HashSet<String> addedMethods)
			throws IOException, InvalidClassFileException {
		ClassWriter writer = clInstr.emitClass();
		for (MethodWrapper m : additionalMethods) {
			addedMethods.add(m.getClassType() + m.getMethodName() + m.getMethodSignature());
			CTUtils.compileAndAddMethodToClassWriter(m.getMethodData(), writer, null);
		}
		offInstr.outputModifiedClass(clInstr, writer);
	}
}
