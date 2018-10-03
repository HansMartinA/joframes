package edu.kit.ipd.pp.joframes.api.logging;

/**
 * A logging implementation that prints all messages out on the standard output.
 *
 * @author Martin Armbruster
 */
class DefaultOutExtendedImplementation extends DefaultOutImplementation {
	@Override
	public void logExtended(final String message) {
		System.out.print(message);
	}
}
