package edu.kit.ipd.pp.joframes.ast.base;

/**
 * Represents a call to an instance method.
 *
 * @author Martin Armbruster
 */
public class Method extends Call {
	/**
	 * Creates a new instance.
	 *
	 * @param signature signature of the method this call goes to.
	 */
	public Method(final String signature) {
		super(signature);
	}
}
