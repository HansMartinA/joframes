package edu.kit.ipd.pp.joframes.test.servlet.integrity2;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A Servlet that overrides the hidden attribute.
 *
 * @author Martin Armbruster
 */
public final class Int2Servlet extends HttpServlet {
	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) {
		String param = req.getParameter("test");
		getServletContext().setAttribute(FirstContextListener.ATTRIBUTE_NAME, param);
	}
}
