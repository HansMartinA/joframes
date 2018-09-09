package edu.kit.ipd.pp.joframes.api.test.framework;

import java.util.EventListener;

/**
 * Represents an EventListener that is worth for other framework classes to implement.
 *
 * @author Martin Armbruster
 */
public interface CEventListener extends EventListener {
	/**
	 * A method that does something.
	 */
	void doSomething();
}
