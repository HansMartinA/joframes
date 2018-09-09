package edu.kit.ipd.pp.joframes.api.test.application;

import edu.kit.ipd.pp.joframes.api.test.framework.SubRandom;

/**
 * Application subclass of SubRandom.
 *
 * @author Martin Armbruster
 */
public class AppSubRandom extends SubRandom {
	@Override
	public void doSomething() {
		super.bar();
		System.out.println("AppSubRandom doSomething()");
		new AAEventListenerImpl();
		new AEventListenerImpl("Hello");
		new BEventListenerImpl();
		new AppRandom();
	}
}
