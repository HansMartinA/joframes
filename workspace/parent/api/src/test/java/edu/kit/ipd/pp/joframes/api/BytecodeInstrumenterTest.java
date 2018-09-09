package edu.kit.ipd.pp.joframes.api;

import com.ibm.wala.shrikeBT.analysis.Analyzer.FailureException;
import com.ibm.wala.shrikeBT.analysis.Verifier;
import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;
import com.ibm.wala.shrikeBT.shrikeCT.OfflineInstrumenter;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import edu.kit.ipd.pp.joframes.api.exceptions.ClassHierarchyCreationException;
import edu.kit.ipd.pp.joframes.api.exceptions.InstrumenterException;
import edu.kit.ipd.pp.joframes.api.exceptions.ParseException;
import edu.kit.ipd.pp.joframes.ast.base.Framework;
import java.io.File;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test class for the BytecodeInstrumenter.
 *
 * @author Martin Armbruster
 */
public class BytecodeInstrumenterTest {
	/**
	 * Counter for methods.
	 */
	private int methodCounter;

	/**
	 * Tests the bytecode instrumentation with the test framework and application.
	 *
	 * @throws ParseException when parsing the framework specification fails.
	 * @throws ClassHierarchyCreationException when the creation of the class hierarchy fails.
	 * @throws InstrumenterException when instrumentation of the bytecode fails.
	 */
	@Test
	public void testBytecodeInstrumentationOfTestSpec() throws ParseException, ClassHierarchyCreationException,
		InstrumenterException {
		instrumentBytecode(TestConstants.TEST_SPEC_PATH, new String[] {TestConstants.TEST_FRAMEWORK_JAR_PATH},
				new String[] {TestConstants.TEST_APPLICATION_JAR_PATH},
				new String[] {TestConstants.TEST_APPLICATION_JAR_PATH}, TestConstants.OUTPUT_JAR);
		assertTrue(new File(TestConstants.OUTPUT_JAR).exists());
		try {
			OfflineInstrumenter offInstr = new OfflineInstrumenter();
			offInstr.addInputJar(new File(TestConstants.OUTPUT_JAR));
			// assertEquals(15, offInstr.getNumInputClasses());
			offInstr.beginTraversal();
			for (int i = 0; i < offInstr.getNumInputClasses(); i++) {
				ClassInstrumenter clInstr = offInstr.nextClass();
				if (clInstr == null) {
					continue;
				}
				if (clInstr.getInputName().endsWith(TestConstants.CLASS_NAME_ARTIFICIAL_CLASS)) {
					methodCounter = 0;
					clInstr.visitMethods(data -> {
						methodCounter++;
						assertTrue(data.getInstructions().length > 1);
					});
					assertEquals(8, methodCounter);
				} else if (clInstr.getInputName().endsWith(TestConstants.CLASS_NAME_WORKING_WORKER)) {
					clInstr.visitMethods(data -> {
						if (data.getName().equals("run")) {
							assertTrue(data.getInstructions().length > 1);
						}
					});
				} else {
					assertTrue("Unexpected class found: " + clInstr.getInputName(),
						clInstr.getInputName().endsWith(TestConstants.CLASS_NAME_INSTANCE_COLLECTOR)
						|| clInstr.getInputName().endsWith(TestConstants.CLASS_NAME_B2)
						|| clInstr.getInputName().endsWith(TestConstants.CLASS_NAME_B)
						|| clInstr.getInputName().endsWith(TestConstants.CLASS_NAME_A_EVENT_LISTENER_IMPL)
						|| clInstr.getInputName().endsWith(TestConstants.CLASS_NAME_AA_EVENT_LISTENER_IMPL)
						|| clInstr.getInputName().endsWith(TestConstants.CLASS_NAME_AAB_EVENT_LISTENER_IMPL)
						|| clInstr.getInputName().endsWith(TestConstants.CLASS_NAME_B_EVENT_LISTENER_IMPL)
						|| clInstr.getInputName().endsWith(TestConstants.CLASS_NAME_APP_RANDOM)
						|| clInstr.getInputName().endsWith(TestConstants.CLASS_NAME_APP_SUB_RANDOM)
						|| clInstr.getInputName().endsWith(TestConstants.CLASS_NAME_SUB_BLOCK_A)
						|| clInstr.getInputName().endsWith(TestConstants.CLASS_NAME_SUB_BLOCK_B)
						|| clInstr.getInputName().endsWith(TestConstants.CLASS_NAME_SUB_BLOCK_C)
						|| clInstr.getInputName().endsWith(TestConstants.CLASS_NAME_SUB_BLOCK_C_INNER_CLASS)
						|| clInstr.getInputName().endsWith(TestConstants.CLASS_NAME_PACKAGE_INFO)
						|| clInstr.getInputName().endsWith(TestConstants.CLASS_NAME_PACKAGE_INFO_EXTERNAL));
				}
			}
			offInstr.beginTraversal();
			for (int i = 0; i < offInstr.getNumInputClasses(); i++) {
				ClassInstrumenter clInstr = offInstr.nextClass();
				if (clInstr == null) {
					continue;
				}
				clInstr.visitMethods(data -> {
					Verifier v = new Verifier(data);
					try {
						v.verify();
					} catch (FailureException e) {
						fail("Could not verify a method: " + data.getName() + data.getSignature() + "("
								+ clInstr.getInputName() + "). Cause: " + e.getMessage());
					}
				});
			}
		} catch (IOException e) {
			fail("Cannot read the instrumented application jar.");
		} catch (InvalidClassFileException e) {
			fail("An invalid class was read: " + e.getMessage());
		}
	}

