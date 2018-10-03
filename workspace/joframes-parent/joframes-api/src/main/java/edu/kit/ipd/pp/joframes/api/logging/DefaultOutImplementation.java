package edu.kit.ipd.pp.joframes.api.logging;

/**
 * A logging implementation that prints the messages out on the standard output.
 *
 * @author Martin Armbruster
 */
class DefaultOutImplementation implements LogImplementation {
	@Override
	public void log(final String message) {
		System.out.print(message);
	}

	@Override
	public void logExtended(final String message) {
	}

	@Override
	public void endLog(final String message) {
		log(message);
	}
}
