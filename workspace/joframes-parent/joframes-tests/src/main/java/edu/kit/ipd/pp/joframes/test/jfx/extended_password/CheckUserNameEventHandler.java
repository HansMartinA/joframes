package edu.kit.ipd.pp.joframes.test.jfx.extended_password;

import java.io.FileWriter;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextInputControl;

/**
 * An EventHandler which checks the user name.
 *
 * @author Martin Armbruster
 */
public final class CheckUserNameEventHandler implements EventHandler<ActionEvent> {
	/**
	 * Stores the text field which contains the user name.
	 */
	private TextInputControl text;

	/**
	 * Creates a new instance.
	 *
	 * @param textInput the text field which contains the user name.
	 */
	public CheckUserNameEventHandler(final TextInputControl textInput) {
		text = textInput;
	}

	@Override
	public void handle(final ActionEvent event) {
		try (FileWriter writer = new FileWriter("UserName.txt")) {
			writer.append(text.getText());
		} catch (IOException e) {
		}
	}
}
