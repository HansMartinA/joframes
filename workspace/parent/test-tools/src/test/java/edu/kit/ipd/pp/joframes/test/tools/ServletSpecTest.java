package edu.kit.ipd.pp.joframes.test.tools;

import edu.kit.ipd.pp.joframes.api.APIConstants;
import edu.kit.ipd.pp.joframes.test.tools.AnalysisApplicator.JoanaProfiles;
import edu.kit.ipd.pp.joframes.test.tools.AnalysisApplicator.SupportedFrameworks;
import edu.kit.joana.api.sdg.SDGProgramPart;
import edu.kit.joana.ifc.sdg.core.conc.DataConflict;
import edu.kit.joana.ifc.sdg.core.conc.OrderConflict;
import edu.kit.joana.ifc.sdg.core.violations.IBinaryViolation;
import edu.kit.joana.ifc.sdg.core.violations.IIllegalFlow;
import edu.kit.joana.ifc.sdg.core.violations.IUnaryViolation;
import edu.kit.joana.ifc.sdg.core.violations.IViolation;
import edu.kit.joana.ifc.sdg.core.violations.IViolationVisitor;
import java.io.File;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * Test class for Servlets.
 *
 * @author Martin Armbruster
 */
public class ServletSpecTest {
	/**
	 * Stores the prefix for the jar files containing the classes for a test case.
	 */
	private static final String JAR_PREFIX = "test-tools-0.2-";
	/**
	 * Stores the applicator for the analysis.
	 */
	private AnalysisApplicator anaApp;

	/**
	 * Set ups the system for the tests.
	 */
	@BeforeClass
	public static void setUpSystem() {
		System.setProperty(APIConstants.TEST_SYSTEM_PROPERTY, "true");
	}

	/**
	 * Cleans the system up after the tests.
	 */
	@AfterClass
	public static void tearDownSystem() {
		System.clearProperty(APIConstants.TEST_SYSTEM_PROPERTY);
	}

	/**
	 * Sets everything up for a test case.
	 */
	@Before
	public void setUp() {
		System.setProperty("edu.kit.ipd.pp.joframes.test.active", "true");
		anaApp = new AnalysisApplicator();
	}

