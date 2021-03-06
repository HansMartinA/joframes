package edu.kit.ipd.pp.joframes.api.exceptions;

/**
 * Exception that is thrown when the creation of the class hierarchy fails.
 *
 * @author Martin Armbruster
 */
public class ClassHierarchyCreationException extends Exception {
	/**
	 * Creates a new instance.
	 *
	 * @param message message of the exception.
	 */
	public ClassHierarchyCreationException(final String message) {
		super(message);
	}

	/**
	 * Creates a new instance.
	 *
	 * @param message message of the exception.
	 * @param cause cause of the exception.
	 */
	public ClassHierarchyCreationException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
