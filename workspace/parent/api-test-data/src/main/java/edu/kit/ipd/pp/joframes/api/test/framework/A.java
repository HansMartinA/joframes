package edu.kit.ipd.pp.joframes.api.test.framework;

/**
 * Represents a class controlling the framework's life cycle.
 * 
 * @author Martin Armbruster
 */
public class A {
	/**
	 * Represents an initialization process.
	 */
	public void init() {
		System.out.println("A init()");
	}
	
	/**
	 * Represents the starting of the framework.
	 */
	public void start() {
		System.out.println("A start()");
	}
	
	/**
	 * Represents the end of the framework.
	 */
	public void destroy() {
		System.out.println("A destroy()");
	}
}
