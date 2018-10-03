package edu.kit.ipd.pp.joframes.api.logging;

/**
 * Interface for actual logging implementations.
 *
 * @author Martin Armbruster
 */
interface LogImplementation {
	/**
	 * Logs a message.
	 *
	 * @param message the message.
	 */
	void log(String message);
	/**
	 * Logs a more detailed message.
	 *
	 * @param message the message.
	 */
	void logExtended(String message);
}
