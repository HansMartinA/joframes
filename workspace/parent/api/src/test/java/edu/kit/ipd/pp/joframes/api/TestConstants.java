package edu.kit.ipd.pp.joframes.api;

/**
 * This class contains constants used by test classes.
 * 
 * @author Martin Armbruster
 */
public final class TestConstants {
	/**
	 * Constant name for the constructor used in a framework specification.
	 */
	static final String CONSTRUCTOR = "Constructor";
	/**
	 * Constant name for the EventListener interface.
	 */
	static final String EVENT_LISTENER = "Ljava/util/EventListener";
	/**
	 * Constant name of the Servlet class.
	 */
	static final String SERVLET = "Ljavax/servlet/Servlet";
	/**
	 * Constant name of the ServletContextListener interface.
	 */
	static final String SERVLET_CONTEXT_LISTENER = "Ljavax/servlet/ServletContextListener";
	/**
	 * Constant name of the ServletRequestListener interface.
	 */
	static final String SERVLET_REQUEST_LISTENER = "Ljavax/servlet/ServletRequestListener";
	/**
	 * Stores the path to the test specification.
	 */
	static final String TEST_SPEC_PATH = "src/test/resources/TestSpec.xml";
	/**
	 * Stores the path to the jar file with the test framework.
	 */
	static final String TEST_FRAMEWORK_JAR_PATH = "src/test/resources/api-test-data-2.0.jar";
	/**
	 * Stores the path to the jar file with the test application.
	 */
	static final String TEST_APPLICATION_JAR_PATH = "src/test/resources/api-test-data-2.0-tests.jar";
	/**
	 * General package name for the test framework and application.
	 */
	static final String PACKAGE = "Ledu/kit/ipd/pp/joframes/api/test/";
	/**
	 * Package name of the test framework.
	 */
	static final String PACKAGE_FRAMEWORK = PACKAGE+"framework/";
	/**
	 * Package name of the test application.
	 */
	static final String PACKAGE_APPLICATION = PACKAGE+"application/";
	/**
	 * Bytecode name of the A class.
	 */
	static final String CLASS_A = PACKAGE_FRAMEWORK+"A";
	/**
	 * Bytecode name of the A2 interface.
	 */
	static final String CLASS_A2 = PACKAGE_FRAMEWORK+"A2";
	/**
	 * Bytecode name of the AEventListener interface.
	 */
	static final String CLASS_A_EVENT_LISTENER = PACKAGE_FRAMEWORK+"AEventListener";
	/**
	 * Bytecode name of the BEventListener interface.
	 */
	static final String CLASS_B_EVENT_LISTENER = PACKAGE_FRAMEWORK+"BEventListener";
	/**
	 * Bytecode name of the AAEventListener interface.
	 */
	static final String CLASS_AA_EVENT_LISTENER = PACKAGE_FRAMEWORK+"AAEventListener";
	/**
	 * Bytecode name of the CEventListener interface.
	 */
	static final String CLASS_C_EVENT_LISTENER = PACKAGE_FRAMEWORK+"CEventListener";
	/**
	 * Bytecode name of the ConcreteFrameworkCEventListener class.
	 */
	static final String CLASS_CONCRETE_FRAMEWORK_C_EVENT_LISTENER = PACKAGE_FRAMEWORK
			+"ConcreteFrameworkCEventListener";
	/**
	 * Bytecode name of the BlockA class.
	 */
	static final String CLASS_BLOCK_A = PACKAGE_FRAMEWORK+"BlockA";
	/**
	 * Bytecode name of the BlockB class.
	 */
	static final String CLASS_BLOCK_B = PACKAGE_FRAMEWORK+"BlockB";
	/**
	 * Bytecode name of the BlockC class.
	 */
	static final String CLASS_BLOCK_C = PACKAGE_FRAMEWORK+"BlockC";
	/**
	 * Bytecode name of the Random class.
	 */
	static final String CLASS_RANDOM = PACKAGE_FRAMEWORK+"Random";
	/**
	 * Bytecode name of the SubRandom class.
	 */
	static final String CLASS_SUB_RANDOM = PACKAGE_FRAMEWORK+"SubRandom";
	/**
	 * Bytecode name of the B class.
	 */
	static final String CLASS_B = PACKAGE_APPLICATION+"B";
	/**
	 * Bytecode name of the B2 class.
	 */
	static final String CLASS_B2 = PACKAGE_APPLICATION+"B2";
	/**
	 * Bytecode name for a handle method.
	 */
	static final String METHOD_HANDLE = "handle()V";
	/**
	 * Bytecode name for a handleAA method.
	 */
	static final String METHOD_HANDLE_AA = "handleAA()V";
	/**
	 * Bytecode name for a doSomething method.
	 */
	static final String METHOD_DO_SOMETHING = "doSomething()V";
	
	/**
	 * Private constructor to avoid instantiation because the class contains only constants.
	 */
	private TestConstants() {
	}
}
