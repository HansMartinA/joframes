package edu.kit.ipd.pp.joframes.api;

import com.ibm.wala.classLoader.IClass;
import edu.kit.ipd.pp.joframes.ast.base.Framework;
import java.util.HashSet;
import java.util.Set;

/**
 * Wraps a framework with additional information for exchange between different stages within the pipeline.
 * 
 * @author Martin Armbruster
 */
class FrameworkWrapper {
	/*
	 * Stores the actual framework.
	 */
	private Framework framework;
	/**
	 * Stores the framework classes found during the class hierarchy analysis.
	 */
	private HashSet<IClass> frameworkClasses;
	
	/**
	 * Creates a new instance.
	 * 
	 * @param framework the wrapped framework.
	 */
	FrameworkWrapper(Framework framework) {
		this.framework = framework;
		frameworkClasses = new HashSet<>();
	}
	
	/**
	 * Returns the wrapped framework.
	 * 
	 * @return the wrapped framework.
	 */
	Framework getFramework() {
		return framework;
	}
	
	/**
	 * Adds a framework class.
	 * 
	 * @param frameworkClass the framework class.
	 */
	void addFrameworkClass(IClass frameworkClass) {
		frameworkClasses.add(frameworkClass);
	}
	
	/**
	 * Returns the framework classes in form of WALA classes.
	 * 
	 * @return the set with the framework classes.
	 */
	Set<IClass> getFrameworkClasses() {
		return (Set<IClass>)frameworkClasses.clone();
	}
}
