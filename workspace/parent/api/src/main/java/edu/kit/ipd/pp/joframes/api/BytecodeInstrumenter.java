package edu.kit.ipd.pp.joframes.api;

import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;
import com.ibm.wala.shrikeBT.shrikeCT.OfflineInstrumenter;
import com.ibm.wala.shrikeCT.InvalidClassFileException;

import edu.kit.ipd.pp.joframes.api.exceptions.InstrumenterException;
import java.io.File;
import java.io.IOException;

/**
 * Generates the artificial method out of the abstract syntax tree.
 * 
 * @author Martin Armbruster
 */
class BytecodeInstrumenter {
	/**
	 * Name of the package containing the external api classes.
	 */
	private static final String PACKAGE = "edu/kit/ipd/pp/joframes/api/external/";
	/**
	 * Format for the jar file containing the external api classes.
	 */
	private static final String OWN_JAR_FILE = ".jar#"+PACKAGE;
	/**
	 * Name of the InstanceCollector class.
	 */
	private static final String IC_NAME = "InstanceCollector.class";
	/**
	 * Name of the ArtifcialClass class.
	 */
	private static final String AC_NAME = "ArtificialClass.class";
	
	/**
	 * Instruments the bytecode for a specific framework and application.
	 * 
	 * @param wrapper the framework with the results of the class hierarchy analysis.
	 * @param applicationJars paths to the jar files containing the application classes.
	 */
	void instrumentBytecode(FrameworkWrapper wrapper, String[] applicationJars) throws InstrumenterException {
		try {
			OfflineInstrumenter offInstr = new OfflineInstrumenter();
			offInstr.setPassUnmodifiedClasses(true);
			offInstr.setOutputJar(new File("instrumented-application-of-"+wrapper.getFramework().getName()+".jar"));
			for(String appJar : applicationJars) {
				offInstr.addInputJar(new File(appJar));
			}
			// At the first try to load the external api classes, they are searched in the jar file corresponding to
			// this application. Otherwise, it is assumed that they are used in the eclipse project.
			if(!offInstr.addInputElement(new File("."), OWN_JAR_FILE+IC_NAME)) {
				String path = "target/classes/"+PACKAGE;
				offInstr.addInputClass(new File(path+IC_NAME), new File(path+IC_NAME));
				offInstr.addInputClass(new File(path+AC_NAME), new File(path+AC_NAME));
			} else {
				offInstr.addInputElement(new File("."), OWN_JAR_FILE+AC_NAME);
			}
			offInstr.beginTraversal();
			for(int i=0; i<offInstr.getNumInputClasses(); i++) {
				ClassInstrumenter clInstr = offInstr.nextClass();
				if(clInstr.getInputName().endsWith(PACKAGE+IC_NAME)) {
					
					clInstr.emitClass();
					break;
				}
			}
			offInstr.beginTraversal();
			
			offInstr.beginTraversal();
			for(int i=0; i<offInstr.getNumInputClasses(); i++) {
				ClassInstrumenter clInstr = offInstr.nextClass();
				if(clInstr.getInputName().endsWith(PACKAGE+AC_NAME)) {
					
					clInstr.emitClass();
					break;
				}
			}
		} catch(IOException e) {
			throw new InstrumenterException("An IO exception occurred while instrumenting the bytecode.", e);
		} catch(InvalidClassFileException e) {
			throw new InstrumenterException("Bytcode instrumentation resulted in an invalid class.", e);
		}
	}
}
