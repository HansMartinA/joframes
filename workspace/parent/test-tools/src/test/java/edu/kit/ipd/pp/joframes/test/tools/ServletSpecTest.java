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
