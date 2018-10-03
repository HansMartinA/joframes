package edu.kit.ipd.pp.joframes.api.logging;

/**
 * This class provides a log for the processing of a framework and application.
 *
 * @author Martin Armbruster
 */
public final class Log {
	/**
	 * A listing of all available logging options.
	 *
	 * @author Martin Armbruster
	 */
	public enum LogOptions {
		/**
		 * Default option: no log message is put out.
		 */
		NO_OP,
		/**
		 * The log messages are put out on the standard output.
		 */
		DEFAULT_OUT,
		/**
		 * All log messages are put out on the standard output.
		 */
		DEFAULT_OUT_EXTENDED,
		/**
		 * The log messages are written to a file.
		 */
		FILE,
		/**
		 * All log messages are written to a file.
		 */
		FILE_EXTENDED,
	}

	/**
	 * Stores the actual logging implementation.
	 */
	private static LogImplementation impl = new NoOpImplementation();

	/**
	 * Private constructor to avoid instantiation.
	 */
	private Log() {
	}

	/**
	 * Sets the logging option to use.
	 *
	 * @param option the option.
	 */
	public static void setLogOption(final LogOptions option) {
		switch (option) {
			case NO_OP:
				impl = new NoOpImplementation();
				break;
			case DEFAULT_OUT:
				impl = new DefaultOutImplementation();
				break;
			case DEFAULT_OUT_EXTENDED:
				impl = new DefaultOutExtendedImplementation();
				break;
			case FILE:
				impl = new FileImplementation();
				break;
			case FILE_EXTENDED:
				impl = new FileExtendedImplementation();
				break;
			default:
				impl = new NoOpImplementation();
				break;
		}
	}

	/**
	 * Logs a message.
	 *
	 * @param message the message.
	 */
	static void log(final String message) {
		impl.log(message);
	}

	/**
	 * Logs a more detailed message.
	 *
	 * @param message the message.
	 */
	static void logExtended(final String message) {
		impl.logExtended(message);
	}
}
