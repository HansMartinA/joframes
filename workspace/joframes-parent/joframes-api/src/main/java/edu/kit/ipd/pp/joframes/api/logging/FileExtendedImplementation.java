package edu.kit.ipd.pp.joframes.api.logging;

/**
 * A logging implementation that writes all messages to a file.
 *
 * @author Martin Armbruster
 */
class FileExtendedImplementation extends FileImplementation {
	/**
	 * Creates a new instance.
	 *
	 * @param file the file in which all messages are written.
	 */
	FileExtendedImplementation(final String file) {
		super(file);
	}

	@Override
	public void logExtended(final String message) {
		log(message);
	}
}
