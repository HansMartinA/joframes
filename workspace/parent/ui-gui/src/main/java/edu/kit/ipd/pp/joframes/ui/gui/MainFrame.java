package edu.kit.ipd.pp.joframes.ui.gui;

import edu.kit.ipd.pp.joframes.api.Pipeline;
import edu.kit.ipd.pp.joframes.api.exceptions.ClassHierarchyCreationException;
import edu.kit.ipd.pp.joframes.api.exceptions.InstrumenterException;
import edu.kit.ipd.pp.joframes.api.exceptions.ParseException;
import edu.kit.joana.ui.ifc.wala.console.gui.IFCConsoleGUI;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Main GUI frame for the [Framework Project].
 *
 * @author Martin Armbruster
 */
public class MainFrame extends JFrame {
	/**
	 * Stores the layout of the frame.
	 */
	private GridBagLayout layout;
	/**
	 * Stores the constraints for the layout.
	 */
	private GridBagConstraints con;
	/**
	 * Stores the path of the framework specification.
	 */
	private JTextField frameworkSpec;
	/**
	 * Button to choose the framework specification.
	 */
	private JButton chooseFrameworkSpec;
	/**
	 * Pane for the list of framework jar files.
	 */
	private JScrollPane frameworkJarsPane;
	/**
	 * List of the framework jar files.
	 */
	private JList<String> frameworkJars;
	/**
	 * Stores the model for the list of framework jar files.
	 */
	private DefaultListModel<String> frameworkJarsModel;
	/**
	 * Button to add a framework jar file.
	 */
	private JButton addFramework;
	/**
	 * Button to remove a framework jar file.
	 */
	private JButton removeFramework;
	/**
	 * Pane for the list of application jar files.
	 */
	private JScrollPane applicationJarsPane;
	/**
	 * List of the application jar files.
	 */
	private JList<String> applicationJars;
	/**
	 * Stores the model for the list of application jar files.
	 */
	private DefaultListModel<String> applicationJarsModel;
	/**
	 * Button to add an application jar file.
	 */
	private JButton addApplication;
	/**
	 * Button to remove an application jar file.
	 */
	private JButton removeApplication;
	/**
	 * Text field for the output jar file.
	 */
	private JTextField outputJar;
	/**
	 * Button to choose the output jar file.
	 */
	private JButton chooseOutputJar;
	/**
	 * CheckBox for the user to choose to start Joana after the instrumentation.
	 */
	private JCheckBox joanaConnection;
	/**
	 * Button to run the analysis and instrumentation.
	 */
	private JButton run;
	/**
	 * Generic file chooser.
	 */
	private JFileChooser fileChooser;
	/**
	 * Stores the jar filter for the file chooser.
	 */
	private FileNameExtensionFilter jarFilter;
	/**
	 * Stores the xml filter for the file chooser.
	 */
	private FileNameExtensionFilter xmlFilter;

