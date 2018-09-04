package edu.kit.ipd.pp.joframes.api;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import edu.kit.ipd.pp.joframes.api.exceptions.ClassHierarchyCreationException;
import edu.kit.ipd.pp.joframes.api.exceptions.ParseException;
import edu.kit.ipd.pp.joframes.ast.acha.MethodCollector;
import edu.kit.ipd.pp.joframes.ast.ap.Block;
import edu.kit.ipd.pp.joframes.ast.base.AstBaseClass;
import edu.kit.ipd.pp.joframes.ast.base.ExplicitDeclaration;
import edu.kit.ipd.pp.joframes.ast.base.Framework;
import edu.kit.ipd.pp.joframes.ast.base.Method;
import edu.kit.ipd.pp.joframes.ast.base.Rule;
import edu.kit.ipd.pp.joframes.ast.base.StaticMethod;
import edu.kit.ipd.pp.joframes.ast.base.ThreadType;
import edu.kit.ipd.pp.joframes.ast.base.WorkingPhase;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test class for the ClassHierarchyAnalysis class.
 * 
 * @author Martin Armbruster
 */
public class ClassHierarchyAnalysisTest {
	/**
	 * Stores the path to the test specification.
	 */
	private static final String TEST_SPEC_PATH = "src/test/resources/TestSpec.xml";
	/**
	 * Stores the path to the jar file with the test framework.
	 */
	private static final String TEST_FRAMEWORK_JAR_PATH = "src/test/resources/api-test-data-2.0.jar";
	/**
	 * Stores the path to the jar file with the test application.
	 */
	private static final String TEST_APPLICATION_JAR_PATH = "src/test/resources/api-test-data-2.0-tests.jar";
	/**
	 * General package name for the test framework and application.
	 */
	private static final String PACKAGE = "Ledu/kit/ipd/pp/joframes/api/test/";
	/**
	 * Package name of the test framework.
	 */
	private static final String PACKAGE_FRAMEWORK = PACKAGE+"framework/";
	/**
	 * Package name of the test application.
	 */
	private static final String PACKAGE_APPLICATION = PACKAGE+"application/";
	/**
	 * Bytecode name of the A class.
	 */
	private static final String CLASS_A = PACKAGE_FRAMEWORK+"A";
	/**
	 * Bytecode name of the A2 interface.
	 */
	private static final String CLASS_A2 = PACKAGE_FRAMEWORK+"A2";
	/**
	 * Bytecode name of the EventListener interface.
	 */
	private static final String CLASS_EVENT_LISTENER = "Ljava/util/EventListener";
	/**
	 * Bytecode name of the AEventListener interface.
	 */
	private static final String CLASS_A_EVENT_LISTENER = PACKAGE_FRAMEWORK+"AEventListener";
	/**
	 * Bytecode name of the BEventListener interface.
	 */
	private static final String CLASS_B_EVENT_LISTENER = PACKAGE_FRAMEWORK+"BEventListener";
	/**
	 * Bytecode name of the AAEventListener interface.
	 */
	private static final String CLASS_AA_EVENT_LISTENER = PACKAGE_FRAMEWORK+"AAEventListener";
	/**
	 * Bytecode name of the CEventListener interface.
	 */
	private static final String CLASS_C_EVENT_LISTENER = PACKAGE_FRAMEWORK+"CEventListener";
	/**
	 * Bytecode name of the ConcreteFrameworkCEventListener class.
	 */
	private static final String CLASS_CONCRETE_FRAMEWORK_C_EVENT_LISTENER = PACKAGE_FRAMEWORK
			+"ConcreteFrameworkCEventListener";
	/**
	 * Bytecode name of the BlockA class.
	 */
	private static final String CLASS_BLOCK_A = PACKAGE_FRAMEWORK+"BlockA";
	/**
	 * Bytecode name of the BlockB class.
	 */
	private static final String CLASS_BLOCK_B = PACKAGE_FRAMEWORK+"BlockB";
	/**
	 * Bytecode name of the BlockC class.
	 */
	private static final String CLASS_BLOCK_C = PACKAGE_FRAMEWORK+"BlockC";
	/**
	 * Bytecode name of the Random class.
	 */
	private static final String CLASS_RANDOM = PACKAGE_FRAMEWORK+"Random";
	/**
	 * Bytecode name of the SubRandom class.
	 */
	private static final String CLASS_SUB_RANDOM = PACKAGE_FRAMEWORK+"SubRandom";
	/**
	 * Bytecode name of the B class.
	 */
	private static final String CLASS_B = PACKAGE_APPLICATION+"B";
	/**
	 * Bytecode name of the B2 class.
	 */
	private static final String CLASS_B2 = PACKAGE_APPLICATION+"B2";
	/**
	 * Bytecode name for a handle method.
	 */
	private static final String METHOD_HANDLE = "handle()V";
	/**
	 * Bytecode name for a handleAA method.
	 */
	private static final String METHOD_HANDLE_AA = "handleAA()V";
	/**
	 * Bytecode name for a doSomething method.
	 */
	private static final String METHOD_DO_SOMETHING = "doSomething()V";
	
