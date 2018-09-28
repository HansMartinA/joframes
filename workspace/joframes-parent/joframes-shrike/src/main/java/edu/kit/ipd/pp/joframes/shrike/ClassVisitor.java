package edu.kit.ipd.pp.joframes.shrike;

/**
 * Visitior for the wrapped classes.
 *
 * @author Martin Armbruster
 */
@FunctionalInterface
public interface ClassVisitor {
	/**
	 * Visits a wrapped class.
	 *
	 * @param classWrapper the wrapped class.
	 */
	void visitClass(ClassInstrumenterWrapper classWrapper);
}
