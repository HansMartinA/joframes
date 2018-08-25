package edu.kit.ipd.pp.joframes.api.test.application;

import edu.kit.ipd.pp.joframes.api.test.framework.A;

/**
 * Represents an application class controlling as framework subclass the framework's life cycle.
 * 
 * @author Martin Armbruster
 */
public class B extends A {
	@Override
	public void start() {
		System.out.println("B start");
		new SubBlockA();
		new SubBlockB();
		new SubBlockB();
		new SubBlockC();
		new SubBlockC();
		new SubBlockC();
		new AppSubRandom();
	}
	
	@Override
	public void destroy() {
		super.destroy();
		System.out.println("B destroy()");
	}
}