	/**
	 * Creates a new instance.
	 */
	public MainFrame() {
		super("");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createComponents();
		layoutComponents();
		addActionListeners();
		setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	/**
	 * Creates all components for the frame.
	 */
	private void createComponents() {
		frameworkSpec = new JTextField();
		frameworkSpec.setPreferredSize(new Dimension(250, 30));
		frameworkSpec.setEditable(false);
		chooseFrameworkSpec = new JButton("Choose framework specification");
		frameworkJarsModel = new DefaultListModel<>();
		frameworkJars = new JList<>(frameworkJarsModel);
		frameworkJars.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		frameworkJarsPane = new JScrollPane(frameworkJars);
		addFramework = new JButton("Add framework jar file");
		removeFramework = new JButton("Remove framework jar file");
		applicationJarsModel = new DefaultListModel<>();
		applicationJars = new JList<>(applicationJarsModel);
		applicationJars.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		applicationJarsPane = new JScrollPane(applicationJars);
		addApplication = new JButton("Add application jar file");
		removeApplication = new JButton("Remove application jar file");
		outputJar = new JTextField();
		outputJar.setPreferredSize(frameworkSpec.getPreferredSize());
		outputJar.setEditable(false);
		chooseOutputJar = new JButton("Choose output jar file");
		joanaConnection = new JCheckBox("Start Joana after instrumentation");
		run = new JButton("Run analysis and instrumentation");
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setMultiSelectionEnabled(false);
		jarFilter = new FileNameExtensionFilter("jar file(s)", "jar");
		xmlFilter = new FileNameExtensionFilter("XML file", "xml");
	}

	/**
	 * Layouts the frame.
	 */
	private void layoutComponents() {
		layout = new GridBagLayout();
		setLayout(layout);
		layoutSpecAndFrameworkSelection();
		layoutApplicationAndOutputSelection();
		layoutEnd();
	}

	/**
	 * Layouts the selection of the framework specification and framework jar files.
	 */
	private void layoutSpecAndFrameworkSelection() {
		con = new GridBagConstraints();
		con.insets = new Insets(5, 5, 5, 5);
		con.gridx = 0;
		con.gridy = 0;
		con.gridwidth = 1;
		con.gridheight = 1;
		JLabel curLabel = new JLabel("Framework specification:");
		add(curLabel, con);
		con.gridx++;
		add(frameworkSpec, con);
		con.gridx++;
		add(chooseFrameworkSpec, con);
		curLabel = new JLabel("Framework jar files:");
		con.gridx = 0;
		con.gridy++;
		con.gridwidth = 2;
		add(curLabel, con);
		con.gridy++;
		con.gridwidth = 2;
		con.gridheight = 2;
		add(frameworkJarsPane, con);
		con.gridx += 2;
		con.gridwidth = 1;
		con.gridheight = 1;
		add(addFramework, con);
		con.gridy++;
		add(removeFramework, con);
	}

	/**
	 * Layouts the selection of the application jar files and the output jar file.
	 */
	private void layoutApplicationAndOutputSelection() {
		JLabel curLabel = new JLabel("Application jar files:");
		con.gridx = 0;
		con.gridy++;
		con.gridwidth = 2;
		add(curLabel, con);
		con.gridy++;
		con.gridwidth = 2;
		con.gridheight = 2;
		add(applicationJarsPane, con);
		con.gridx += 2;
		con.gridwidth = 1;
		con.gridheight = 1;
		add(addApplication, con);
		con.gridy++;
		add(removeApplication, con);
		curLabel = new JLabel("Output jar file:");
		con.gridx = 0;
		con.gridy++;
		add(curLabel, con);
		con.gridx++;
		add(outputJar, con);
		con.gridx++;
		add(chooseOutputJar, con);
	}

	/**
	 * Layouts the end of the frame.
	 */
	private void layoutEnd() {
		con.gridx = 0;
		con.gridy++;
		con.gridwidth = 3;
		add(joanaConnection, con);
		con.gridx = 1;
		con.gridy++;
		con.gridwidth = 1;
		add(run, con);
	}

	/**
	 * Adds ActionListeners to the buttons.
	 */
	private void addActionListeners() {
		chooseFrameworkSpec.addActionListener(event -> {
			String[] file = prepareAndShowFileChooser(xmlFilter, false);
			if (file.length == 1) {
				frameworkSpec.setText(file[0]);
			}
		});
		addFramework.addActionListener(event -> {
			String[] files = prepareAndShowFileChooser(jarFilter, true);
			for (String f : files) {
				frameworkJarsModel.addElement(f);
			}
		});
		removeFramework.addActionListener(event -> {
			String selVal = frameworkJars.getSelectedValue();
			if (selVal != null) {
				frameworkJarsModel.removeElement(selVal);
			}
		});
		addApplication.addActionListener(event -> {
			String[] files = prepareAndShowFileChooser(jarFilter, true);
			for (String f : files) {
				applicationJarsModel.addElement(f);
			}
		});
		removeApplication.addActionListener(event -> {
			String selVal = applicationJars.getSelectedValue();
			if (selVal != null) {
				applicationJarsModel.removeElement(selVal);
			}
		});
		chooseOutputJar.addActionListener(event -> {
			String[] file = prepareAndShowFileChooser(jarFilter, false);
			if (file.length == 1) {
				outputJar.setText(file[0]);
			}
		});
		run.addActionListener(event -> {
			String fwSpecPath = frameworkSpec.getText();
			String[] fwJars = new String[frameworkJarsModel.getSize()];
			for (int i = 0; i < fwJars.length; i++) {
				fwJars[i] = frameworkJarsModel.getElementAt(i);
			}
			String[] appJars = new String[applicationJarsModel.getSize()];
			for (int i = 0; i < appJars.length; i++) {
				appJars[i] = applicationJarsModel.getElementAt(i);
			}
			String output = outputJar.getText();
			run.setEnabled(false);
			new Thread(() -> {
				Pipeline p = new Pipeline(fwSpecPath, fwJars, appJars);
				p.setOutput(output);
				try {
					p.process();
				} catch (ParseException | ClassHierarchyCreationException | InstrumenterException e) {
					SwingUtilities.invokeLater(() -> JOptionPane.showConfirmDialog(this, "An exception occurred: "
							+ e.getMessage() + ". Cause: " + e.getCause().getMessage(), "Occurred exception",
							JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE));
				}
				SwingUtilities.invokeLater(() -> {
					if (joanaConnection.isSelected()) {
						IFCConsoleGUI.main(new String[] {});
					}
					run.setEnabled(true);
				});
			}).start();
		});
	}

	/**
	 * Prepares the file chooser with the appropriate file extension filter and displays it to the user.
	 *
	 * @param filter the file extension filter to be used.
	 * @param multiSelection true if multiple files can be selected. false otherwise.
	 * @return an array of all selected files. If multiSelection is set to false, the array contains only one element.
	 *         If the user selected no file, the array is empty.
	 */
	private String[] prepareAndShowFileChooser(final FileNameExtensionFilter filter, final boolean multiSelection) {
		fileChooser.resetChoosableFileFilters();
		fileChooser.setFileFilter(filter);
		fileChooser.setMultiSelectionEnabled(multiSelection);
		int result = fileChooser.showDialog(this, "Select");
		if (result == JFileChooser.APPROVE_OPTION) {
			if (!multiSelection) {
				return new String[] {fileChooser.getSelectedFile().getAbsolutePath()};
			}
			File[] selectedFiles = fileChooser.getSelectedFiles();
			String[] resultArray = new String[selectedFiles.length];
			for (int i = 0; i < selectedFiles.length; i++) {
				resultArray[i] = selectedFiles[i].getAbsolutePath();
			}
			return resultArray;
		}
		return new String[] {};
	}
}
