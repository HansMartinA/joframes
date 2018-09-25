package edu.kit.ipd.pp.joframes.test.swing.swing_worker;

import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import javax.swing.text.JTextComponent;

/**
 * A SwingWorker which simulates a long-running task.
 *
 * @author Martin Armbruster
 */
public class LongRunningSwingWorker extends SwingWorker<String, Void> {
	/**
	 * Stores a secret.
	 */
	private String secret = "secret";
	/**
	 * Stores a text component for output.
	 */
	private JTextComponent text;

	/**
	 * Creates a new instance.
	 *
	 * @param textComponent a text component for output.
	 */
	public LongRunningSwingWorker(final JTextComponent textComponent) {
		text = textComponent;
	}

	@Override
	protected String doInBackground() throws Exception {
		String output =  secret;
		double iterations = Math.random() * Double.MAX_VALUE;
		while (iterations >= 0) {
			output += iterations;
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
			}
			iterations -= 0.5;
		}
		return output;
	}

	@Override
	protected void done() {
		try {
			text.setText(get());
		} catch (InterruptedException | ExecutionException e) {
		}
	}
}
