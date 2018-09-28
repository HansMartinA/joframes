package edu.kit.ipd.pp.joframes.test.jfx.password;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Application which makes it possible for the user to put a password in.
 *
 * @author Martin Armbruster
 */
public final class PasswordApplication extends Application {
	/**
	 * Stores a password field for the password input.
	 */
	private PasswordField password;
	/**
	 * Stores a button.
	 */
	private Button button;

	/**
	 * Main method for the JavaFX test case.
	 *
	 * @param args arguments.
	 */
	public static void main(final String[] args) {
		PasswordApplication.launch(args);
	}

	@Override
	public void start(final Stage stage) {
		VBox layout = new VBox();
		password = new PasswordField();
		button = new Button("Click me!");
		button.setOnAction(new TextOutputEventHandler(password));
		layout.getChildren().addAll(password, button);
		stage.setScene(new Scene(layout));
		stage.show();
	}
}
