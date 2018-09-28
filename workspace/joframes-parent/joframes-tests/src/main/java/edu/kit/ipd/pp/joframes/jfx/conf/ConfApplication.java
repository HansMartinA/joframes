package edu.kit.ipd.pp.joframes.jfx.conf;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Main application for the JavaFX test case.
 *
 * @author Martin Armbruster
 */
public final class ConfApplication extends Application {
	/**
	 * Stores the TextField for public output.
	 */
	private TextField text;
	/**
	 * Stores a button.
	 */
	private Button button;

	/**
	 * Main method which starts the ConfApplication.
	 *
	 * @param args arguments.
	 */
	public static void main(final String[] args) {
		ConfApplication.launch(args);
	}

	@Override
	public void start(final Stage primaryStage) {
		VBox layout = new VBox();
		text = new TextField();
		button = new Button("Click me!");
		button.setOnAction(new ConfEventHandler(text));
		layout.getChildren().addAll(text, button);
		primaryStage.setScene(new Scene(layout));
		primaryStage.show();
	}
}
