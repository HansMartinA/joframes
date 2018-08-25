package edu.kit.ipd.pp.joframes.api.test.application;

import edu.kit.ipd.pp.joframes.api.test.framework.BlockC;

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
	}
}
