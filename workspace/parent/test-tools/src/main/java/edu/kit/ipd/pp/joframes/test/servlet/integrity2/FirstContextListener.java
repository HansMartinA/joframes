package edu.kit.ipd.pp.joframes.test.servlet.integrity2;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * A ServletContextListener which sets a attribute that is considered to be hidden by this listener.
 *
 * @author Martin Armbruster
 */
@WebListener
public final class FirstContextListener implements ServletContextListener {
	/**
	 * Stores the name of the attribute.
	 */
	public static final String ATTRIBUTE_NAME = "edu.kit.ipd.pp.joframes.test.servlet.integrity2.attribute";
	/**
	 * Stores the secret value of the attribute.
	 */
	private String secret = "secret";

	@Override
	public void contextInitialized(final ServletContextEvent event) {
		event.getServletContext().setAttribute(ATTRIBUTE_NAME, secret);
	}
}
