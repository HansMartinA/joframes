package edu.kit.ipd.pp.joframes.api;

import com.ibm.wala.classLoader.IClass;
import edu.kit.ipd.pp.joframes.ast.base.Framework;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
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
	 * Stores a map from framework classes in form of WALA classes to actual Java classes.
	 */
	private HashMap<IClass, Class<?>> frameworkClassesToClasses;
	/**
	 * Stores a set of application classes corresponding to the framework classes as application subclasses.
	 */
	private HashSet<IClass> applicationClasses;
	
	/**
	 * Creates a new instance.
	 * 
	 * @param framework the wrapped framework.
	 */
	FrameworkWrapper(Framework framework) {
		this.framework = framework;
		frameworkClassesToClasses = new HashMap<>();
		applicationClasses = new HashSet<>();
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
		try {
			Class<?> javaClass = Class.forName(frameworkClass.getName().toUnicodeString());
			frameworkClassesToClasses.put(frameworkClass, javaClass);
		} catch(ClassNotFoundException e) {
		}
	}
	
	/**
	 * Returns the framework classes in form of WALA classes.
	 * 
	 * @return the set with the framework classes.
	 */
	Set<IClass> getFrameworkClass() {
		return frameworkClassesToClasses.keySet();
	}
	
	/**
	 * Returns the framework classes in form of Java classes.
	 * 
	 * @return the set with the framework classes.
	 */
	Set<Class<?>> getClasses() {
		HashSet<Class<?>> classes = new HashSet<>();
		for(Entry<?, Class<?>> entry : frameworkClassesToClasses.entrySet()) {
			classes.add(entry.getValue());
		}
		return classes;
	}
	
	/**
	 * Adds an application class.
	 * 
	 * @param appClass the application class.
	 */
	void addApplicationClass(IClass appClass) {
		applicationClasses.add(appClass);
	}
	
	/**
	 * Returns the application subclasses of the framework classes.
	 * 
	 * @return the set with the application classes.
	 */
	Set<IClass> getApplicationClasses() {
		return (Set<IClass>)applicationClasses.clone();
	}
}
