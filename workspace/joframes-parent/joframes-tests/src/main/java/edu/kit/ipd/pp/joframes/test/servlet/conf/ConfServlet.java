package edu.kit.ipd.pp.joframes.test.servlet.conf;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A Servlet that puts an attribute out - exactly the attribute set in the ConfContextListener containing a secret
 * value.
 *
 * @author Martin Armbruster
 */
public final class ConfServlet extends HttpServlet {
	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
		PrintWriter writer = resp.getWriter();
		writer.println(getServletContext().getAttribute(ConfContextListener.ATTRIBUTE_NAME));
	}
}
