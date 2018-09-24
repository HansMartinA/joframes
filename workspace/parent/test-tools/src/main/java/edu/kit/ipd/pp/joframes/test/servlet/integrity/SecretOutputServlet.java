package edu.kit.ipd.pp.joframes.test.servlet.integrity;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A Servlet that makes a secret output.
 *
 * @author Martin Armbruster
 */
public final class SecretOutputServlet extends HttpServlet {
	/**
	 * Stores some input which is considered as private.
	 */
	protected static String input;

	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
		PrintWriter writer = resp.getWriter();
		writer.println(input);
	}
}
