package edu.kit.ipd.pp.joframes.api.test.application;

import edu.kit.ipd.pp.joframes.api.test.framework.A2;

/**
 * Implementation of A2.
 *
 * @author Martin Armbruster
 */
public class B2 implements A2 {
	/**
	 * Creates a new instance.
	 *
	 * @param i an integer.
	 * @param s a string.
	 */
	public B2(final int i, final String s) {
		System.out.println(s + " " + i);
		System.out.println("B2 <init>(ILjava/lang/String;)V");
	}

	@Override
	public void initialize(final String s) {
		System.out.println("B2 initialize(Ljava/lang/String;)V");
		System.out.println(s);
	}

	@Override
	public void run() {
		System.out.println("B2 run()");
	}

	@Override
	public void stop() {
		System.out.println("B2 stop()");
	}
}
