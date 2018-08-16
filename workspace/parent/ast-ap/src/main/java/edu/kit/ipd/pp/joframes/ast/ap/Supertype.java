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
	 * @param superType the super type for this rule.
	 */
	public Supertype(String superType) {
		this.superType = superType;
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
