package edu.kit.ipd.pp.joframes.api.external;

/**
 * Class that contains the artificial main method acting as the main entry point.
 *
 * @author Martin Armbruster
 */
public final class ArtificialClass {
	/**
	 * Creates a new instance. Private to avoid external instantiation.
	 */
	private ArtificialClass() {
	}

	/**
	 * Artificial main method.
	 *
	 * @param args Arguments.
	 */
	public static void main(final String[] args) {
		new ArtificialClass().artificialMain();
	}

	/**
	 * The actual artificial main method.
	 */
	private void artificialMain() {
		start();
		working();
		end();
	}

	/**
	 * Will contain the code for the start phase.
	 */
	private void start() {
	}

	/**
	 * Will contain the code for the end phase.
	 */
	private void end() {
	}

	/**
	 * Will contain the code for the working phase.
	 */
	private void working() {
	}

	/**
	 * A general Runnable class that is used for the working phase.
	 *
	 * @author Martin Armbruster
	 */
	private class WorkingWorker implements Runnable {
		/**
		 * Stores a reference to the outer instance using this instance.
		 */
		private ArtificialClass outerInstance;
		/**
		 * Stores the number of the working phase this instance belongs to.
		 */
		private int phaseNumber;

		/**
		 * Creates a new instance.
		 *
		 * @param instance outer instance using this instance.
		 * @param phase number of the working phase this instance belongs to.
		 */
		WorkingWorker(final ArtificialClass instance, final int phase) {
			outerInstance = instance;
			this.phaseNumber = phase;
		}

		@Override
		public void run() {
		}
	}
}
