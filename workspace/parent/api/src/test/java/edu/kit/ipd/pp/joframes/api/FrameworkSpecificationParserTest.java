package edu.kit.ipd.pp.joframes.api;

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
import org.junit.Ignore;
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
	private static final String CONSTRUCTOR = "Constructor";
	private static final String EVENT_LISTENER = "Ljava/util/EventListener";
	private static final String SERVLET = "Ljavax/swing/Servlet";
	private static final String SERVLET_CONTEXT_LISTENER = "Ljavax/swing/ServletContextListener";
	private static final String SERVLET_REQUEST_LISTENER = "Ljavax/swing/ServletRequestListener";
	
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
		assertEquals(1, framework.getStartPhase().getDeclarations());
		ExplicitDeclaration startDeclaration = null;
		for(ExplicitDeclaration dec : framework.getStartPhase().getDeclarations()) {
			startDeclaration = dec;
		}
		assertNull(startDeclaration.getClassName());
		assertEquals(3, startDeclaration.getNumberOfCallsAndDeclarations());
		ExplicitDeclaration decl = (ExplicitDeclaration)startDeclaration.getCallOrDeclaration(0);
		assertEquals(EVENT_LISTENER, decl.getClassName());
		assertEquals(1, decl.getNumberOfCallsAndDeclarations());
		assertEquals(CONSTRUCTOR, ((Call)decl.getCallOrDeclaration(0)).getSignature());
		decl = (ExplicitDeclaration)startDeclaration.getCallOrDeclaration(1);
		assertEquals(SERVLET_CONTEXT_LISTENER, decl.getClassName());
		assertEquals(1, decl.getNumberOfCallsAndDeclarations());
		assertEquals("contextInitialized(Ljavax/servlet/ServletContextEvent;)V",
				((Call)decl.getCallOrDeclaration(0)).getSignature());
		decl = (ExplicitDeclaration)startDeclaration.getCallOrDeclaration(2);
		assertEquals(SERVLET, decl.getClassName());
		assertEquals(2, decl.getNumberOfCallsAndDeclarations());
		assertEquals(CONSTRUCTOR, ((Call)startDeclaration.getCallOrDeclaration(0)).getSignature());
		assertEquals("init(Ljavax/servlet/ServletConfig;)V",
				((Call)startDeclaration.getCallOrDeclaration(1)).getSignature());
		ExplicitDeclaration endDeclaration = framework.getEndPhase().getEnd();
		assertNull(endDeclaration.getClassName());
		assertEquals(2, endDeclaration.getNumberOfCallsAndDeclarations());
		decl = (ExplicitDeclaration)endDeclaration.getCallOrDeclaration(0);
		assertEquals(SERVLET, decl.getClassName());
		assertEquals(1, decl.getNumberOfCallsAndDeclarations());
		assertEquals("destroy()V", ((Call)decl.getCallOrDeclaration(0)).getSignature());
		decl = (ExplicitDeclaration)endDeclaration.getCallOrDeclaration(1);
		assertEquals(SERVLET_CONTEXT_LISTENER, decl.getClassName());
		assertEquals(1, decl.getNumberOfCallsAndDeclarations());
		assertEquals("contextDestroyed(Ljavax/servlet/ServletContextEvent;)V",
				((Call)decl.getCallOrDeclaration(0)).getSignature());
		assertEquals(1, framework.getWorkingPhases().size());
		WorkingPhase workingPhase = framework.getWorkingPhases().get(0);
		assertEquals(ThreadType.MULTI, workingPhase.getThreadType());
		assertEquals(1, workingPhase.getRules().size());
		Block blockRule = null;
		for(Rule r : workingPhase.getRules()) {
			blockRule = (Block)r;
		}
		assertEquals(BlockQuantor.FOR_ALL, blockRule.getQuantor());
		assertEquals(SERVLET, blockRule.getClassName());
		assertNull(blockRule.getInnerBlock());
		assertNotNull(blockRule.getDeclaration());
		decl = blockRule.getDeclaration();
		assertNull(decl.getClassName());
		assertEquals(3, decl.getNumberOfCallsAndDeclarations());
		ExplicitDeclaration moreDecl = (ExplicitDeclaration)decl.getCallOrDeclaration(0);
		assertEquals(SERVLET_REQUEST_LISTENER, moreDecl.getClassName());
		assertEquals(1, moreDecl.getNumberOfCallsAndDeclarations());
		assertEquals("requestInitialized(Ljavax/servlet/ServletRequestEvent;)V",
				((Call)moreDecl.getCallOrDeclaration(0)).getSignature());
		assertEquals("service(Ljavax/servlet/ServletRequest;Ljavax/servlet/ServletResponse;)V",
				((Call)decl.getCallOrDeclaration(1)).getSignature());
		moreDecl = (ExplicitDeclaration)decl.getCallOrDeclaration(2);
		assertEquals(SERVLET_REQUEST_LISTENER, moreDecl.getClassName());
		assertEquals(1, moreDecl.getNumberOfCallsAndDeclarations());
		assertEquals("requestDestroyed(Ljavax/servlet/ServletRequestEvent;)V",
				((Call)moreDecl.getCallOrDeclaration(0)).getSignature());
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
		assertEquals(3, framework.getStartPhase().getDeclarations().size());
		List<WorkingPhase> workingPhases = framework.getWorkingPhases();
		assertEquals(3, workingPhases.size());
		assertEquals(ThreadType.MULTI, workingPhases.get(0).getThreadType());
		assertEquals(1, workingPhases.get(0).getRules().size());
		for(Rule r : workingPhases.get(0).getRules()) {
			assertEquals(Block.class, r.getClass());
		}
		assertEquals(ThreadType.SINGLE, workingPhases.get(1).getThreadType());
		for(Rule r : workingPhases.get(1).getRules()) {
			assertNotEquals(MethodCollector.class, r.getClass());
			if(r.getClass()==Regex.class) {
				Regex rr = (Regex)r;
				assertEquals(".*", rr.getRegularExpression());
			} else if(r.getClass()==Supertype.class) {
				Supertype rr = (Supertype)r;
				assertTrue(rr.getSuperType().equals(EVENT_LISTENER) || rr.getSuperType().equals("Ljava/lang/Object")
						|| rr.getSuperType().equals("Ljavax/swing/JFrame"));
			} else {
				Block block = (Block)r;
				StaticMethod method = (StaticMethod)block.getInnerBlock().getInnerBlock().getDeclaration()
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
	 */
	private void parseFramework(String fileName) {
		framework = parser.parse("src/test/resources/"+fileName+".xml");
		assertNotNull(framework);
		assertNotNull(framework.getStartPhase());
		assertNotNull(framework.getStartPhase().getDeclarations());
		assertNotNull(framework.getEndPhase());
		assertNotNull(framework.getEndPhase().getEnd());
		assertNotNull(framework.getWorkingPhases());
		for(WorkingPhase phase : framework.getWorkingPhases()) {
			assertNotNull(phase);
			assertNotNull(phase.getThreadType());
			assertNotNull(phase.getRules());
		}
	}
}