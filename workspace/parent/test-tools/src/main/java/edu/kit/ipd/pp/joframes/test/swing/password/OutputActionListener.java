package edu.kit.ipd.pp.joframes.test.swing.password;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.text.JTextComponent;

/**
 * This ActionListener prints the content of a JTextComponent out.
 *
 * @author Martin Armbruster
 */
public class OutputActionListener implements ActionListener {
	/**
	 * Stores the TextComponent.
	 */
	private JTextComponent text;

	/**
	 * Creates a new instance.
	 *
	 * @param textComponent the JTextComponent which text will be printed out.
	 */
	public OutputActionListener(final JTextComponent textComponent) {
		text = textComponent;
	}

	@Override
	public void actionPerformed(final ActionEvent event) {
		System.out.println(text.getText());
	}
}
