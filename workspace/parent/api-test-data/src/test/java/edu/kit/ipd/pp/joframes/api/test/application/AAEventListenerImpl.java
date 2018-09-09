package edu.kit.ipd.pp.joframes.api.test.application;

import edu.kit.ipd.pp.joframes.api.test.framework.AAEventListener;
import edu.kit.ipd.pp.joframes.api.test.framework.ConcreteFrameworkCEventListener;

/**
 * Implementation of the AAEventListener.
 *
 * @author Martin Armbruster
 */
public class AAEventListenerImpl implements AAEventListener {
	@Override
	public void handle() {
		System.out.println("AAEventListenerImpl handle()");
	}

	@Override
	public void handleAA() {
		System.out.println("AAEventListenerImpl handleAA()");
		new ConcreteFrameworkCEventListener();
	}
}
