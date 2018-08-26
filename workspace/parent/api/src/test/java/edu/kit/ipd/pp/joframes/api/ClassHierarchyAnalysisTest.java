package edu.kit.ipd.pp.joframes.api;

import com.ibm.wala.classLoader.IClass;
import edu.kit.ipd.pp.joframes.api.exceptions.ClassHierarchyCreationException;
import edu.kit.ipd.pp.joframes.api.exceptions.ParseException;
import edu.kit.ipd.pp.joframes.api.test.framework.A;
import edu.kit.ipd.pp.joframes.api.test.framework.AAEventListener;
import edu.kit.ipd.pp.joframes.api.test.framework.AEventListener;
import edu.kit.ipd.pp.joframes.api.test.framework.BEventListener;
import edu.kit.ipd.pp.joframes.api.test.framework.BlockA;
import edu.kit.ipd.pp.joframes.api.test.framework.BlockB;
import edu.kit.ipd.pp.joframes.api.test.framework.BlockC;
import edu.kit.ipd.pp.joframes.api.test.framework.Random;
import edu.kit.ipd.pp.joframes.api.test.framework.SubRandom;
import edu.kit.ipd.pp.joframes.ast.base.Framework;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test class for the ClassHierarchyAnalysis class.
 * 
 * @author Martin Armbruster
 */
public class ClassHierarchyAnalysisTest {
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
		analyzeClassHierarchy("src/test/resources/TestSpec.xml", new String[]
				{"src/test/resources/api-test-data-1.0.jar"}, new String[]
						{"src/test/resources/api-test-data-1.0-tests.jar"});
		assertEquals(9, wrapper.getFrameworkClasses().size());
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
		set = wrapper.getClasses();
		assertTrue(set.contains(A.class));
		assertTrue(set.contains(AEventListener.class));
		assertTrue(set.contains(BEventListener.class));
		assertTrue(set.contains(AAEventListener.class));
		assertTrue(set.contains(BlockA.class));
		assertTrue(set.contains(BlockB.class));
		assertTrue(set.contains(BlockC.class));
		assertTrue(set.contains(Random.class));
		assertTrue(set.contains(SubRandom.class));
		assertEquals(10, wrapper.getApplicationClasses());
		set = convertToStringSet(wrapper.getApplicationClasses());
		assertTrue(set.contains(PACKAGE_APPLICATION+"B"));
		assertTrue(set.contains(PACKAGE_APPLICATION+"AEventListenerImpl"));
		assertTrue(set.contains(PACKAGE_APPLICATION+"BEventListenerImpl"));
		assertTrue(set.contains(PACKAGE_APPLICATION+"AAEventListenerImpl"));
		assertTrue(set.contains(PACKAGE_APPLICATION+"AABEventListenerImpl"));
		assertTrue(set.contains(PACKAGE_APPLICATION+"SubBlockA"));
		assertTrue(set.contains(PACKAGE_APPLICATION+"SubBlockB"));
		assertTrue(set.contains(PACKAGE_APPLICATION+"SubBlockC"));
		assertTrue(set.contains(PACKAGE_APPLICATION+"AppRandom"));
		assertTrue(set.contains(PACKAGE_APPLICATION+"AppSubRandom"));
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
		assertNotNull(wrapper.getApplicationClasses());
		assertNotNull(wrapper.getClasses());
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
}
