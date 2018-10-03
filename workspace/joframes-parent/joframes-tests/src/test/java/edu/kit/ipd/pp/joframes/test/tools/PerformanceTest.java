package edu.kit.ipd.pp.joframes.test.tools;

import edu.kit.ipd.pp.joframes.test.tools.AnalysisApplicator.JoanaProfiles;
import edu.kit.ipd.pp.joframes.test.tools.AnalysisApplicator.SupportedFrameworks;
import org.junit.Test;

/**
 * Class that tests the performance of JoFrames in combination with Joana for real-world applications.
 *
 * @author Martin Armbruster
 */
public final class PerformanceTest extends BasicTest {
	/**
	 * Tests the performance of JGit.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testJGit() throws Exception {
		System.out.println("Testing of JGit (Servlets)");
		anaApp.applyAnalysis(SupportedFrameworks.SERVLET, new String[] {
				"target/JavaEWAH-1.1.6.jar",
				"target/jsch-0.1.54.jar",
				"target/jzlib-1.1.1.jar",
				"target/org.eclipse.jgit.http.server-5.1.1.201809181055-r.jar",
				"target/org.eclipse.jgit-5.1.1.201809181055-r.jar",
				"target/slf4j-api-1.7.2.jar"},
				null, "jgit-instrumented.jar", JoanaProfiles.MODERATE);
		System.out.println();
	}

	@Override
	SupportedFrameworks getFramework() {
		return null;
	}

	@Override
	void annotateDefaultSource() {
	}

	@Override
	void annotateDefaultSink() {
	}
}
