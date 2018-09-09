package edu.kit.ipd.pp.joframes.api.test.application;

import edu.kit.ipd.pp.joframes.api.test.framework.BEventListener;

/**
 * Implementation of the BEventListener.
 *
 * @author Martin Armbruster
 */
public class BEventListenerImpl implements BEventListener {
	@Override
	public void handle() {
		System.out.println("BEventListenerImpl handle()");
	}
}
