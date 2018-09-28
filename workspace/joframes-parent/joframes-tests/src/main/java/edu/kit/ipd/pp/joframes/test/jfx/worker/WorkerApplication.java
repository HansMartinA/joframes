package edu.kit.ipd.pp.joframes.test.jfx.worker;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * An application that uses a Worker.
 *
 * @author Martin Armbruster
 */
public final class WorkerApplication extends Application {
	/**
	 * Stores the text field for output.
	 */
	private TextField text;
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
		WorkerApplication.launch(args);
	}

	@Override
	public void start(final Stage stage) {
		text = new TextField();
		button = new Button();
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				new Thread(new WorkerWorker(text)).start();
			}
		});
		VBox layout = new VBox();
		layout.getChildren().addAll(text, button);
		stage.setScene(new Scene(layout));
		stage.show();
	}
}
