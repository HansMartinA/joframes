package edu.kit.ipd.pp.joframes.test.swing.swing_worker;

/**
 * Main class for the Swing test case including a SwingWorker.
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
