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
	 * @param textInput text that will be printed out.
	 */
	public AEventListenerImpl(final String textInput) {
		this.text = textInput;
	}

	@Override
	public void handle() {
		System.out.println(text);
	}
}
