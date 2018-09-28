package edu.kit.ipd.pp.joframes.ast.ap;

import edu.kit.ipd.pp.joframes.ast.base.Rule;

/**
 * Represents a super type as a rule for a working phase.
 *
 * @author Martin Armbruster
 */
public class Supertype extends Rule {
	/**
	 * Stores the super type.
	 */
	private String superType;

	/**
	 * Creates a new instance.
	 *
	 * @param type the super type for this rule.
	 */
	public Supertype(final String type) {
		this.superType = type;
	}

	/**
	 * Returns the super type.
	 *
	 * @return the super type.
	 */
	public String getSuperType() {
		return superType;
	}
}
