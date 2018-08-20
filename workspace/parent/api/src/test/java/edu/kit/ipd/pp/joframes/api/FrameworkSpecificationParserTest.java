package edu.kit.ipd.pp.joframes.api;

import edu.kit.ipd.pp.joframes.ast.base.Framework;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test class for the FrameworkSpecificationParser.
 * 
 * @author Martin Armbruster
 */
public class FrameworkSpecificationParserTest {
	/**
	 * Stores the used parser for the tests.
	 */
	private FrameworkSpecificationParser parser;
	/**
	 * Stores the generated test ast.
	 */
	private Framework framework;
	
	/**
	 * Sets all test resources up.
	 */
	@Before
	public void setUp() {
		parser = new FrameworkSpecificationParser();
	}
	
	/**
	 * Tests the parsing of the servlet specification.
	 */
	@Test
	public void testServletSpecification() {
		parseFramework("Servlets");
	}
	
	/**
	 * Tests an invalid (empty root element) specification. This test case is ignored because an invalid specification
	 * leads to a termination of the jvm.
	 */
	@Test
	@Ignore
	public void testEmptySpecification() {
		parseFramework("EmptySpec");
	}
	
	/**
	 * Tests an invalid specification with an empty block rule. This test case is ignored because an invalid
	 * specification leads to a termination of the jvm. 
	 */
	@Test
	@Ignore
	public void testInvalidSpecification() {
		parseFramework("InvalidSpec");
	}
	
	/**
	 * Tests a test specification which uses all elements.
	 */
	@Test
	public void testCompleteSpecification() {
		parseFramework("CompleteSpec");
	}
	
	/**
	 * Does the actual parsing of a specification.
	 * 
	 * @param fileName name of the specification file.
	 */
	private void parseFramework(String fileName) {
		framework = parser.parse("src/test/resources/"+fileName+".xml");
	}
}
