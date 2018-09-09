package edu.kit.ipd.pp.joframes.api.test.application;

import edu.kit.ipd.pp.joframes.api.test.framework.BlockC;
import edu.kit.ipd.pp.joframes.api.test.framework.CEventListener;

/**
 * Subclass of BlockC.
 *
 * @author Martin Armbruster
 */
public class SubBlockC extends BlockC {
	@Override
	public void biz() {
		super.biz();
		System.out.println("SubBlockC biz()");
		AAEventListenerImpl impl = new AAEventListenerImpl();
		impl.handleAA();
		new CEventListener() {
			@Override
			public void doSomething() {
				System.out.println("Anonymous inner class: subclass of CEventListener, doSomething().");
			}
		};
	}
}
