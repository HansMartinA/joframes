package edu.kit.ipd.pp.joframes.test.swing.swing_worker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

/**
 * Class that contains the frame for the Swing test case.
 *
 * @author Martin Armbruster
 */
public class SwingWorkerFrame {
	/**
	 * Stores the frame that will be displayed.
	 */
	private JFrame frame;
	/**
	 * Stores a JTextField for input and output.
	 */
	private JTextField text;
	/**
	 * Stores a button.
	 */
	private JButton button;

	/**
	 * Creates a new instance.
	 */
	public SwingWorkerFrame() {
		frame = new JFrame("Test");
		text = new JTextField();
		frame.add(text);
		button = new JButton("Click me!");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				new LongRunningSwingWorker(text).execute();
			}
		});
		frame.add(button);
		frame.setVisible(true);
	}
}
