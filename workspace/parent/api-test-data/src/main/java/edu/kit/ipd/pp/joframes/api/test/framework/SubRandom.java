package edu.kit.ipd.pp.joframes.api.test.framework;

/**
 * Represents a random framework subclass.
 * 
 * @author Martin Armbruster
 */
public class SubRandom extends Random {
	@Override
	public void doSomething() {
		System.out.println("SubRandom doSomething");
	}
}
