package edu.kit.ipd.pp.joframes.test.tools;

import edu.kit.ipd.pp.joframes.test.tools.AnalysisApplicator.JoanaProfiles;
import edu.kit.ipd.pp.joframes.test.tools.AnalysisApplicator.SupportedFrameworks;
import edu.kit.joana.api.lattice.BuiltinLattices;
import org.junit.Test;

/**
 * Test class for Servlets.
 *
 * @author Martin Armbruster
 */
public class ServletSpecTest extends BasicTest {
	/**
	 * Tests the Aliasing1 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testAliasing1() throws Exception {
		makeAndPrintResults("aliasing1.jar", "al1.jar", JoanaProfiles.HIGH_PRECISION, 1, true, true);
	}

	/**
	 * Tests the Aliasing2 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testAliasing2() throws Exception {
		makeAndPrintResults("aliasing2.jar", "al2.jar", JoanaProfiles.HIGH_PRECISION, 0, true, true);
	}

	/**
	 * Tests the Aliasing5 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testAliasing5() throws Exception {
		makeAndPrintResults("aliasing5.jar", "al5.jar", JoanaProfiles.HIGH_PRECISION, 1, true, true);
	}

	/**
	 * Tests the Aliasing6 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testAliasing6() throws Exception {
		anaApp.addSource("org.apache.catalina.connector.Request.getParameterValues(Ljava/lang/String;)"
				+ "[Ljava/lang/String;->exit", BuiltinLattices.STD_SECLEVEL_HIGH);
		anaApp.addSink("java.io.PrintWriter.println(Ljava/lang/Object;)V->p1", BuiltinLattices.STD_SECLEVEL_LOW);
		makeAndPrintResults("aliasing6.jar", "al6.jar", JoanaProfiles.HIGH_PRECISION, 1, false, false);
	}

	/**
	 * Tests the Arrays2 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testArrays2() throws Exception {
		makeAndPrintResults("arrays2.jar", "ar2.jar", JoanaProfiles.HIGH_PRECISION, 1, true, true);
	}

	/**
	 * Tests the Arrays4 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testArrays4() throws Exception {
		makeAndPrintResults("arrays4.jar", "ar4.jar", JoanaProfiles.HIGH_PRECISION, 1, true, true);
	}

	/**
	 * Tests the Arrays5 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testArrays5() throws Exception {
		makeAndPrintResults("arrays5.jar", "ar5.jar", JoanaProfiles.HIGH_PRECISION, 0, true, true);
	}

	/**
	 * Tests the Arrays10 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testArrays10() throws Exception {
		makeAndPrintResults("arrays10.jar", "ar10.jar", JoanaProfiles.HIGH_PRECISION, 1, true, true);
	}

	/**
	 * Tests the Collections3 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testCollections3() throws Exception {
		makeAndPrintResults("collections3.jar", "coll3.jar", JoanaProfiles.HIGH_PRECISION, 0, true, true);
	}

	/**
	 * Tests the Collections6 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testCollections6() throws Exception {
		makeAndPrintResults("collections6.jar", "coll6.jar", JoanaProfiles.HIGH_PRECISION, 1, true, true);
	}

	/**
	 * Tests the Collections7 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testCollections7() throws Exception {
		makeAndPrintResults("collections7.jar", "coll7.jar", JoanaProfiles.FAST, 1, true, true);
	}

	/**
	 * Tests the Collections8 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testCollections8() throws Exception {
		makeAndPrintResults("collections8.jar", "coll8.jar", JoanaProfiles.HIGH_PRECISION, 0, true, true);
	}

	/**
	 * Tests the Collections11 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testCollections11() throws Exception {
		makeAndPrintResults("collections11.jar", "coll11.jar", JoanaProfiles.HIGH_PRECISION, 1, true, true);
	}

	/**
	 * Tests the Collections13 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testCollections13() throws Exception {
		anaApp.addSink("java.io.PrintWriter.println(Ljava/lang/Object;)V->p1", BuiltinLattices.STD_SECLEVEL_LOW);
		makeAndPrintResults("collections13.jar", "coll13.jar", JoanaProfiles.HIGH_PRECISION, 1, true, false);
	}

	/**
	 * Tests the Collections14 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testCollections14() throws Exception {
		anaApp.addSink("java.io.PrintWriter.println(Ljava/lang/Object;)V->p1", BuiltinLattices.STD_SECLEVEL_LOW);
		makeAndPrintResults("collections14.jar", "coll14.jar", JoanaProfiles.HIGH_PRECISION, 1, true, false);
	}

	/**
	 * Tests the Datastructures2 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testDatastructures2() throws Exception {
		makeAndPrintResults("datastructures2.jar", "data2.jar", JoanaProfiles.HIGH_PRECISION, 1, true, true);
	}

	/**
	 * Tests the Datastructures4 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testDatastructures4() throws Exception {
		makeAndPrintResults("datastructures4.jar", "data4.jar", JoanaProfiles.HIGH_PRECISION, 0, true, true);
	}

	/**
	 * Tests the Datastructures6 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testDatastructures6() throws Exception {
		makeAndPrintResults("datastructures6.jar", "data6.jar", JoanaProfiles.HIGH_PRECISION, 1, true, true);
	}

	/**
	 * Tests the Inter2 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testInter2() throws Exception {
		makeAndPrintResults("inter2.jar", "inter2.jar", JoanaProfiles.HIGH_PRECISION, 1, true, true);
	}

	/**
	 * Tests the Inter3 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testInter3() throws Exception {
		makeAndPrintResults("inter3.jar", "inter3.jar", JoanaProfiles.HIGH_PRECISION, 1, true, true);
	}

	/**
	 * Tests the Inter4 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testInter4() throws Exception {
		makeAndPrintResults("inter4.jar", "inter4.jar", JoanaProfiles.HIGH_PRECISION, 1, true, true);
	}

	/**
	 * Tests the Inter7 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testInter7() throws Exception {
		makeAndPrintResults("inter7.jar", "inter7.jar", JoanaProfiles.HIGH_PRECISION, 1, true, true);
	}

	/**
	 * Tests the Inter8 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testInter8() throws Exception {
		makeAndPrintResults("inter8.jar", "inter8.jar", JoanaProfiles.HIGH_PRECISION, 1, true, true);
	}

	/**
	 * Tests the Inter11 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testInter11() throws Exception {
		makeAndPrintResults("inter11.jar", "inter11.jar", JoanaProfiles.HIGH_PRECISION, 1, true, true);
	}

	/**
	 * Tests the Inter13 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testInter13() throws Exception {
		makeAndPrintResults("inter13.jar", "inter13.jar", JoanaProfiles.HIGH_PRECISION, 1, true, true);
	}

	/**
	 * Tests the Pred1 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testPred1() throws Exception {
		anaApp.addSink("javax.servlet.http.HttpServlet.service(Ljavax/servlet/ServletRequest;"
				+ "Ljavax/servlet/ServletResponse;)V->p2", BuiltinLattices.STD_SECLEVEL_LOW);
		makeAndPrintResults("pred1.jar", "pred1.jar", JoanaProfiles.HIGH_PRECISION, 0, true, false);
	}

	/**
	 * Tests the Pred3 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testPred3() throws Exception {
		makeAndPrintResults("pred3.jar", "pred3.jar", JoanaProfiles.HIGH_PRECISION, 0, true, true);
	}

	/**
	 * Tests the Pred7 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testPred7() throws Exception {
		makeAndPrintResults("pred7.jar", "pred7.jar", JoanaProfiles.HIGH_PRECISION, 0, true, true);
	}

	/**
	 * Tests the Pred9 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testPred9() throws Exception {
		makeAndPrintResults("pred9.jar", "pred9.jar", JoanaProfiles.HIGH_PRECISION, 1, true, true);
	}

	/**
	 * Tests the Sanitizers2 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testSanitizers2() throws Exception {
		makeAndPrintResults("san2.jar", "san2.jar", JoanaProfiles.HIGH_PRECISION, 1, true, true);
	}

	/**
	 * Tests the Sanitizers4 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testSanitizers4() throws Exception {
		makeAndPrintResults("san4.jar", "san4.jar", JoanaProfiles.HIGH_PRECISION, 1, true, true);
	}

	/**
	 * Tests the Session2 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testSession2() throws Exception {
		anaApp.addSource("org.apache.catalina.connector.Request.getSession()Ljavax/servlet/http/HttpSession;->exit",
				BuiltinLattices.STD_SECLEVEL_HIGH);
		makeAndPrintResults("sess2.jar", "sess2.jar", JoanaProfiles.HIGH_PRECISION, 1, false, true);
	}

	/**
	 * Tests the StrongUpdates3 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testStrongUpdates3() throws Exception {
		makeAndPrintResults("su3.jar", "su3.jar", JoanaProfiles.HIGH_PRECISION, 0, true, true);
	}

	/**
	 * Tests the StrongUpdates4 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testStrongUpdates4() throws Exception {
		makeAndPrintResults("su4.jar", "su4.jar", JoanaProfiles.HIGH_PRECISION, 1, true, true);
		// Here, two different annotations are possible.
		// anaApp.addSource("securibench.micro.strong_updates.StrongUpdates4.name", BuiltinLattices.STD_SECLEVEL_HIGH);
		// makeAndPrintResults("su4.jar", "su4.jar", JoanaProfiles.HIGH_PRECISION, 1, false, true);
	}

	/**
	 * Tests the StrongUpdates5 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testStrongUpdates5() throws Exception {
		makeAndPrintResults("su5.jar", "su5.jar", JoanaProfiles.HIGH_PRECISION, 0, true, true);
	}

	/**
	 * Tests the Basic4 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testBasic4() throws Exception {
		makeAndPrintResults("basic4.jar", "basic4.jar", JoanaProfiles.HIGH_PRECISION, 1, true, true);
	}

	/**
	 * Tests the Basic7 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testBasic7() throws Exception {
		makeAndPrintResults("basic7.jar", "basic7.jar", JoanaProfiles.HIGH_PRECISION, 1, true, true);
	}

	/**
	 * Tests the Basic8 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testBasic8() throws Exception {
		makeAndPrintResults("basic8.jar", "basic8.jar", JoanaProfiles.HIGH_PRECISION, 0, true, true);
	}

	/**
	 * Tests the Basic14 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testBasic14() throws Exception {
		anaApp.addSource("javax.servlet.GenericServlet.getServletConfig()Ljavax/servlet/ServletConfig;->exit",
				BuiltinLattices.STD_SECLEVEL_HIGH);
		makeAndPrintResults("basic14.jar", "basic14.jar", JoanaProfiles.HIGH_PRECISION, 1, false, true);
	}

	/**
	 * Tests the Basic23 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testBasic23() throws Exception {
		anaApp.addSink("java.io.FileWriter.<init>(Ljava/lang/String;)V", BuiltinLattices.STD_SECLEVEL_LOW);
		anaApp.addSink("java.io.FileInputStream.<init>(Ljava/lang/String;)V", BuiltinLattices.STD_SECLEVEL_LOW);
		makeAndPrintResults("basic23.jar", "basic23.jar", JoanaProfiles.HIGH_PRECISION, 2, true, false);
	}

	/**
	 * Tests the Basic28 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testBasic28() throws Exception {
		makeAndPrintResults("basic28.jar", "basic28.jar", JoanaProfiles.HIGH_PRECISION, 1, true, true);
	}

	/**
	 * Tests the Basic31 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testBasic31() throws Exception {
		anaApp.addSource("org.apache.catalina.connector.Request.getCookies()[Ljavax/servlet/http/Cookie;",
				BuiltinLattices.STD_SECLEVEL_HIGH);
		makeAndPrintResults("basic31.jar", "basic31.jar", JoanaProfiles.HIGH_PRECISION, 1, false, true);
	}

	/**
	 * Tests the Basic35 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testBasic35() throws Exception {
		anaApp.addSource("javax.servlet.http.HttpServlet.service(Ljavax/servlet/ServletRequest;"
				+ "Ljavax/servlet/ServletResponse;)V->p1", BuiltinLattices.STD_SECLEVEL_HIGH);
		makeAndPrintResults("basic35.jar", "basic35.jar", JoanaProfiles.HIGH_PRECISION, 1, false, true);
	}

	/**
	 * Tests the Servlet test case which violates confidentiality by putting out a secret attribute.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testConf1() throws Exception {
		anaApp.addSource("edu.kit.ipd.pp.joframes.test.servlet.conf.ConfContextListener.secret",
				BuiltinLattices.STD_SECLEVEL_HIGH);
		anaApp.addSink("java.io.PrintWriter.println(Ljava/lang/Object;)V->p1", BuiltinLattices.STD_SECLEVEL_LOW);
		makeAndPrintResults("conf1.jar", "conf1.jar", JoanaProfiles.HIGH_PRECISION, 1, false, false);
	}

	/**
	 * Tests the Servlet test case which does not violate confidentiality by extending the test case out of testConf1
	 * and overriding the secret attribute.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testConf2() throws Exception {
		anaApp.addSource("edu.kit.ipd.pp.joframes.test.servlet.conf.ConfContextListener.secret",
				BuiltinLattices.STD_SECLEVEL_HIGH);
		anaApp.addSink("java.io.PrintWriter.println(Ljava/lang/Object;)V->p1", BuiltinLattices.STD_SECLEVEL_LOW);
		makeAndPrintResults("conf2.jar", "conf2.jar", JoanaProfiles.HIGH_PRECISION, 0, false, false);
	}

	/**
	 * Tests the Servlet test case which violates integrity by putting out a secret value which can be written
	 * publicly.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testInt1() throws Exception {
		anaApp.addSource("edu.kit.ipd.pp.joframes.test.servlet.integrity.SecretOutputServlet.input",
				BuiltinLattices.STD_SECLEVEL_LOW);
		makeAndPrintResults("int1.jar", "int1.jar", JoanaProfiles.HIGH_PRECISION, 1, true, true);
	}

	/**
	 * Tests the second Servlet test case which violates integrity where a Servlet can influence a flow of a hidden
	 * attribute.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testInt2() throws Exception {
		anaApp.addSource("edu.kit.ipd.pp.joframes.test.servlet.integrity2.FirstContextListener.secret",
				BuiltinLattices.STD_SECLEVEL_LOW);
		anaApp.addSink("java.io.PrintStream.println(Ljava/lang/Object;)V->p1",
				BuiltinLattices.STD_SECLEVEL_LOW);
		makeAndPrintResults("int2.jar", "int2.jar", JoanaProfiles.HIGH_PRECISION, 1, true, false);
	}

	@Override
	SupportedFrameworks getFramework() {
		return SupportedFrameworks.SERVLET;
	}

	@Override
	void annotateDefaultSource() {
		anaApp.addSource(
				"org.apache.catalina.connector.Request.getParameter(Ljava/lang/String;)Ljava/lang/String;->exit",
				BuiltinLattices.STD_SECLEVEL_HIGH);
	}

	@Override
	void annotateDefaultSink() {
		anaApp.addSink("java.io.PrintWriter.println(Ljava/lang/String;)V->p1", BuiltinLattices.STD_SECLEVEL_LOW);
	}
}
