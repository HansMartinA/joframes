package edu.kit.ipd.pp.joframes.api;

import edu.kit.ipd.pp.joframes.api.exceptions.ParseException;
import edu.kit.ipd.pp.joframes.ast.acha.MethodCollector;
import edu.kit.ipd.pp.joframes.ast.ap.Block;
import edu.kit.ipd.pp.joframes.ast.ap.BlockQuantor;
import edu.kit.ipd.pp.joframes.ast.ap.Regex;
import edu.kit.ipd.pp.joframes.ast.ap.Supertype;
import edu.kit.ipd.pp.joframes.ast.base.Call;
import edu.kit.ipd.pp.joframes.ast.base.ExplicitDeclaration;
import edu.kit.ipd.pp.joframes.ast.base.Framework;
import edu.kit.ipd.pp.joframes.ast.base.Rule;
import edu.kit.ipd.pp.joframes.ast.base.StaticMethod;
import edu.kit.ipd.pp.joframes.ast.base.ThreadType;
import edu.kit.ipd.pp.joframes.ast.base.WorkingPhase;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
	 *
	 * @throws ParseException when parsing fails.
	 */
	@Test
	public void testServletSpecification() throws ParseException {
		parseFramework("Servlets");
		assertEquals(1, framework.getStartPhase().getDeclarations().size());
		ExplicitDeclaration startDeclaration = null;
		for (ExplicitDeclaration dec : framework.getStartPhase().getDeclarations()) {
			startDeclaration = dec;
		}
		assertNull(startDeclaration.getClassName());
		assertEquals(3, startDeclaration.getNumberOfCallsAndDeclarations());
		ExplicitDeclaration decl = (ExplicitDeclaration) startDeclaration.getCallOrDeclaration(0);
		assertEquals(TestConstants.EVENT_LISTENER, decl.getClassName());
		assertEquals(1, decl.getNumberOfCallsAndDeclarations());
		assertEquals(TestConstants.CONSTRUCTOR, ((Call) decl.getCallOrDeclaration(0)).getSignature());
		decl = (ExplicitDeclaration) startDeclaration.getCallOrDeclaration(1);
		assertEquals(TestConstants.SERVLET_CONTEXT_LISTENER, decl.getClassName());
		assertEquals(1, decl.getNumberOfCallsAndDeclarations());
		assertEquals("contextInitialized(Ljavax/servlet/ServletContextEvent;)V",
				((Call) decl.getCallOrDeclaration(0)).getSignature());
		decl = (ExplicitDeclaration) startDeclaration.getCallOrDeclaration(2);
		assertEquals(TestConstants.SERVLET, decl.getClassName());
		assertEquals(2, decl.getNumberOfCallsAndDeclarations());
		assertEquals(TestConstants.CONSTRUCTOR, ((Call) decl.getCallOrDeclaration(0)).getSignature());
		assertEquals("init(Ljavax/servlet/ServletConfig;)V",
				((Call) decl.getCallOrDeclaration(1)).getSignature());
		ExplicitDeclaration endDeclaration = framework.getEndPhase().getEnd();
		assertNull(endDeclaration.getClassName());
		assertEquals(2, endDeclaration.getNumberOfCallsAndDeclarations());
		decl = (ExplicitDeclaration) endDeclaration.getCallOrDeclaration(0);
		assertEquals(TestConstants.SERVLET, decl.getClassName());
		assertEquals(1, decl.getNumberOfCallsAndDeclarations());
		assertEquals("destroy()V", ((Call) decl.getCallOrDeclaration(0)).getSignature());
		decl = (ExplicitDeclaration) endDeclaration.getCallOrDeclaration(1);
		assertEquals(TestConstants.SERVLET_CONTEXT_LISTENER, decl.getClassName());
		assertEquals(1, decl.getNumberOfCallsAndDeclarations());
		assertEquals("contextDestroyed(Ljavax/servlet/ServletContextEvent;)V",
				((Call) decl.getCallOrDeclaration(0)).getSignature());
		assertEquals(1, framework.getWorkingPhases().size());
		WorkingPhase workingPhase = framework.getWorkingPhases().get(0);
		assertEquals(ThreadType.MULTI, workingPhase.getThreadType());
		assertEquals(1, workingPhase.getRules().size());
		Block blockRule = null;
		for (Rule r : workingPhase.getRules()) {
			blockRule = (Block) r;
		}
		assertEquals(BlockQuantor.FOR_ALL, blockRule.getQuantor());
		assertEquals(TestConstants.SERVLET, blockRule.getClassName());
		assertNull(blockRule.getInnerBlock());
		assertNotNull(blockRule.getDeclaration());
		decl = blockRule.getDeclaration();
		assertNull(decl.getClassName());
		assertEquals(3, decl.getNumberOfCallsAndDeclarations());
		ExplicitDeclaration moreDecl = (ExplicitDeclaration) decl.getCallOrDeclaration(0);
		assertEquals(TestConstants.SERVLET_REQUEST_LISTENER, moreDecl.getClassName());
		assertEquals(1, moreDecl.getNumberOfCallsAndDeclarations());
		assertEquals("requestInitialized(Ljavax/servlet/ServletRequestEvent;)V",
				((Call) moreDecl.getCallOrDeclaration(0)).getSignature());
		assertEquals("service(Ljavax/servlet/ServletRequest;Ljavax/servlet/ServletResponse;)V",
				((Call) decl.getCallOrDeclaration(1)).getSignature());
		moreDecl = (ExplicitDeclaration) decl.getCallOrDeclaration(2);
		assertEquals(TestConstants.SERVLET_REQUEST_LISTENER, moreDecl.getClassName());
		assertEquals(1, moreDecl.getNumberOfCallsAndDeclarations());
		assertEquals("requestDestroyed(Ljavax/servlet/ServletRequestEvent;)V",
				((Call) moreDecl.getCallOrDeclaration(0)).getSignature());
	}

	/**
	 * Tests an invalid (empty root element) specification.
	 *
	 * @throws ParseException when parsing fails (here, it should fail).
	 */
	@Test(expected = ParseException.class)
	public void testEmptySpecification() throws ParseException {
		parseFramework("EmptySpec");
	}

	/**
	 * Tests an invalid specification with an empty block rule.
	 *
	 * @throws ParseException when parsing fails (here, it should fail).
	 */
	@Test(expected = ParseException.class)
	public void testInvalidSpecification() throws ParseException {
		parseFramework("InvalidSpec");
	}

	/**
	 * Tests a test specification which uses all elements.
	 *
	 * @throws ParseException when parsing fails.
	 */
	@Test
	public void testCompleteSpecification() throws ParseException {
		parseFramework("CompleteSpec");
		assertNotNull(framework.getResourceLoader());
		assertEquals("Test.class", framework.getResourceLoader().getClassName());
		assertEquals(3, framework.getStartPhase().getDeclarations().size());
		List<WorkingPhase> workingPhases = framework.getWorkingPhases();
		assertEquals(3, workingPhases.size());
		assertEquals(ThreadType.MULTI, workingPhases.get(0).getThreadType());
		assertEquals(1, workingPhases.get(0).getRules().size());
		for (Rule r : workingPhases.get(0).getRules()) {
			assertEquals(Block.class, r.getClass());
		}
		assertEquals(ThreadType.SINGLE, workingPhases.get(1).getThreadType());
		for (Rule r : workingPhases.get(1).getRules()) {
			assertNotEquals(MethodCollector.class, r.getClass());
			if (r.getClass() == Regex.class) {
				Regex rr = (Regex) r;
				assertEquals(".*", rr.getRegularExpression());
			} else if (r.getClass() == Supertype.class) {
				Supertype rr = (Supertype) r;
				assertTrue(rr.getSuperType().equals(TestConstants.EVENT_LISTENER)
						|| rr.getSuperType().equals("Ljava/lang/Object")
						|| rr.getSuperType().equals("Ljavax/swing/JFrame"));
			} else {
				Block block = (Block) r;
				StaticMethod method = (StaticMethod) block.getInnerBlock().getInnerBlock().getDeclaration()
						.getCallOrDeclaration(0);
				assertEquals("Ljava/lang/Runtime", method.getClassString());
				assertEquals("getRuntime()Ljava/lang/Runtime;", method.getSignature());
			}
		}
		assertEquals(ThreadType.MULTI, workingPhases.get(2).getThreadType());
		assertEquals(0, workingPhases.get(2).getRules().size());
	}

	/**
	 * Does the actual parsing of a specification and performs basic tests.
	 *
	 * @param fileName name of the specification file.
	 * @throws ParseException when parsing fails.
	 */
	private void parseFramework(final String fileName) throws ParseException {
		framework = parser.parse("src/test/resources/" + fileName + ".xml");
		assertNotNull(framework);
		assertNotNull(framework.getStartPhase());
		assertNotNull(framework.getStartPhase().getDeclarations());
		assertNotNull(framework.getEndPhase());
		assertNotNull(framework.getEndPhase().getEnd());
		assertNotNull(framework.getWorkingPhases());
		for (WorkingPhase phase : framework.getWorkingPhases()) {
			assertNotNull(phase);
			assertNotNull(phase.getThreadType());
			assertNotNull(phase.getRules());
		}
	}
}
