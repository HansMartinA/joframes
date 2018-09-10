package edu.kit.ipd.pp.joframes.ui.gui;

/**
 * Main class that starts the graphical user interface.
 *
 * @author Martin Armbruster
 */
public final class Main {
	/**
	 * Private constructor to avoid instantiation.
	 */
	private Main() {
	}

	/**
	 * Main method.
	 *
	 * @param args arguments.
	 */
	public static void main(final String[] args) {
		new MainFrame().setVisible(true);
	}
}
