package edu.kit.ipd.pp.joframes.shrike;

/**
 * Visitor interface for methods of one class.
 *
 * @author Martin Armbruster
 */
@FunctionalInterface
public interface MethodVisitor {
	/**
	 * Visits a method.
	 *
	 * @param methodWrapper object that wraps a method.
	 */
	void visitMethod(MethodWrapper methodWrapper);
}
