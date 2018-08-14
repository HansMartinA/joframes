package ast.base;

import java.util.ArrayList;

/**
 * Represents a specified framework.
 * 
 * @author Martin Armbruster
 */
public class Framework implements AstBaseClass {
	/**
	 * Stores the start phase of the framework.
	 */
	private StartPhase start;
	/**
	 * Stores the end phase of the framework.
	 */
	private EndPhase end;
	/**
	 * Stores all working phases of the framework.
	 */
	private ArrayList<WorkingPhase> workingPhases;
	
	/**
	 * Creates a new instance.
	 */
	public Framework() {
		workingPhases = new ArrayList<>();
	}
	
	/**
	 * Returns the start phase of the framework.
	 * 
	 * @return the start phase.
	 */
	public StartPhase getStartPhase() {
		return start;
	}
	
	/**
	 * Sets the start phase of the framework.
	 * 
	 * @param start the new start phase.
	 */
	public void setStartPhase(StartPhase start) {
		this.start = start;
	}
	
	/**
	 * Returns the end phase of the framework.
	 * 
	 * @return the end phase.
	 */
	public EndPhase getEndPhase() {
		return end;
	}
	
	/**
	 * Sets the end phase of the framework.
	 * 
	 * @param end the new end phase.
	 */
	public void setEndPhase(EndPhase end) {
		this.end = end;
	}
	
	/**
	 * Adds a working phase to the framework.
	 * 
	 * @param working the working phase to add.
	 */
	public void addWorkingPhase(WorkingPhase working) {
		workingPhases.add(working);
	}
}
