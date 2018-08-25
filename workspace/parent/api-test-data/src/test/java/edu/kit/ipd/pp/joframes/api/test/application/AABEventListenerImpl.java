package edu.kit.ipd.pp.joframes.api.test.application;

import edu.kit.ipd.pp.joframes.api.test.framework.AAEventListener;
import edu.kit.ipd.pp.joframes.api.test.framework.BEventListener;

/**
 * Implementation of the AAEventListener and BEventListener.
 * 
 * @author Martin Armbruster
 */
public class AABEventListenerImpl implements AAEventListener, BEventListener {
	@Override
	public void handle() {
		System.out.println("AABEventListenerImpl handle()");
	}
	
	@Override
	public void handleAA() {
		System.out.println("AABEventListenerImpl handleAA()");
	}
}