	/**
	 * Stores the parsed framework.
	 */
	private Framework framework;
	/**
	 * Stores the wrapped framework after the ClassHierarchyAnalysis.
	 */
	private FrameworkWrapper wrapper;
	
	/**
	 * Tests the test framework and application.
	 * 
	 * @throws ParseException when parsing of the framework specification fails.
	 * @throws ClassHierarchyCreationException when creation of the class hierarchy fails.
	 */
	@Test
	public void testTestSpec() throws ParseException, ClassHierarchyCreationException {
		analyzeClassHierarchy(TEST_SPEC_PATH, new String[]
				{TEST_FRAMEWORK_JAR_PATH}, new String[]
						{TEST_APPLICATION_JAR_PATH});
		assertEquals(13, wrapper.getFrameworkClasses().size());
		Set<?> set = convertToStringSet(wrapper.getFrameworkClasses());
		assertTrue(set.contains(CLASS_A));
		assertTrue(set.contains(CLASS_A2));
		assertTrue(set.contains(CLASS_A_EVENT_LISTENER));
		assertTrue(set.contains(CLASS_B_EVENT_LISTENER));
		assertTrue(set.contains(CLASS_C_EVENT_LISTENER));
		assertTrue(set.contains(CLASS_AA_EVENT_LISTENER));
		assertTrue(set.contains(CLASS_BLOCK_A));
		assertTrue(set.contains(CLASS_BLOCK_B));
		assertTrue(set.contains(CLASS_BLOCK_C));
		assertTrue(set.contains(CLASS_CONCRETE_FRAMEWORK_C_EVENT_LISTENER));
		assertTrue(set.contains(CLASS_EVENT_LISTENER));
		assertTrue(set.contains(CLASS_RANDOM));
		assertTrue(set.contains(CLASS_SUB_RANDOM));
		for(ExplicitDeclaration declaration : wrapper.getFramework().getStartPhase().getDeclarations()) {
			validateExplicitDeclaration(declaration);
		}
		validateStartPhaseOfTestSpec();
		validateExplicitDeclaration(wrapper.getFramework().getEndPhase().getEnd());
		assertEquals(2, wrapper.getFramework().getWorkingPhases().size());
		WorkingPhase working = wrapper.getFramework().getWorkingPhases().get(0);
		assertEquals(ThreadType.MULTI, working.getThreadType());
		for(Rule r : working.getRules()) {
			if(r.getClass()==MethodCollector.class) {
				MethodCollector c = (MethodCollector)r;
				validateMethodCollectorOfTestSpec(c, true);
			} else {
				fail("First working phase contains an illegal block, regex or supertype rule.");
			}
		}
		working = wrapper.getFramework().getWorkingPhases().get(1);
		assertEquals(ThreadType.SINGLE, working.getThreadType());
		for(Rule r : working.getRules()) {
			if(r.getClass()==MethodCollector.class) {
				MethodCollector c = (MethodCollector)r;
				validateMethodCollectorOfTestSpec(c, false);
			} else if(r.getClass()==Block.class) {
				validateBlock((Block)r);
			} else {
				fail("The second working phase contains an illegal regex or supertype rule.");
			}
		}
	}
	
	/**
	 * Validates the start phase for the TestSpec.
	 */
	private void validateStartPhaseOfTestSpec() {
		for(ExplicitDeclaration declaration : wrapper.getFramework().getStartPhase().getDeclarations()) {
			assertEquals(1, declaration.getApplicationClasses().size());
			if(declaration.getClassName().equals(CLASS_A)) {
				for(IClass cl : declaration.getApplicationClasses()) {
					assertEquals(CLASS_B, cl.getName().toString());
				}
			} else if(declaration.getClassName().equals(CLASS_A2)) {
				for(IClass cl : declaration.getApplicationClasses()) {
					assertEquals(CLASS_B2, cl.getName().toString());
				}
			} else {
				fail("Unexpected class found in the start phase: "+declaration.getClassName());
			}
		}
	}
	
