package edu.kit.ipd.pp.joframes.api.exceptions;

/**
 * Exception in case the class hierarchy analysis fails or an unexpected state is encountered.
 *
 * @author Martin Armbruster
 */
public class ClassHierarchyAnalysisException extends Exception {
	/**
	 * Creates a new instance.
	 *
	 * @param message message of the exception.
	 */
	public ClassHierarchyAnalysisException(final String message) {
		super(message);
	}
}
