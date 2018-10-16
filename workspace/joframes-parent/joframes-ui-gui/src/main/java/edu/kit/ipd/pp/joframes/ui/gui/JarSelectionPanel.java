package edu.kit.ipd.pp.joframes.ui.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * A panel to select jar files.
 *
 * @author Martin Armbruster
 */
class JarSelectionPanel extends JPanel {
	/**
	 * Pane for the list of jar files.
	 */
	private JScrollPane jarsPane;
	/**
	 * List of the jar files.
	 */
	private JList<String> jars;
	/**
	 * Stores the model for the list of jar files.
	 */
	private DefaultListModel<String> jarsModel;
	/**
	 * Button to add a jar file.
	 */
	private JButton addJar;
	/**
	 * Button to remove a jar file.
	 */
	private JButton removeJar;
	/**
	 * File chooser.
	 */
	private JFileChooser fileChooser;
	/**
	 * Stores the jar filter for the file chooser.
	 */
	private FileNameExtensionFilter jarFilter;

	/**
	 * Creates a new instance.
	 *
	 * @param jarType type of jar files.
	 */
	JarSelectionPanel(final String jarType) {
		createUI(jarType);
	}

	/**
	 * Creates the ui for the panel.
	 *
	 * @param jarType type of the jar files.
	 */
	private void createUI(final String jarType) {
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);

		JLabel curLabel = new JLabel(jarType + " jar files:");
		GridBagConstraints con = new GridBagConstraints();
		con.insets = new Insets(5, 5, 5, 5);
		con.gridx = 0;
		con.gridy = 0;
		con.gridwidth = 1;
		con.gridheight = 1;
		add(curLabel, con);

		jarsModel = new DefaultListModel<>();
		jars = new JList<>(jarsModel);
		jars.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jarsPane = new JScrollPane(jars);
		con.gridy++;
		con.gridheight = 2;
		add(jarsPane, con);

		addJar = new JButton("Add " + jarType + " jar file");
		addJar.addActionListener(event -> {
			String[] files = MainFrame.prepareAndShowFileChooser(fileChooser, jarFilter, true);
			for (String f : files) {
				jarsModel.addElement(f);
			}
		});
		con.gridx += 1;
		con.gridheight = 1;
		add(addJar, con);

		removeJar = new JButton("Remove " + jarType + " jar file");
		removeJar.addActionListener(event -> {
			String selVal = jars.getSelectedValue();
			if (selVal != null) {
				jarsModel.removeElement(selVal);
			}
		});
		con.gridy++;
		add(removeJar, con);
	}

	/**
	 * Sets the file chooser for this panel.
	 *
	 * @param chooser the file chooser.
	 * @param filter the file filter for the file chooser.
	 */
	void setJFileChooser(final JFileChooser chooser, final FileNameExtensionFilter filter) {
		fileChooser = chooser;
		jarFilter = filter;
	}

	/**
	 * Returns all choosen jar files.
	 *
	 * @return the jar files.
	 */
	String[] getJarFiles() {
		String[] jarFiles = new String[jarsModel.getSize()];
		for (int i = 0; i < jarFiles.length; i++) {
			jarFiles[i] = jarsModel.getElementAt(i);
		}
		return jarFiles;
	}
}