	/**
	 * Validates a MethodCollector object created during the class hierarchy analysis of the TestSpec.
	 * 
	 * @param coll the MethodCollector object.
	 * @param regexIncluded true if the regex rule (from working phase one) is included. false if not (in working phase
	 *                      two).
	 */
	private void validateMethodCollectorOfTestSpec(MethodCollector coll, boolean regexIncluded) {
		int expectedInstances = regexIncluded?7:5;
		assertEquals(expectedInstances, coll.getFrameworkClasses().size());
		for(IClass cl : coll.getFrameworkClasses()) {
			Set<IMethod> methods = coll.getMethodCollection(cl);
			switch(cl.getName().toString()) {
				case CLASS_A_EVENT_LISTENER:
					assertEquals(1, methods.size());
					for(IMethod m : methods) {
						assertEquals(cl, m.getDeclaringClass());
						assertEquals(METHOD_HANDLE, m.getSelector().toString());
					}
					break;
				case CLASS_B_EVENT_LISTENER:
					assertEquals(1, methods.size());
					for(IMethod m : methods) {
						assertEquals(cl, m.getDeclaringClass());
						assertEquals(METHOD_HANDLE, m.getSelector().toString());
					}
					break;
				case CLASS_C_EVENT_LISTENER:
					assertEquals(1, methods.size());
					for(IMethod m : methods) {
						assertEquals(cl, m.getDeclaringClass());
						assertEquals(METHOD_DO_SOMETHING, m.getSelector().toString());
					}
					break;
				case CLASS_AA_EVENT_LISTENER:
					assertEquals(1, methods.size());
					for(IMethod m : methods) {
						assertEquals(cl, m.getDeclaringClass());
						assertEquals(METHOD_HANDLE_AA, m.getSelector().toString());
					}
					break;
				case CLASS_CONCRETE_FRAMEWORK_C_EVENT_LISTENER:
					assertEquals(1, methods.size());
					for(IMethod m : methods) {
						assertEquals(cl, m.getDeclaringClass());
						assertEquals(METHOD_DO_SOMETHING, m.getSelector().toString());
					}
					break;
				case CLASS_RANDOM:
					assertEquals(1, methods.size());
					for(IMethod m : methods) {
						assertEquals(cl, m.getDeclaringClass());
						assertEquals(METHOD_DO_SOMETHING, m.getSelector().toString());
					}
					break;
				case CLASS_SUB_RANDOM:
					assertEquals(1, methods.size());
					for(IMethod m : methods) {
						assertEquals(cl, m.getDeclaringClass());
						assertEquals(METHOD_DO_SOMETHING, m.getSelector().toString());
					}
					break;
				default:
					fail("The MethodCollector contains the unexpected class or interface: "+cl.getName().toString());
					break;
			}
		}
	}
	
	/**
	 * Tests the analysis of the class hierarchy with no framework jars.
	 * 
	 * @throws ParseException when parsing of the test framework specification fails.
	 * @throws ClassHierarchyCreationException when the creation of the class hierarchy fails.
	 */
	@Test(expected=ClassHierarchyCreationException.class)
	public void testTestSpecWithNonExistingFrameworkJars() throws ParseException, ClassHierarchyCreationException {
		analyzeClassHierarchy(TEST_SPEC_PATH, new String[] {}, new String[] {TEST_APPLICATION_JAR_PATH});
	}
	
	/**
	 * Tests the analysis of the class hierarchy with a non-existent application jar.
	 * 
	 * @throws ParseException when parsing of the test framework specification fails.
	 * @throws ClassHierarchyCreationException when the creation of the class hierarchy fails.
	 */
	@Test(expected=ClassHierarchyCreationException.class)
	public void testTestSpecWithNonExistingApplicationJars() throws ParseException, ClassHierarchyCreationException {
		analyzeClassHierarchy(TEST_SPEC_PATH,
				new String[] {TEST_FRAMEWORK_JAR_PATH}, new String[] {"NonExistent.jar"});
	}
	
