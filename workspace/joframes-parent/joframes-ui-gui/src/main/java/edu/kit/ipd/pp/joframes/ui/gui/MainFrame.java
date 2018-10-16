package edu.kit.ipd.pp.joframes.ui.gui;

import edu.kit.ipd.pp.joframes.api.Pipeline;
import edu.kit.joana.ui.ifc.wala.console.gui.IFCConsoleGUI;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Main GUI frame for JoFrames.
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
	 * Selection panel for the framework jar files.
	 */
	private JarSelectionPanel frameworkSelection;
	/**
	 * Selection panel for the application jar files.
	 */
	private JarSelectionPanel applicationSelection;
	/**
	 * Text field for the output jar file.
	 */
	private JTextField outputJar;
	/**
	 * Button to choose the output jar file.
	 */
	private JButton chooseOutputJar;
	/**
	 * Text field for putting in the main class.
	 */
	private JTextField mainClass;
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
		super("JoFrames");
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
		frameworkSelection = new JarSelectionPanel("framework");
		applicationSelection = new JarSelectionPanel("application");
		outputJar = new JTextField();
		outputJar.setPreferredSize(frameworkSpec.getPreferredSize());
		outputJar.setEditable(false);
		chooseOutputJar = new JButton("Choose output jar file");
		mainClass = new JTextField();
		mainClass.setPreferredSize(frameworkSpec.getPreferredSize());
		joanaConnection = new JCheckBox("Start Joana after instrumentation");
		run = new JButton("Run analysis and instrumentation");
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setMultiSelectionEnabled(false);
		jarFilter = new FileNameExtensionFilter("jar file(s)", "jar");
		xmlFilter = new FileNameExtensionFilter("XML file", "xml");
		frameworkSelection.setJFileChooser(fileChooser, jarFilter);
		applicationSelection.setJFileChooser(fileChooser, jarFilter);
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
		con.gridx = 0;
		con.gridy++;
		con.gridwidth = 3;
		add(frameworkSelection, con);
	}

	/**
	 * Layouts the selection of the application jar files and the output jar file.
	 */
	private void layoutApplicationAndOutputSelection() {
		con.gridy++;
		add(applicationSelection, con);
		JLabel curLabel = new JLabel("Class with main method to use:");
		con.gridy++;
		con.gridwidth = 1;
		add(curLabel, con);
		con.gridx++;
		add(mainClass, con);
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
			String[] file = prepareAndShowFileChooser(fileChooser, xmlFilter, false);
			if (file.length == 1) {
				frameworkSpec.setText(file[0]);
			}
		});
		chooseOutputJar.addActionListener(event -> {
			String[] file = prepareAndShowFileChooser(fileChooser, jarFilter, false);
			if (file.length == 1) {
				outputJar.setText(file[0]);
			}
		});
		run.addActionListener(event -> {
			String fwSpecPath = frameworkSpec.getText();
			String[] fwJars = frameworkSelection.getJarFiles();
			String[] appJars = applicationSelection.getJarFiles();
			String main = mainClass.getText();
			String output = outputJar.getText();
			if (fwSpecPath == null || fwSpecPath.equals("") || fwJars.length == 0 || appJars.length == 0) {
				return;
			}
			run.setEnabled(false);
			new Thread(() -> {
				Pipeline p = new Pipeline(fwSpecPath, fwJars, appJars);
				if (output != null && !output.equals("")) {
					p.setOutput(output);
				}
				if (main != null && !main.equals("")) {
					p.setMainClass(main);
				}
				try {
					p.process();
					SwingUtilities.invokeLater(() -> {
						if (joanaConnection.isSelected()) {
							IFCConsoleGUI.main(new String[] {});
						}
						outputJar.setText(p.getOutput());
					});
				} catch (Exception e) {
					e.printStackTrace();
					SwingUtilities.invokeLater(() -> JOptionPane.showConfirmDialog(this, "An exception occurred: "
							+ e.getMessage() + "."
							+ (e.getCause() != null ? " Cause: " + e.getCause().getMessage() : ""),
							"Occurred exception", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE));
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
	 * Prepares a file chooser with the appropriate file extension filter and displays it to the user.
	 *
	 * @param chooser the file chooser to display.
	 * @param filter the file extension filter to be used.
	 * @param multiSelection true if multiple files can be selected. false otherwise.
	 * @return an array of all selected files. If multiSelection is set to false, the array contains only one element.
	 *         If the user selected no file, the array is empty.
	 */
	static String[] prepareAndShowFileChooser(final JFileChooser chooser, final FileNameExtensionFilter filter,
			final boolean multiSelection) {
		chooser.resetChoosableFileFilters();
		chooser.setFileFilter(filter);
		chooser.setMultiSelectionEnabled(multiSelection);
		chooser.setSelectedFile(new File(""));
		chooser.setSelectedFiles(new File[] {new File("")});
		int result = chooser.showDialog(null, "Select");
		if (result == JFileChooser.APPROVE_OPTION) {
			if (!multiSelection) {
				return new String[] {chooser.getSelectedFile().getAbsolutePath()};
			}
			File[] selectedFiles = chooser.getSelectedFiles();
			String[] resultArray = new String[selectedFiles.length];
			for (int i = 0; i < selectedFiles.length; i++) {
				resultArray[i] = selectedFiles[i].getAbsolutePath();
			}
			return resultArray;
		}
		return new String[] {};
	}
}
