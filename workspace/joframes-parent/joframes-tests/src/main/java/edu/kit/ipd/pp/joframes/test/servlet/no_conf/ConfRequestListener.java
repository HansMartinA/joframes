package edu.kit.ipd.pp.joframes.test.servlet.no_conf;

import edu.kit.ipd.pp.joframes.test.servlet.conf.ConfContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;

/**
 * A ServletRequestListener that overrides the attribute with a secret value every time before the request is delivered
 * to the Servlet. Therefore, the attribute contains only a non-secret value.
 *
 * @author Martin Armbruster
 */
@WebListener
public final class ConfRequestListener implements ServletRequestListener {
	@Override
	public void requestInitialized(final ServletRequestEvent event) {
		event.getServletContext().setAttribute(ConfContextListener.ATTRIBUTE_NAME, "no secret");
	}
}
