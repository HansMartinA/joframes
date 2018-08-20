package edu.kit.ipd.pp.joframes.ast.base;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a specified framework.
 * 
 * @author Martin Armbruster
 */
public class Framework implements AstBaseClass {
	/**
	 * Stores the framework name.
	 */
	private String name;
	/**
	 * Stores the resource loader for the framework.
	 */
	private ResourceLoader resourceLoader;
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
	 * 
	 * @param name name of the framework.
	 */
	public Framework(String name) {
		this.name = name;
		workingPhases = new ArrayList<>();
	}
	
	/**
	 * Returns the name of the framework.
	 * 
	 * @return the framework name.
	 */
	public String getName() {
		return name;
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
	 * Returns the resource loader for the framework.
	 * 
	 * @return the resource loader.
	 */
	public ResourceLoader getResourceLoader() {
		return resourceLoader;
	}
	
	/**
	 * Sets the resource loader for the framework.
	 * 
	 * @param loader the resource loader.
	 */
	public void setResourceLoader(ResourceLoader loader) {
		resourceLoader = loader;
	}
	
	/**
	 * Adds a working phase to the framework.
	 * 
	 * @param working the working phase to add.
	 */
	public void addWorkingPhase(WorkingPhase working) {
		workingPhases.add(working);
	}
	
	/**
	 * Returns a list of all added working phases. The order of the phases is considered to be their order within the
	 * framework.
	 * 
	 * @return the list.
	 */
	public List<WorkingPhase> getWorkingPhases() {
		return (List<WorkingPhase>)workingPhases.clone();
	}
}
