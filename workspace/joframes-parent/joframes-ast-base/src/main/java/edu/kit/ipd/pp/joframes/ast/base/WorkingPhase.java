package edu.kit.ipd.pp.joframes.ast.base;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a working phase in a framework.
 *
 * @author Martin Armbruster
 */
public class WorkingPhase implements AstBaseClass {
	/**
	 * Stores the thread policy of the working phase.
	 */
	private ThreadType threadType;
	/**
	 * Stores all rules for the working phase.
	 */
	private HashSet<Rule> rules;

	/**
	 * Creates a new instance.
	 *
	 * @param type the thread policy.
	 */
	public WorkingPhase(final ThreadType type) {
		this.threadType = type;
		rules = new HashSet<>();
	}

	/**
	 * Returns the thread policy for the working phase.
	 *
	 * @return the thread policy.
	 */
	public ThreadType getThreadType() {
		return threadType;
	}

	/**
	 * Adds a rule to the working phase.
	 *
	 * @param rule the rule to add.
	 */
	public void addRule(final Rule rule) {
		rules.add(rule);
	}

	/**
	 * Removes a rule.
	 *
	 * @param rule the rule to be removed.
	 */
	public void removeRule(final Rule rule) {
		rules.remove(rule);
	}

	/**
	 * Returns the set of added rules. Modifications to the set are not reflected in this working phase, but
	 * modifications to the rules.
	 *
	 * @return the set.
	 */
	public Set<Rule> getRules() {
		return (Set<Rule>) rules.clone();
	}
}
