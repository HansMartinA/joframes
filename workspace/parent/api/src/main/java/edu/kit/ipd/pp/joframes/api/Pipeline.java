package edu.kit.ipd.pp.joframes.api;

import edu.kit.ipd.pp.joframes.api.exceptions.ClassHierarchyCreationException;
import edu.kit.ipd.pp.joframes.api.exceptions.InstrumenterException;
import edu.kit.ipd.pp.joframes.api.exceptions.ParseException;
import edu.kit.ipd.pp.joframes.ast.base.Framework;

/**
 * The pipeline processes a framework specification and application to generate an artificial method for analysis
 * within Joana. 
 * 
 * @author Martin Armbruster
 */
public class Pipeline {
	/**
	 * Stores the path to the framework specification.
	 */
	private String frameworkSpecification;
	/**
	 * Stores all paths to the jar files containing the framework classes.
	 */
	private String[] frameworkJars;
	/**
	 * Stores all paths to the jar files containing the application classes.
	 */
	private String[] applicationJars;
	/**
	 * Stores the name of the class containing the main method.
	 */
	private String mainClassName;
	/**
	 * Stores the path to the ouput jar file.
	 */
	private String output;
	
	/**
	 * Creates a new instance.
	 * 
	 * @param frameworkSpecification path to the framework specification.
	 * @param frameworkJars all paths to the jar files containing the framework classes.
	 * @param applicationJars all paths to the jar files containing the application classes.
	 */
	public Pipeline(String frameworkSpecification, String[] frameworkJars, String[] applicationJars) {
		this.frameworkSpecification = frameworkSpecification;
		this.frameworkJars = frameworkJars;
		this.applicationJars = applicationJars;
	}
	
	/**
	 * Sets the output jar file.
	 * 
	 * @param output path to the output jar file.
	 */
	public void setOutput(String output) {
		this.output = output;
	}
	
	/**
	 * Returns the path to the output jar file.
	 * 
	 * @return the path to the output jar file or null if no one is set and the framework is not yet processed.
	 */
	public String getOutput() {
		return output;
	}
	
	/**
	 * Sets the name of the class containing the main method.
	 * 
	 * @param mainClassName the class name.
	 */
	public void setMainClass(String mainClassName) {
		this.mainClassName = mainClassName;
	}
	
	/**
	 * Processes the framework and application.
	 * 
	 * @throws ParseException when parsing the framework specification fails.
	 * @throws ClassHierarchyCreationException when the creation of the class hierarchy fails.
	 * @throws InstrumenterException when the instrumentation of the bytecode fails.
	 */
	public void process() throws ParseException, ClassHierarchyCreationException, InstrumenterException {
		FrameworkSpecificationParser parser = new FrameworkSpecificationParser();
		Framework framework = parser.parse(frameworkSpecification);
		ClassHierarchyAnalyzer analyzer = new ClassHierarchyAnalyzer();
		FrameworkWrapper wrapper;
		if(mainClassName==null) {
			wrapper = analyzer.analyzeClassHierarchy(framework, frameworkJars, applicationJars);
		} else {
			wrapper = analyzer.analyzeClassHierarchy(framework, frameworkJars, applicationJars, mainClassName);
		}
		BytecodeInstrumenter instrumenter = new BytecodeInstrumenter();
		if(output==null) {
			instrumenter.instrumentBytecode(wrapper, applicationJars);
		} else {
			instrumenter.instrumentBytecode(wrapper, applicationJars, output);
		}
		this.output = instrumenter.getOutput();
	}
}
