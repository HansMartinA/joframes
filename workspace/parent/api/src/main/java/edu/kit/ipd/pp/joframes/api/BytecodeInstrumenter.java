package edu.kit.ipd.pp.joframes.api;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.shrikeBT.ConstantInstruction;
import com.ibm.wala.shrikeBT.DupInstruction;
import com.ibm.wala.shrikeBT.IInvokeInstruction;
import com.ibm.wala.shrikeBT.InvokeInstruction;
import com.ibm.wala.shrikeBT.MethodEditor;
import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;
import com.ibm.wala.shrikeBT.shrikeCT.OfflineInstrumenter;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.types.TypeReference;

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
	 * Name of a constructor in bytecode.
	 */
	private static final String INIT = "<init>";
	/**
	 * Name of the static initializer method.
	 */
	private static final String CLINIT = "<clinit>";
	/**
	 * Name of the method in which the start phase instructions are put.
	 */
	private static final String START = "start";
	/**
	 * Name of the method in which the working phase instructions are put.
	 */
	private static final String WORKING = "working";
	/**
	 * Name of the method in which the end phase instructions are put.
	 */
	private static final String END = "end";
	/**
	 * Stores the wrapper for the framework.
	 */
	private FrameworkWrapper wrapper;
	
	/**
	 * Instruments the bytecode for a specific framework and application.
	 * 
	 * @param wrapper the framework with the results of the class hierarchy analysis.
	 * @param applicationJars paths to the jar files containing the application classes.
	 */
	void instrumentBytecode(FrameworkWrapper wrapper, String[] applicationJars) throws InstrumenterException {
		this.wrapper = wrapper;
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
					clInstr.visitMethods(data -> {
						if(data.getName().equals(CLINIT)) {
							MethodEditor editor = new MethodEditor(data);
							editor.beginPass();
							editor.insertAfterBody(new MethodEditor.Patch() {
								@Override
								public void emitTo(MethodEditor.Output w) {
									for(IClass cl : wrapper.getFrameworkClasses()) {
										// WALA classes stores class names in the bytecode format, but the used method
										// Class.forName requires a full-qualified class name. Therefore, the bytecode
										// name is converted to a fully qualified class name.
										w.emit(ConstantInstruction.makeString(cl.getName().toString()
												.substring(1).replace("/", ".")));
										w.emit(InvokeInstruction.make("(Ljava/lang/String;)Ljava/lang/Class;",
												"Ljava/lang/Class", "forName", IInvokeInstruction.Dispatch.STATIC));
										w.emit(InvokeInstruction.make("(Ljava/lang/Class;)V",
												"L"+PACKAGE+"InstanceCollector", "addClass",
												IInvokeInstruction.Dispatch.STATIC));
									}
								}
							});
							editor.applyPatches();
							editor.endPass();
						}
					});
					clInstr.emitClass();
					break;
				}
			}
			offInstr.beginTraversal();
			for(int i=0; i<offInstr.getNumInputClasses(); i++) {
				ClassInstrumenter clInstr = offInstr.nextClass();
				clInstr.visitMethods(data -> {
					MethodEditor editor = new MethodEditor(data);
					if(editor.getData().getClassType().contains(PACKAGE)) {
						return;
					}
					editor.beginPass();
					editor.visitInstructions(new MethodEditor.Visitor() {
						@Override
						public void visitInvoke(IInvokeInstruction instruction) {
							if(instruction.getMethodName().equals(INIT)
									&&isSubclassOfFrameworkClasses(instruction.getClassType())) {
								this.insertAfter(new MethodEditor.Patch() {
									@Override
									public void emitTo(MethodEditor.Output w) {
										w.emit(DupInstruction.make(0));
										w.emit(InvokeInstruction.make("(Ljava/lang/Object;)V",
												"L"+PACKAGE+"InstanceCollector", "addInstance",
												IInvokeInstruction.Dispatch.STATIC));
									}
								});
							}
						}
					});
					editor.applyPatches();
					editor.endPass();
				});
				clInstr.emitClass();
			}
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
	
	/**
	 * Checks if a class is a subclass of a framework class.
	 * 
	 * @param className name of the class that is checked.
	 * @return true if the class is a subclass of a framework class. false otherwise.
	 */
	private boolean isSubclassOfFrameworkClasses(String className) {
		IClass subclass = wrapper.getClassHierarchy().lookupClass(TypeReference.findOrCreate
				(wrapper.getClassHierarchy().getScope().getApplicationLoader(), className));
		for(IClass cl : wrapper.getFrameworkClasses()) {
			if(wrapper.getClassHierarchy().isSubclassOf(subclass, cl)) {
				return true;
			}
		}
		return false;
	}
}