	/**
	 * Parses a framework specification and analyzes its class hierarchy.
	 * 
	 * @param frameworkSpecification file path to the framework specification.
	 * @param frameworkJars array with paths to the jar files containing the framework classes.
	 * @param applicationJars array with paths to the jar files containing the application classes.
	 * @throws ParseException when parsing of the framework specification fails.
	 * @throws ClassHierarchyCreationException when creation of the class hierarchy fails.
	 */
	private void analyzeClassHierarchy(String frameworkSpecification, String[] frameworkJars, String[] applicationJars)
			throws ParseException, ClassHierarchyCreationException {
		FrameworkSpecificationParser parser = new FrameworkSpecificationParser();
		framework = parser.parse(frameworkSpecification);
		ClassHierarchyAnalyzer analyzer = new ClassHierarchyAnalyzer();
		wrapper = analyzer.analyzeClassHierarchy(framework, frameworkJars, applicationJars);
		assertNotNull(wrapper);
		assertNotNull(wrapper.getFramework());
		assertEquals(framework, wrapper.getFramework());
		assertNotNull(wrapper.getClassHierarchy());
		assertNotNull(wrapper.getFrameworkClasses());
		for(IClass cl : wrapper.getFrameworkClasses()) {
			// Instances are counted during the bytecode instrumentation.
			assertTrue(wrapper.getInstancesCount(cl)==0);
		}
	}
	
	/**
	 * Converts a set of WALA classes to a set containing the class names.
	 * 
	 * @param classSet the set of WALA classes.
	 * @return the set with the class names.
	 */
	private HashSet<String> convertToStringSet(Set<IClass> classSet) {
		HashSet<String> stringSet = new HashSet<>();
		for(IClass cl : classSet) {
			stringSet.add(cl.getName().toString());
		}
		return stringSet;
	}
	
	/**
	 * Validates an explicit declaration.
	 * 
	 * @param declaration the explicit declaration.
	 */
	private void validateExplicitDeclaration(ExplicitDeclaration declaration) {
		if(declaration.getClassName()!=null) {
			assertNotNull(declaration.getIClass());
			assertEquals(declaration.getClassName(), declaration.getIClass().getName().toString());
		} else {
			assertNull(declaration.getIClass());
		}
		for(int i=0; i<declaration.getNumberOfCallsAndDeclarations(); i++) {
			AstBaseClass abc = declaration.getCallOrDeclaration(i);
			if(abc.getClass()==ExplicitDeclaration.class) {
				validateExplicitDeclaration((ExplicitDeclaration) abc);
			} else if(abc.getClass()==Method.class) {
				Method m = (Method)abc;
				assertNotNull(m.getSignature());
				if(m.getSignature().equals("Constructor")) {
					for(IClass cl : declaration.getApplicationClasses()) {
						assertTrue(wrapper.getClassHierarchy().isSubclassOf(cl, declaration.getIClass())
								||wrapper.getClassHierarchy().implementsInterface(cl, declaration.getIClass()));
						IMethod init = declaration.getConstructor(cl);
						assertNotNull(init);
						assertTrue(init.getName().toString().equals("<init>"));
						assertEquals(cl, init.getDeclaringClass());
					}
				} else {
					assertNotNull(m.getMethod());
					assertTrue(m.getMethod().getSignature().endsWith(m.getSignature()));
					assertEquals(declaration.getIClass(), m.getMethod().getDeclaringClass());
				}
			} else if(abc.getClass()==StaticMethod.class) {
				StaticMethod sm = (StaticMethod)abc;
				assertNotNull(sm.getClassString());
				assertNotNull(sm.getIClass());
				assertEquals(sm.getClassString(), sm.getIClass().getName().toString());
				assertNotNull(sm.getSignature());
				assertNotNull(sm.getMethod());
				assertTrue(sm.getMethod().getSignature().endsWith(sm.getSignature()));
				assertEquals(sm.getIClass(), sm.getMethod().getDeclaringClass());
			}
		}
	}
	
	/**
	 * Validates a block rule.
	 * 
	 * @param b the block rule.
	 */
	private void validateBlock(Block b) {
		assertNotNull(b.getClassName());
		assertNotNull(b.getIClass());
		assertEquals(b.getClassName(), b.getIClass().getName().toString());
		if(b.getInnerBlock()==null) {
			assertNotNull(b.getDeclaration());
			validateExplicitDeclaration(b.getDeclaration());
		} else {
			assertNotNull(b.getInnerBlock());
			validateBlock(b.getInnerBlock());
		}
	}
}
