package edu.kit.ipd.pp.joframes.test.servlet.conf;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * A ServletContextListener that sets an secret attribute when the ServletContext is initialized.
 *
 * @author Martin Armbruster
 */
@WebListener
public final class ConfContextListener implements ServletContextListener {
	/**
	 * Stores the attribute name.
	 */
	public static final String ATTRIBUTE_NAME = "edu.kit.ipd.pp.joframes.test.servlet.conf.secret_attribute_name";
	/**
	 * Stores the actual secret attribute value.
	 */
	private String secret;

	@Override
	public void contextInitialized(final ServletContextEvent event) {
		secret = "secret";
		event.getServletContext().setAttribute(ATTRIBUTE_NAME, secret);
	}
}
