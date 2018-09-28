package edu.kit.ipd.pp.joframes.test.swing.no_if;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTextField;

/**
 * ActionListener that uses input text for an output.
 *
 * @author Martin Armbruster
 */
public final class NoActionListener implements ActionListener {
	/**
	 * Stores the JTextField used for an output.
	 */
	private JTextField text;

	/**
	 * Creates a new instance.
	 *
	 * @param textField JTextField which text is used for an output.
	 */
	public NoActionListener(final JTextField textField) {
		text = textField;
	}

	@Override
	public void actionPerformed(final ActionEvent event) {
		String output;
		if (text.getText().equals("abc")) {
			output = "a";
		} else {
			output = "b";
		}
		output = "c";
		System.out.println(output);
	}
}
