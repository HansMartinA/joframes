package edu.kit.ipd.pp.joframes.test.swing.extended_password;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.text.JTextComponent;

/**
 * An ActionListener that checks a user name.
 *
 * @author Martin Armbruster
 */
public final class CheckUserNameActionListener implements ActionListener {
	/**
	 * Stores the JTextComponent as input of the user name.
	 */
	private JTextComponent text;

	/**
	 * Creates a new instance.
	 *
	 * @param textComponent a JTextComponent as input for the user name.
	 */
	public CheckUserNameActionListener(final JTextComponent textComponent) {
		text = textComponent;
	}

	@Override
	public void actionPerformed(final ActionEvent event) {
		String userName = text.getText();
		try {
			URL url = new URL("http://example");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.addRequestProperty("user.name", userName);
			con.connect();
			con.disconnect();
		} catch (IOException e) {
		}
	}
}
