package edu.kit.ipd.pp.joframes.test.swing.conf;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

/**
 * This class contains and shows the main frame for the Swing application.
 *
 * @author Martin Armbruster
 */
public class MainFrame {
	/**
	 * Creates a new instance.
	 */
	public MainFrame() {
		JFrame frame = new JFrame("Test");
		JTextField text = new JTextField();
		JButton button = new JButton("Click me!");
		button.addActionListener(new ConfActionListener(text));
		frame.add(text);
		frame.add(button);
		frame.setVisible(true);
	}
}
