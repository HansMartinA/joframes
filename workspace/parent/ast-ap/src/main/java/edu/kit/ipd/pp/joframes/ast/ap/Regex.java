package edu.kit.ipd.pp.joframes.ast.ap;

import edu.kit.ipd.pp.joframes.ast.base.Rule;

/**
 * Represents a regular expression as a rule for a working phase.
 * 
 * @author Martin Armbruster
 */
public class Regex extends Rule {
	/**
	 * Stores the regular expression.
	 */
	private String regex;
	
	/**
	 * Creates a new instance.
	 * 
	 * @param regex the actual regular expression for the rule.
	 */
	public Regex(String regex) {
		this.regex = regex;
	}
	
	/**
	 * Returns the actual regular expression.
	 * 
	 * @return the regular expression.
	 */
	public String getRegularExpression() {
		return regex;
	}
}
