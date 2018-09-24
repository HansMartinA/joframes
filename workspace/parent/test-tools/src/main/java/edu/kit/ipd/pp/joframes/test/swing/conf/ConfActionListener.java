package edu.kit.ipd.pp.joframes.test.swing.conf;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTextField;

/**
 * A simple ActionListener that shows a text in a TextField.
 *
 * @author Martin Armbruster
 */
public class ConfActionListener implements ActionListener {
	/**
	 * Stores a secret.
	 */
	private String secret = "secret";
	/**
	 * The TextField in which some text will be shown.
	 */
	private JTextField text;

	/**
	 * Creates a new instance.
	 *
	 * @param textField TextField in which some text will be shown.
	 */
	public ConfActionListener(final JTextField textField) {
		text = textField;
	}

	@Override
	public void actionPerformed(final ActionEvent event) {
		text.setText(secret);
	}
}
