package edu.kit.ipd.pp.joframes.ui.cli;

import edu.kit.ipd.pp.joframes.api.Pipeline;
import edu.kit.ipd.pp.joframes.api.exceptions.ClassHierarchyCreationException;
import edu.kit.ipd.pp.joframes.api.exceptions.InstrumenterException;
import edu.kit.ipd.pp.joframes.api.exceptions.ParseException;

/**
 * This class provides a command-line interface.
 *
 * @author Martin Armbruster
 */
public final class CLIMain {
	/**
	 * Private constructor to avoid instantiation.
	 */
	private CLIMain() {
	}

	/**
	 * Main method for processing a framework and application.
	 *
	 * @param args the arguments.
	 */
	public static void main(final String[] args) {
		try {
			if (args.length == 0 || args[0].toLowerCase().equals("help")) {
				printUsage();
				return;
			}
			String frameworkSpecification = args[0];
			int counter = 0;
			for (int i = 1; i < args.length && !args[i].equals(";"); i++) {
				counter++;
			}
			String[] frameworkJars = new String[counter];
			for (int i = 1; i <= counter; i++) {
				frameworkJars[i - 1] = args[i];
			}
			counter += 2;
			int appCounter = 0;
			for (int i = counter; i < args.length && !args[i].equals(";"); i++) {
				appCounter++;
			}
			String[] applicationJars = new String[appCounter];
			for (int i = counter; i < counter + appCounter; i++) {
				applicationJars[i - counter] = args[i];
			}
			counter += appCounter + 1;
			String output = null;
			if (!args[counter].equals(";")) {
				output = args[counter];
				counter += 2;
			} else {
				counter++;
			}
			String mainClassName = null;
			if (counter < args.length) {
				mainClassName = args[counter];
			}
			Pipeline p = new Pipeline(frameworkSpecification, frameworkJars, applicationJars);
			p.setOutput(output);
			p.setMainClass(mainClassName);
			try {
				p.process();
			} catch (ParseException | ClassHierarchyCreationException | InstrumenterException e) {
				System.out.println("An exception occurred while processing the framework and its application: "
						+ e.getMessage());
				e.printStackTrace();
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("There were too few arguments: " + e.getMessage());
			printUsage();
		}
	}

	/**
	 * Prints the usage of the command-line interface.
	 */
	private static void printUsage() {
		System.out.println("Usage:");
		System.out.println("help or nothing prints this help. The framework and application are processed when the "
				+ "following arguments are given.");
		System.out.println("First argument: path to the framework specification.");
		System.out.println("(Optional) second to nth argument: paths to jar files containing framework classes.");
		System.out.println("Next argument: ;");
		System.out.println("Next to mth argument: paths to jar files containing application classes.");
		System.out.println("Next argument: ;");
		System.out.println("(Optional) next argument: path to the output jar file.");
		System.out.println("Next argument: ;");
		System.out.println("(Optional) next argument: name of class containing the main method.");
	}
}
