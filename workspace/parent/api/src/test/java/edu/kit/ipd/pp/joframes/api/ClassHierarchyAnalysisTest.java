package edu.kit.ipd.pp.joframes.api;

import com.ibm.wala.classLoader.IClass;
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
		assertEquals(10, wrapper.getFrameworkClasses().size());
		Set<?> set = convertToStringSet(wrapper.getFrameworkClasses());
		assertTrue(set.contains(PACKAGE_FRAMEWORK+"A"));
		assertTrue(set.contains(PACKAGE_FRAMEWORK+"AEventListener"));
		assertTrue(set.contains(PACKAGE_FRAMEWORK+"BEventListener"));
		assertTrue(set.contains(PACKAGE_FRAMEWORK+"AAEventListener"));
		assertTrue(set.contains(PACKAGE_FRAMEWORK+"BlockA"));
		assertTrue(set.contains(PACKAGE_FRAMEWORK+"BlockB"));
		assertTrue(set.contains(PACKAGE_FRAMEWORK+"BlockC"));
		assertTrue(set.contains(PACKAGE_FRAMEWORK+"Random"));
		assertTrue(set.contains(PACKAGE_FRAMEWORK+"SubRandom"));
		assertTrue(set.contains("Ljava/util/EventListener"));
		for(ExplicitDeclaration declaration : wrapper.getFramework().getStartPhase().getDeclarations()) {
			validateExplicitDeclaration(declaration);
		}
		validateExplicitDeclaration(wrapper.getFramework().getEndPhase().getEnd());
		assertEquals(2, wrapper.getFramework().getWorkingPhases().size());
		WorkingPhase working = wrapper.getFramework().getWorkingPhases().get(0);
		assertEquals(ThreadType.MULTI, working.getThreadType());
		for(Rule r : working.getRules()) {
			if(r.getClass()==MethodCollector.class) {
				MethodCollector c = (MethodCollector)r;
				// Test the MethodCollector.
			} else {
				fail("First working phase contains an illegal block, regex or supertype rule.");
			}
		}
		working = wrapper.getFramework().getWorkingPhases().get(1);
		assertEquals(ThreadType.SINGLE, working.getThreadType());
		for(Rule r : working.getRules()) {
			if(r.getClass()==MethodCollector.class) {
				MethodCollector c = (MethodCollector)r;
				// Test the MethodCollector.
			} else if(r.getClass()==Block.class) {
				validateBlock((Block)r);
			} else {
				fail("The second working phase contains an illegal regex or supertype rule.");
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
		assertNotNull(wrapper.getFrameworkClasses());
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
				assertNotNull(m.getMethod());
				if(m.getSignature().equals("Constructor")) {
					assertTrue(m.getMethod().getSignature().endsWith("<init>()V"));
					for(IClass cl : declaration.getApplicationClasses()) {
						assertTrue(cl.getClassHierarchy().isSubclassOf(cl, declaration.getIClass()));
					}
				} else {
					assertTrue(m.getMethod().getSignature().endsWith(m.getSignature()));
				}
				assertEquals(declaration.getIClass(), m.getMethod().getDeclaringClass());
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
