package edu.kit.ipd.pp.joframes.test.swing.no_ifc;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

/**
 * There is no information flow in this frame.
 *
 * @author Martin Armbruster
 */
public class FrameNo extends JFrame {
	/**
	 * Stores an input field.
	 */
	private JTextField text;
	/**
	 * Stores a button.
	 */
	private JButton button;

	/**
	 * Creates a new instance.
	 */
	public FrameNo() {
		super("Test");
		text = new JTextField();
		add(text);
		button = new JButton("Click me!");
		button.addActionListener(new NoActionListener(text));
		add(button);
		setVisible(true);
	}
}
