package edu.kit.ipd.pp.joframes.test.servlet.ip;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A Servlet that collects the IP addresses of all requests and puts them out.
 *
 * @author Martin Armbruster
 */
public final class IPServlet extends HttpServlet {
	/**
	 * Stores a StringBuilder used to store all IP addresses.
	 */
	private StringBuilder builder = new StringBuilder();

	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
		String ip = req.getRemoteAddr();
		builder.append(ip);
	}

	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
		PrintWriter writer = resp.getWriter();
		writer.println(builder.toString());
	}
}
