package edu.kit.ipd.pp.joframes.test.jfx.worker;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TextField;

/**
 * A simple Worker that simulates a long-running task.
 *
 * @author Martin Armbruster
 */
public final class WorkerWorker extends Task<Void> {
	/**
	 * A secret.
	 */
	private String secret = "secret";
	/**
	 * Stores the text field for some output.
	 */
	private TextField text;

	/**
	 * Creates a new instance.
	 *
	 * @param textField a text field for some output.
	 */
	public WorkerWorker(final TextField textField) {
		text = textField;
	}

	@Override
	protected Void call() {
		String output = secret;
		double iterations = Math.random();
		while (iterations > -1) {
			iterations -= 0.05;
			output += iterations;
		}
		String o = output;
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				text.setText(o);
			}
		});
		return null;
	}
}
