package edu.kit.ipd.pp.joframes.ast.base;

/**
 * Represents the end phase in a framework.
 *
 * @author Martin Armbruster
 */
public class EndPhase implements AstBaseClass {
	/**
	 * Stores the explicit declaration representing the end phase.
	 */
	private ExplicitDeclaration end;

	/**
	 * Creates a new instance.
	 *
	 * @param endDec explicit declaration representing the end phase.
	 */
	public EndPhase(final ExplicitDeclaration endDec) {
		this.end = endDec;
	}

	/**
	 * Returns the explicit declaration representing the end phase.
	 *
	 * @return the explicit declaration.
	 */
	public ExplicitDeclaration getEnd() {
		return end;
	}
}
