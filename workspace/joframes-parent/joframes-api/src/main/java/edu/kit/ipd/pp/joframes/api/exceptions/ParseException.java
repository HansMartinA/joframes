package edu.kit.ipd.pp.joframes.api.exceptions;

/**
 * Exception that is thrown when the parsing of a framework specification fails.
 *
 * @author Martin Armbruster
 */
public class ParseException extends Exception {
	/**
	 * Creates a new instance.
	 *
	 * @param message message of the exception.
	 * @param cause cause of the exception.
	 */
	public ParseException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
