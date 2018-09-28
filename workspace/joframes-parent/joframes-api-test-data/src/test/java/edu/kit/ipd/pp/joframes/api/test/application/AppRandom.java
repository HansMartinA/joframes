package edu.kit.ipd.pp.joframes.api.test.application;

import edu.kit.ipd.pp.joframes.api.test.framework.Random;

/**
 * Application implementation of the random class.
 *
 * @author Martin Armbruster
 */
public class AppRandom extends Random {
	@Override
	public void doSomething() {
		System.out.println("AppRandom doSomething()");
		new AABEventListenerImpl();
	}
}
