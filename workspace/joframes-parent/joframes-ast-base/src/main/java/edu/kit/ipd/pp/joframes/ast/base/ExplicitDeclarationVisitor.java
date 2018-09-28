package edu.kit.ipd.pp.joframes.ast.base;

/**
 * Visitor for the content of an explicit declaration.
 *
 * @author Martin Armbruster
 */
public interface ExplicitDeclarationVisitor {
	/**
	 * Visits an explicit declaration.
	 *
	 * @param declaration the explicit declaration.
	 */
	void visitExplicitDeclaration(ExplicitDeclaration declaration);
	/**
	 * Visits a method call.
	 *
	 * @param method the method.
	 */
	void visitMethod(Method method);
	/**
	 * Visits a call to a static method.
	 *
	 * @param stMethod the static method.
	 */
	void visitStaticMethod(StaticMethod stMethod);

	/**
	 * This class provides a default implementation of an explicit declaration visitor with empty implementations.
	 *
	 * @author Martin Armbruster
	 */
	abstract class DefaultExplicitDeclarationVisitor implements ExplicitDeclarationVisitor {
		@Override
		public void visitExplicitDeclaration(final ExplicitDeclaration declaration) {
		}

		@Override
		public void visitMethod(final Method method) {
		}

		@Override
		public void visitStaticMethod(final StaticMethod stMethod) {
		}
	}
}
