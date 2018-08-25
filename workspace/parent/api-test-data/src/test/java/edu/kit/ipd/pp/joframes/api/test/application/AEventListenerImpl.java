package edu.kit.ipd.pp.joframes.api.test.application;

import edu.kit.ipd.pp.joframes.api.test.framework.AEventListener;

/**
 * Implementation of an AEventListener.
 * 
 * @author Martin Armbruster
 */
public class AEventListenerImpl implements AEventListener {
	/**
	 * Stores the text to print out.
	 */
	private String text;
	
	/**
	 * Creates a new instance.
	 * 
	 * @param text text that will be printed out.
	 */
	public AEventListenerImpl(String text) {
		this.text = text;
	}
	
	@Override
	public void handle() {
		System.out.println(text);
	}
}
