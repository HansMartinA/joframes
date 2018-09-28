package edu.kit.ipd.pp.joframes.test.jfx.password;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextInputControl;

/**
 * An EventHandler that puts the content of a text field out.
 *
 * @author Martin Armbruster
 */
public final class TextOutputEventHandler implements EventHandler<ActionEvent> {
	/**
	 * Stores the text field of which the text will be put out.
	 */
	private TextInputControl text;

	/**
	 * Creates a new instance.
	 *
	 * @param textInput the text field of which the text will be put out.
	 */
	public TextOutputEventHandler(final TextInputControl textInput) {
		text = textInput;
	}

	@Override
	public void handle(final ActionEvent event) {
		System.out.println(text.getText());
	}
}
