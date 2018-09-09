package edu.kit.ipd.pp.joframes.api.test.framework;

/**
 * A concrete framework implementation of the CEventListener.
 *
 * @author Martin Armbruster
 */
public class ConcreteFrameworkCEventListener implements CEventListener {
	@Override
	public void doSomething() {
		System.out.println("ConcreteFrameworkCEventListener doSomething()");
		doSomething("This output should never show up alone (ConcreteFrameworkCEventListener doSomething()).");
	}

	/**
	 * Private method that does something. That should never appear somewhere.
	 *
	 * @param s input string that will be put out.
	 */
	private void doSomething(final String s) {
		System.out.println(s);
	}
}
