package edu.kit.ipd.pp.joframes.api;

import com.ibm.wala.shrikeBT.Constants;

/**
 * This class contains common used constants.
 *
 * @author Martin Armbruster
 */
final class APIConstants {
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