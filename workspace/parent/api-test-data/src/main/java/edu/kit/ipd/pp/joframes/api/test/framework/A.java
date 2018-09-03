package edu.kit.ipd.pp.joframes.api.test.framework;

/**
 * Represents a class controlling the framework's life cycle.
 * 
 * @author Martin Armbruster
 */
public class A {
	/**
	 * A static method that is similar to a main method.
	 * 
	 * @param args arguments.
	 */
	public static void similarToMain(String[] args) {
		System.out.println("A similarToMain([Ljava/lang/String;)V");
	}
	
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
