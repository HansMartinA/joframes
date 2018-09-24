package edu.kit.ipd.pp.joframes.test.swing.password;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPasswordField;

/**
 * This class provides an password input for the user.
 *
 * @author Martin Armbruster
 */
public class PasswordInput implements KeyListener {
	/**
	 * Stores the displayed window.
	 */
	private JFrame frame;
	/**
	 * Stores the actual password input field.
	 */
	private JPasswordField pwInput;
	/**
	 * Stores the button to cancel the input.
	 */
	private JButton cancel;
	/**
	 * Stores the button which proceeds.
	 */
	private JButton click;

	/**
	 * Creates a new instance.
	 */
	public PasswordInput() {
		frame = new JFrame("Password Input");
		frame.addKeyListener(this);
		pwInput = new JPasswordField();
		frame.add(pwInput);
		cancel = new JButton("Cancel");
		cancel.addActionListener(event -> System.exit(0));
		frame.add(cancel);
		click = new JButton("Click me!");
		click.addActionListener(new OutputActionListener(pwInput));
		frame.add(click);
		frame.setVisible(true);
	}

	@Override
	public void keyPressed(final KeyEvent e) {
	}

	@Override
	public void keyReleased(final KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_F1) {
			pwInput.setText("");
		}
	}

	@Override
	public void keyTyped(final KeyEvent e) {
	}
}
