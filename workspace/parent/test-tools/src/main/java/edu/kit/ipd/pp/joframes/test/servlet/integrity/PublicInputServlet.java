package edu.kit.ipd.pp.joframes.test.servlet.integrity;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A Servlet that is open for public inputs.
 *
 * @author Martin Armbruster
 */
public final class PublicInputServlet extends HttpServlet {
	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) {
		SecretOutputServlet.input = req.getParameter("test");
	}
}
