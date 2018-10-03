package edu.kit.ipd.pp.joframes.api.logging;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A logging implementation that writes the messages to a file.
 *
 * @author Martin Armbruster
 */
class FileImplementation implements LogImplementation {
	/**
	 * Stores the writer for the file.
	 */
	private BufferedWriter writer;

	/**
	 * Creates a new instance.
	 */
	FileImplementation() {
		try {
			writer = new BufferedWriter(new FileWriter("", true));
		} catch (IOException e) {
		}
	}

	@Override
	public void log(final String message) {
		try {
			writer.write(message);
		} catch (IOException | NullPointerException e) {
		}
	}

	@Override
	public void logExtended(final String message) {
	}
}
