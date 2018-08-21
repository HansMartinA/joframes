package edu.kit.ipd.pp.joframes.api.external;

/**
 * Class that contains the artificial main method acting as the main entry point.
 * 
 * @author Martin Armbruster
 */
public class ArtificialClass {
	/**
	 * Artificial main method.
	 * 
	 * @param args Arguments.
	 */
	public static void main(String[] args) {
		new ArtificialClass().artificialMain();
	}
	
	/**
	 * Creates a new instance. Private to avoid external instantiation.
	 */
	private ArtificialClass() {
	}
	
	/**
	 * The actual artificial main method.
	 */
	private void artificialMain() {
		start();
		working();
		end();
	}
	
	/**
	 * Will contain the code for the start phase.
	 */
	private void start() {
	}
	
	/**
	 * Will contain the code for the end phase.
	 */
	private void end() {
	}
	
	/**
	 * Will contain the code for the working phase.
	 */
	private void working() {
	}
}
