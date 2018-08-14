package ast.base;

import java.util.HashSet;

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
	 * @param threadType the thread policy.
	 */
	public WorkingPhase(ThreadType threadType) {
		this.threadType = threadType;
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
	public void addRule(Rule rule) {
		rules.add(rule);
	}
}
