package edu.kit.ipd.pp.joframes.test.tools;

import edu.kit.ipd.pp.joframes.api.APIConstants;
import edu.kit.ipd.pp.joframes.api.logging.Log;
import edu.kit.ipd.pp.joframes.api.logging.Log.LogOptions;
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
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.assertTrue;

/**
 * A super class for test classes.
 *
 * @author Martin Armbruster
 */
public abstract class BasicTest {
	/**
	 * Stores the prefix for the jar files containing the classes for a test case.
	 */
	private static final String JAR_PREFIX = "joframes-tests-1.0-";
	/**
	 * Stores all results.
	 */
	private static ArrayList<AnalysisApplicator.AAResults> results;
	/**
	 * Stores the applicator for the analysis.
	 */
	AnalysisApplicator anaApp;

	/**
	 * Set ups the system for the tests.
	 */
	@BeforeClass
	public static void setUpSystem() {
		System.setProperty(APIConstants.TEST_SYSTEM_PROPERTY, "true");
		Log.setLogOption(LogOptions.DEFAULT_OUT_EXTENDED);
		results = new ArrayList<>();
	}

	/**
	 * Cleans the system up after the tests.
	 */
	@AfterClass
	public static void tearDownSystem() {
		printAllResults();
		System.clearProperty(APIConstants.TEST_SYSTEM_PROPERTY);
	}

	/**
	 * Sets everything up for a test case.
	 */
	@Before
	public void setUp() {
		anaApp = new AnalysisApplicator();
		anaApp.setAnalyzeWithJoana(analyzeWithJoana());
	}

	/**
	 * Returns the framework for the test cases.
	 *
	 * @return the framework.
	 */
	abstract SupportedFrameworks getFramework();

	/**
	 * Returns the class containing the main method.
	 *
	 * @return the main class.
	 */
	String getMainClass() {
		return null;
	}

	/**
	 * Annotates the default source.
	 */
	abstract void annotateDefaultSource();

	/**
	 * Annotates the default sink.
	 */
	abstract void annotateDefaultSink();

	/**
	 * Indicates if the instrumented code should be analyzed with Joana or not.
	 *
	 * @return true if the code should be analyzed with Joana. false otherwise.
	 */
	abstract boolean analyzeWithJoana();

	/**
	 * Runs the analysis and prints and tests the results.
	 *
	 * @param classifier classifier of the input jar file.
	 * @param output the output jar file.
	 * @param profile profile for Joana.
	 * @param minViolations the number on minimum violations that have to be found.
	 * @param useDefaultSource true if the default source annotation should be used. false otherwise.
	 * @param useDefaultSink true if the default sink annotation should be used. false otherwise.
	 * @throws Exception if something goes wrong.
	 */
	void makeAndPrintResults(final String classifier, final String output,
			final JoanaProfiles profile, final int minViolations, final boolean useDefaultSource, final boolean
			useDefaultSink) throws Exception {
		System.out.println("Testing: " + classifier + " of " + getFramework());
		if (useDefaultSource) {
			annotateDefaultSource();
		}
		if (useDefaultSink) {
			annotateDefaultSink();
		}
		AnalysisApplicator.AAResults result = anaApp.applyAnalysis(getFramework(), new String[] {
				"target" + File.separator + JAR_PREFIX + classifier}, getMainClass(), output, profile);
		results.add(result);
		System.out.println("Violations: " + result.getViolations().keySet().size() + ", minimal expected: "
				+ minViolations);
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
		System.out.println();
		assertTrue(result.getViolations().keySet().size() >= minViolations);
	}

	/**
	 * Prints all results at the end of the tests.
	 */
	private static void printAllResults() {
		System.out.println();
		for (AnalysisApplicator.AAResults result : results) {
			System.out.println(result.getTestName() + " " + result.getFrameworkInstructionCount() + " "
				+ result.getApplicationInstructionCount() + " " + result.getAdditionalInstructionsCount() + " "
				+ result.getProcessingTime() + " " + result.getTimeOfJoana() + " " + result.getOverallTime());
		}
		System.out.println();
	}
}
