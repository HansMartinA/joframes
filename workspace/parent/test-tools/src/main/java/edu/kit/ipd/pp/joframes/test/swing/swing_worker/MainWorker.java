package edu.kit.ipd.pp.joframes.test.swing.swing_worker;

/**
 * Main class which creates the main frame.
 *
 * @author Martin Armbruster
 */
public final class MainWorker {
	/**
	 * Private constructor to avoid instantiation.
	 */
	private MainWorker() {
	}

	/**
	 * The main method.
	 *
	 * @param args arguments.
	 */
	public static void main(final String[] args) {
		new SwingWorkerFrame();
	}
}
