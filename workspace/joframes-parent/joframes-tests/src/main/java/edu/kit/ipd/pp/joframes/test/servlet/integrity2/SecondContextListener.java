package edu.kit.ipd.pp.joframes.test.servlet.integrity2;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * A further ServletContextListener that secretly prints out the hidden attribute.
 *
 * @author Martin Armbruster
 */
@WebListener
public final class SecondContextListener implements ServletContextListener {
	@Override
	public void contextDestroyed(final ServletContextEvent event) {
		System.out.println(event.getServletContext().getAttribute(FirstContextListener.ATTRIBUTE_NAME));
	}
}
