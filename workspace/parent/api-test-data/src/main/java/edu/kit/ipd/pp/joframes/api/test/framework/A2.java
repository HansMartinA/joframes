package edu.kit.ipd.pp.joframes.api.test.framework;

/**
 * Represents a second class that can control the framework's life-cycle.
 * 
 * @author Martin Armbruster
 */
public interface A2 {
	/**
	 * Convenient method for the initialization process.
	 */
	default void initialize() {
		initialize("Hello");
	}
	/**
	 * Initializes the framework.
	 * 
	 * @param s a random parameter.
	 */
	void initialize(String s);
	/**
	 * Starts the framework.
	 */
	void run();
	/**
	 * Stops the framework.
	 */
	void stop();
}