	/**
	 * Tests the Aliasing1 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testAliasing1() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("aliasing1.jar", "al1.jar", JoanaProfiles.HIGH_PRECISION, 1);
	}

	/**
	 * Tests the Aliasing2 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testAliasing2() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("aliasing2.jar", "al2.jar", JoanaProfiles.HIGH_PRECISION, 0);
	}

	/**
	 * Tests the Aliasing5 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testAliasing5() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("aliasing5.jar", "al5.jar", JoanaProfiles.HIGH_PRECISION, 1);
	}

	/**
	 * Tests the Aliasing6 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testAliasing6() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("aliasing6.jar", "al6.jar", JoanaProfiles.HIGH_PRECISION, 7);
	}

	/**
	 * Tests the Arrays2 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testArrays2() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("arrays2.jar", "ar2.jar", JoanaProfiles.HIGH_PRECISION, 1);
	}

	/**
	 * Tests the Arrays4 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testArrays4() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("arrays4.jar", "ar4.jar", JoanaProfiles.HIGH_PRECISION, 1);
	}

	/**
	 * Tests the Arrays5 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testArrays5() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("arrays5.jar", "ar5.jar", JoanaProfiles.HIGH_PRECISION, 0);
	}

	/**
	 * Tests the Arrays10 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testArrays10() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("arrays10.jar", "ar10.jar", JoanaProfiles.HIGH_PRECISION, 1);
	}

	/**
	 * Tests the Collections3 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testCollections3() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("collections3.jar", "coll3.jar", JoanaProfiles.HIGH_PRECISION, 2);
	}

	/**
	 * Tests the Collections6 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testCollections6() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("collections6.jar", "coll6.jar", JoanaProfiles.HIGH_PRECISION, 1);
	}

	/**
	 * Tests the Collections7 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testCollections7() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("collections7.jar", "coll7.jar", JoanaProfiles.HIGH_PRECISION, 1);
	}

	/**
	 * Tests the Collections8 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testCollections8() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("collections8.jar", "coll8.jar", JoanaProfiles.HIGH_PRECISION, 1);
	}

	/**
	 * Tests the Collections11 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testCollections11() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("collections11.jar", "coll11.jar", JoanaProfiles.HIGH_PRECISION, 1);
	}

	/**
	 * Tests the Collections13 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testCollections13() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("collections13.jar", "coll13.jar", JoanaProfiles.HIGH_PRECISION, 1);
	}

	/**
	 * Tests the Collections14 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testCollections14() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("collections14.jar", "coll14.jar", JoanaProfiles.HIGH_PRECISION, 1);
	}

	/**
	 * Tests the Datastructures2 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testDatastructures2() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("datastructures2.jar", "data2.jar", JoanaProfiles.HIGH_PRECISION, 1);
	}

	/**
	 * Tests the Datastructures4 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testDatastructures4() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("datastructures4.jar", "data4.jar", JoanaProfiles.HIGH_PRECISION, 0);
	}

	/**
	 * Tests the Datastructures6 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testDatastructures6() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("datastructures6.jar", "data6.jar", JoanaProfiles.HIGH_PRECISION, 1);
	}

	/**
	 * Tests the Inter2 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testInter2() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("inter2.jar", "inter2.jar", JoanaProfiles.HIGH_PRECISION, 2);
	}

	/**
	 * Tests the Inter3 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testInter3() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("inter3.jar", "inter3.jar", JoanaProfiles.HIGH_PRECISION, 1);
	}

	/**
	 * Tests the Inter4 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testInter4() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("inter4.jar", "inter4.jar", JoanaProfiles.HIGH_PRECISION, 1);
	}

	/**
	 * Tests the Inter7 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testInter7() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("inter7.jar", "inter7.jar", JoanaProfiles.HIGH_PRECISION, 1);
	}

	/**
	 * Tests the Inter8 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testInter8() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("inter8.jar", "inter8.jar", JoanaProfiles.HIGH_PRECISION, 1);
	}

	/**
	 * Tests the Inter11 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testInter11() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("inter11.jar", "inter11.jar", JoanaProfiles.HIGH_PRECISION, 1);
	}

	/**
	 * Tests the Inter13 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testInter13() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("inter13.jar", "inter13.jar", JoanaProfiles.HIGH_PRECISION, 1);
	}

	/**
	 * Tests the Pred1 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testPred1() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("pred1.jar", "pred1.jar", JoanaProfiles.HIGH_PRECISION, 0);
	}

	/**
	 * Tests the Pred3 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testPred3() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("pred3.jar", "pred3.jar", JoanaProfiles.HIGH_PRECISION, 0);
	}

	/**
	 * Tests the Pred7 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testPred7() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("pred7.jar", "pred7.jar", JoanaProfiles.HIGH_PRECISION, 0);
	}

	/**
	 * Tests the Pred9 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testPred9() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("pred9.jar", "pred9.jar", JoanaProfiles.HIGH_PRECISION, 1);
	}

	/**
	 * Tests the Sanitizers2 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testSanitizers2() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("sanitizers2.jar", "san2.jar", JoanaProfiles.HIGH_PRECISION, 1);
	}

	/**
	 * Tests the Sanitizers4 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testSanitizers4() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("sanitizers4.jar", "san4.jar", JoanaProfiles.HIGH_PRECISION, 2);
	}

	/**
	 * Tests the Session2 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testSession2() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("session2.jar", "sess2.jar", JoanaProfiles.HIGH_PRECISION, 1);
	}

	/**
	 * Tests the StrongUpdates3 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testStrongUpdates3() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("strong-updates3.jar", "su3.jar", JoanaProfiles.HIGH_PRECISION, 0);
	}

	/**
	 * Tests the StrongUpdates4 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testStrongUpdates4() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("strong-updates4.jar", "su4.jar", JoanaProfiles.HIGH_PRECISION, 1);
	}

	/**
	 * Tests the StrongUpdates5 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testStrongUpdates5() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("strong-updates5.jar", "su5.jar", JoanaProfiles.HIGH_PRECISION, 0);
	}

	/**
	 * Tests the Basic4 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testBasic4() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("basic4.jar", "basic4.jar", JoanaProfiles.HIGH_PRECISION, 1);
	}

	/**
	 * Tests the Basic7 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testBasic7() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("basic7.jar", "basic7.jar", JoanaProfiles.HIGH_PRECISION, 1);
	}

	/**
	 * Tests the Basic8 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testBasic8() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("basic8.jar", "basic8.jar", JoanaProfiles.HIGH_PRECISION, 1);
	}

	/**
	 * Tests the Basic14 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testBasic14() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("basic14.jar", "basic14.jar", JoanaProfiles.HIGH_PRECISION, 1);
	}

	/**
	 * Tests the Basic23 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testBasic23() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("basic23.jar", "basic23.jar", JoanaProfiles.HIGH_PRECISION, 3);
	}

	/**
	 * Tests the Basic28 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testBasic28() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("basic28.jar", "basic28.jar", JoanaProfiles.HIGH_PRECISION, 2);
	}

	/**
	 * Tests the Basic31 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testBasic31() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("basic31.jar", "basic31.jar", JoanaProfiles.HIGH_PRECISION, 3);
	}

	/**
	 * Tests the Basic35 class of Securibench Micro.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testBasic35() throws Exception {
		// Annotation of sources and sinks.
		makeAndPrintResults("basic35.jar", "basic35.jar", JoanaProfiles.HIGH_PRECISION, 6);
	}

	/**
	 * Runs the analysis and prints and tests the results.
	 *
	 * @param classifier classifier of the input jar file.
	 * @param output the output jar file.
	 * @param profile profile for Joana.
	 * @param minViolations the number on minimum violations that have to be found.
	 * @throws Exception if something goes wrong.
	 */
	private void makeAndPrintResults(final String classifier, final String output, final JoanaProfiles profile,
			final int minViolations) throws Exception {
		AnalysisApplicator.AAResults result = anaApp.applyAnalysis(SupportedFrameworks.SERVLET, new String[] {"target"
				+ File.separator + JAR_PREFIX + classifier}, null, output, profile);
		System.out.println("Instructions: " + result.getFrameworkAndApplicationInstructionCount() + " + "
				+ result.getAdditionalInstructionsCount());
		System.out.println("Overall time: " + result.getOverallTime() + ", time for [Framework Project]: "
				+ result.getProcessingTime() + ", time for Joana: " + result.getTimeOfJoana());
		System.out.println("Violations: " + result.getViolations().keySet().size());
		for (IViolation<SDGProgramPart> part : result.getViolations().keySet()) {
			System.out.println("Found a violation.");
			part.accept(new IViolationVisitor<SDGProgramPart>() {
				@Override
				public void visitIllegalFlow(final IIllegalFlow<SDGProgramPart> arg0) {
					System.out.println("Illegal flow: " + arg0.getSource() + " to " + arg0.getSink()
						+ " with attacker level " + arg0.getAttackerLevel());
				}

				@Override
				public void visitDataConflict(final DataConflict<SDGProgramPart> arg0) {
					System.out.println("Data conflict: " + arg0.getInfluenced());
				}

				@Override
				public void visitOrderConflict(final OrderConflict<SDGProgramPart> arg0) {
					System.out.println("Order conflict: " + arg0.getConflictEdge().getSource() + " to "
							+ arg0.getConflictEdge().getTarget());
				}

				@Override
				public <L> void visitUnaryViolation(final IUnaryViolation<SDGProgramPart, L> arg0) {
					System.out.println("Unary violation: " + arg0.getNode() + " with " + arg0.getActualLevel()
						+ " instead of " + arg0.getExpectedLevel());
				}

				@Override
				public <L> void visitBinaryViolation(final IBinaryViolation<SDGProgramPart, L> arg0) {
					System.out.println("Binary violation: " + arg0.getInfluencedBy() + " (" + arg0.getAttackerLevel()
						+ ")");
				}
			});
		}
		assertTrue(result.getViolations().keySet().size() >= minViolations);
	}
}