	/**
	 * Instruments the bytecode for a framework.
	 *
	 * @param frameworkSpecification path to the framework specification.
	 * @param frameworkJars contains all paths to jar files with framework classes.
	 * @param applicationJars contains all paths to jar files with application classes.
	 * @param applicationJarsForInstrumentation contains all paths to jar files with application classes. They will be
	 *                                          used for the actual instrumentation.
	 * @param output path of the output jar file for the BytecodeInstrumenter.
	 * @throws ParseException when parsing of the framework specification fails.
	 * @throws ClassHierarchyCreationException when the creation of the class hierarchy fails.
	 * @throws InstrumenterException when instrumenting the bytecode fails.
	 */
	private void instrumentBytecode(final String frameworkSpecification, final String[] frameworkJars,
			final String[] applicationJars, final String[] applicationJarsForInstrumentation,
			final String output) throws ParseException, ClassHierarchyCreationException, InstrumenterException {
		FrameworkSpecificationParser parser = new FrameworkSpecificationParser();
		Framework framework = parser.parse(frameworkSpecification);
		ClassHierarchyAnalyzer analyzer = new ClassHierarchyAnalyzer();
		FrameworkWrapper wrapper = analyzer.analyzeClassHierarchy(framework, frameworkJars, applicationJars);
		BytecodeInstrumenter instrumenter = new BytecodeInstrumenter();
		instrumenter.instrumentBytecode(wrapper, applicationJarsForInstrumentation, output);
	}

	/**
	 * Tests the BytecodeInstrumenter with a non-existing application jar file.
	 *
	 * @throws ParseException when parsing of the framework specification fails.
	 * @throws ClassHierarchyCreationException when the creation of the class hierarchy fails.
	 * @throws InstrumenterException when instrumentation of the bytecode fails.
	 */
	@Test(expected = InstrumenterException.class)
	public void testNonExistentApplicationJar() throws ParseException, ClassHierarchyCreationException,
		InstrumenterException {
		instrumentBytecode(TestConstants.TEST_SPEC_PATH, new String[] {TestConstants.TEST_FRAMEWORK_JAR_PATH},
				new String[] {TestConstants.TEST_APPLICATION_JAR_PATH}, new String[] {TestConstants.NON_EXISTING_JAR},
				"A.jar");
	}

	/**
	 * Tests the BytecodeInstrumenter with an illegal output jar file.
	 *
	 * @throws ParseException when parsing the framework specification fails.
	 * @throws ClassHierarchyCreationException when the creation of the class hierarchy fails.
	 * @throws InstrumenterException when instrumentation of the bytecode fails.
	 */
	@Test(expected = InstrumenterException.class)
	public void testIllegalOutputJar() throws ParseException, ClassHierarchyCreationException, InstrumenterException {
		instrumentBytecode(TestConstants.TEST_SPEC_PATH, new String[] {TestConstants.TEST_FRAMEWORK_JAR_PATH},
				new String[] {TestConstants.TEST_APPLICATION_JAR_PATH},
				new String[] {TestConstants.TEST_APPLICATION_JAR_PATH}, "");
	}
}
