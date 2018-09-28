package edu.kit.ipd.pp.joframes.jfx.extended_password;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * An application for user name and password input.
 *
 * @author Martin Armbruster
 */
public final class ExtendedPasswordApplication extends Application {
	/**
	 * Stores the text field for user name input.
	 */
	private TextField userName;
	/**
	 * Stores the text field for password input.
	 */
	private PasswordField password;
	/**
	 * Stores the button to check the user name.
	 */
	private Button checkUserName;
	/**
	 * Stores the button to check the password.
	 */
	private Button checkPassword;

	/**
	 * Main method for the JavaFX test case.
	 *
	 * @param args arguments.
	 */
	public static void main(final String[] args) {
		ExtendedPasswordApplication.launch(args);
	}

	@Override
	public void start(final Stage stage) {
		userName = new TextField();
		password = new PasswordField();
		checkUserName = new Button("Check user name");
		checkUserName.setOnAction(new CheckUserNameEventHandler(userName));
		checkPassword = new Button("Check password");
		checkPassword.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				password.getText();
			}
		});
		VBox layout = new VBox();
		layout.getChildren().addAll(userName, password, checkUserName, checkPassword);
		stage.setScene(new Scene(layout));
		stage.show();
	}
}
