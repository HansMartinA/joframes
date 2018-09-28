package edu.kit.ipd.pp.joframes.jfx.conf;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;

/**
 * An EventHandler for ActionEvents that uses a text field to put out some text.
 *
 * @author Martin Armbruster
 */
public final class ConfEventHandler implements EventHandler<ActionEvent> {
	/**
	 * Stores the secret.
	 */
	private String secret = "secret";
	/**
	 * Stores a TextField for some output.
	 */
	private TextField text;

	/**
	 * Creates a new instance.
	 *
	 * @param textField text field for output.
	 */
	public ConfEventHandler(final TextField textField) {
		text = textField;
	}

	@Override
	public void handle(final ActionEvent event) {
		text.setText(secret);
	}
}
