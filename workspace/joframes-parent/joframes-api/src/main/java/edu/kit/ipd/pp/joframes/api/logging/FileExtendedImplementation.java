package edu.kit.ipd.pp.joframes.api.logging;

/**
 * A logging implementation that writes all messages to a file.
 *
 * @author Martin Armbruster
 */
class FileExtendedImplementation extends FileImplementation {
	@Override
	public void logExtended(final String message) {
		log(message);
	}
}
