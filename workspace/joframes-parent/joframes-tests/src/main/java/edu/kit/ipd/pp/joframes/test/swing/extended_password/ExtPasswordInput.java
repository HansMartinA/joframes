package edu.kit.ipd.pp.joframes.test.swing.extended_password;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * A frame where the user can put in a user name and password.
 *
 * @author Martin Armbruster
 */
public class ExtPasswordInput extends JFrame {
	/**
	 * Stores the JTextField for putting in the user name.
	 */
	private JTextField userName;
	/**
	 * Stores the JPasswordField for putting in the password.
	 */
	private JPasswordField password;
	/**
	 * Button to check the user name.
	 */
	private JButton acceptName;
	/**
	 * Button to check the password.
	 */
	private JButton acceptPassword;

	/**
	 * Creates a new instance.
	 */
	public ExtPasswordInput() {
		super("Test");
		userName = new JTextField();
		add(userName);
		password = new JPasswordField();
		add(password);
		acceptName = new JButton("Check name");
		acceptName.addActionListener(new CheckUserNameActionListener(userName));
		add(acceptName);
		acceptPassword = new JButton("Check password");
		acceptPassword.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				String o = password.getText();
			}
		});
		add(acceptPassword);
		setVisible(true);
	}
}
