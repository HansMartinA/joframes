package edu.kit.ipd.pp.joframes.api;

import com.ibm.wala.shrikeBT.Constants;

/**
 * This class contains common used constants.
 *
 * @author Martin Armbruster
 */
public final class APIConstants {
	/**
	 * Name of the system property that is only set for test cases to indicate a test and development environment.
	 */
	public static final String TEST_SYSTEM_PROPERTY = "edu.kit.ipd.pp.joframes.test.active";
	/**
	 * Default regular expression for selecting classes to exclude in the instrumentation.
	 */
	public static final String DEFAULT_EXCLUSION_REGEX = "(java\\/nio\\/.*)|"
			+ "(java\\/net\\/.*)|"
			+ "(com\\/sun\\/.*)|"
			+ "(sun\\/.*)|"
			+ "(apple\\/awt\\/.*)|"
			+ "(com\\/apple\\/.*)|"
			+ "(org\\/omg\\/.*)";
	/**
	 * Signature of a main method.
	 */
	static final String MAIN_SIGNATURE = "main([" + Constants.TYPE_String + ")" + Constants.TYPE_void;
	/**
	 * Method name within the framework specification to identify constructors.
	 */
	static final String CONSTRUCTOR = "Constructor";
	/**
	 * Name of a constructor in bytecode.
	 */
	static final String INIT = "<init>";
	/**
	 * Signature of the default constructor.
	 */
	static final String DEFAULT_CONSTRUCTOR_SIGNATURE = INIT + "()" + Constants.TYPE_void;

	/**
	 * Private constructor to avoid instantiation.
	 */
	private APIConstants() {
	}
}
