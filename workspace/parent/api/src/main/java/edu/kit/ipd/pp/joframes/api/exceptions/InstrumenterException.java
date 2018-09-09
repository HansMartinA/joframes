package edu.kit.ipd.pp.joframes.api.exceptions;

/**
 * Exception that is thrown if the bytecode instrumentation fails.
 *
 * @author Martin Armbruster
 */
public class InstrumenterException extends Exception {
	/**
	 * Creates a new instance.
	 *
	 * @param message message of the exception.
	 * @param cause cause of the exception.
	 */
	public InstrumenterException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
