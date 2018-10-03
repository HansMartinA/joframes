package edu.kit.ipd.pp.joframes.api.logging;

/**
 * A logging implementation that ignores all messages.
 *
 * @author Martin Armbruster
 */
final class NoOpImplementation implements LogImplementation {
	@Override
	public void log(final String message) {
	}

	@Override
	public void logExtended(final String message) {
	}
}
