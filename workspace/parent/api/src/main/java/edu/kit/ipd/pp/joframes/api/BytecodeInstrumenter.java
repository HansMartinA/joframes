package edu.kit.ipd.pp.joframes.api;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.shrikeBT.CheckCastInstruction;
import com.ibm.wala.shrikeBT.ConditionalBranchInstruction;
import com.ibm.wala.shrikeBT.ConstantInstruction;
import com.ibm.wala.shrikeBT.DupInstruction;
import com.ibm.wala.shrikeBT.GotoInstruction;
import com.ibm.wala.shrikeBT.IConditionalBranchInstruction;
import com.ibm.wala.shrikeBT.IInvokeInstruction;
import com.ibm.wala.shrikeBT.InvokeInstruction;
import com.ibm.wala.shrikeBT.LoadInstruction;
import com.ibm.wala.shrikeBT.MethodEditor;
import com.ibm.wala.shrikeBT.NewInstruction;
import com.ibm.wala.shrikeBT.StoreInstruction;
import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;
import com.ibm.wala.shrikeBT.shrikeCT.OfflineInstrumenter;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.types.TypeReference;
import edu.kit.ipd.pp.joframes.api.exceptions.InstrumenterException;
import edu.kit.ipd.pp.joframes.ast.base.AstBaseClass;
import edu.kit.ipd.pp.joframes.ast.base.ExplicitDeclaration;
import edu.kit.ipd.pp.joframes.ast.base.Method;
import edu.kit.ipd.pp.joframes.ast.base.StaticMethod;
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
	 * Stores the next index for a local variable.
	 */
	private int nextLocalIndex;
	
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
					clInstr.visitMethods(data -> {
						MethodEditor editor = new MethodEditor(data);
						editor.beginPass();
						if(data.getName().equals(START)) {
							instrumentStartPhase(editor);
						} else if(data.getName().equals(WORKING)) {
							instrumentWorkingPhase(editor);
						} else if(data.getName().equals(END)) {
							instrumentEndPhase(editor);
						}
						editor.applyPatches();
						editor.endPass();
					});
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
	 * Resets the indices for local variables in case a new method is instrumented.
	 */
	private void resetLocalVariables() {
		nextLocalIndex = 1;
	}
	
	/**
	 * Gets the next free index for a local variable.
	 * 
	 * @return the next index.
	 */
	private int getNextLocalIndex() {
		return nextLocalIndex++;
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
	
	/**
	 * Instruments the bytecode with an explicit declaration.
	 * 
	 * @param editor the editor for the bytecode instrumentation.
	 * @param declaration the explicit declaration.
	 * @param instanceIndex local variable index of an instance that is put in.
	 */
	private void instrumentExplicitDeclaration(MethodEditor editor, ExplicitDeclaration declaration, int instanceIndex,
			String instanceType) {
		boolean useForLoop = declaration.getClassName()!=null;
		String actualInstanceType = instanceType;
		int iteratorIndex = 0;
		int localInstanceIndex = instanceIndex;
		int beginLoopLabel = 0;
		int afterLoopLabel = 0;
		if(useForLoop) {
			actualInstanceType = declaration.getClassName();
			iteratorIndex = getNextLocalIndex();
			localInstanceIndex = getNextLocalIndex();
			beginLoopLabel = editor.allocateLabel();
			afterLoopLabel = editor.allocateLabel();
			int iteratorIndexCopy = iteratorIndex;
			int localInstanceIndexCopy = localInstanceIndex;
			int beginLoopLabelCopy = beginLoopLabel;
			int afterLoopLabelCopy = afterLoopLabel;
			editor.insertAfterBody(new MethodEditor.Patch() {
				@Override
				public void emitTo(MethodEditor.Output w) {
					w.emit(ConstantInstruction.makeString(declaration.getClassName()));
					w.emit(InvokeInstruction.make("(Ljava/lang/String;)Ljava/lang/Class;",
							"Ljava/lang/Class", "forName", IInvokeInstruction.Dispatch.STATIC));
					w.emit(InvokeInstruction.make("(Ljava/lang/Class;)Ljava/util/List;",
							"L"+PACKAGE+"InstanceCollector", "getInstances",
							IInvokeInstruction.Dispatch.STATIC));
					w.emit(InvokeInstruction.make("()Ljava/util/Iterator", "Ljava/util/List", "iterator",
							IInvokeInstruction.Dispatch.INTERFACE));
					w.emit(StoreInstruction.make("Ljava/util/Iterator", iteratorIndexCopy));
					w.emitLabel(beginLoopLabelCopy);
					w.emit(LoadInstruction.make("Ljava/util/Iterator", iteratorIndexCopy));
					w.emit(InvokeInstruction.make("()Z", "Ljava/util/Iterator", "hasNext",
							IInvokeInstruction.Dispatch.INTERFACE));
					w.emit(ConstantInstruction.make(1));
					w.emit(ConditionalBranchInstruction.make("Z", IConditionalBranchInstruction.Operator.EQ,
							afterLoopLabelCopy));
					w.emit(LoadInstruction.make("Ljava/util/Iterator", iteratorIndexCopy));
					w.emit(InvokeInstruction.make("()Ljava/util/Object;", "Ljava/util/Iterator", "next",
							IInvokeInstruction.Dispatch.INTERFACE));
					w.emit(CheckCastInstruction.make(declaration.getClassName()));
					w.emit(StoreInstruction.make(declaration.getClassName(), localInstanceIndexCopy));
				}
			});
		}
		instrumentExplicitDeclarationContent(editor, declaration, localInstanceIndex, actualInstanceType);
		if(useForLoop) {
			int beginLoopLabelCopy = beginLoopLabel;
			int afterLoopLabelCopy = afterLoopLabel;
			editor.insertAfterBody(new MethodEditor.Patch() {
				@Override
				public void emitTo(MethodEditor.Output w) {
					w.emit(GotoInstruction.make(beginLoopLabelCopy));
					w.emitLabel(afterLoopLabelCopy);
				}
			});
		}
		for(IClass appClass : declaration.getApplicationClasses()) {
			instrumentExplicitDeclarationContent(editor, declaration, getNextLocalIndex(),
					appClass.getName().toString());
		}
	}
	
	/**
	 * Instruments the bytecode with the content of an explicit declaration.
	 * 
	 * @param editor the editor for bytecode instrumentation.
	 * @param declaration the explicit declaration.
	 * @param instanceIndex index where the local variable to use is stored.
	 * @param instanceType type of the local variable.
	 */
	private void instrumentExplicitDeclarationContent(MethodEditor editor, ExplicitDeclaration declaration,
			int instanceIndex, String instanceType) {
		for(int i=0; i<declaration.getNumberOfCallsAndDeclarations(); i++) {
			AstBaseClass abc = declaration.getCallOrDeclaration(i);
			if(abc.getClass()==Method.class) {
				Method m = (Method)abc;
				if(m.getSignature().equals("Constructor")) {
					editor.insertAfterBody(new MethodEditor.Patch() {
						@Override
						public void emitTo(MethodEditor.Output w) {
							w.emit(LoadInstruction.make("L"+PACKAGE+"ArtificialClass", 0));
							w.emit(NewInstruction.make(instanceType, 0));
							w.emit(DupInstruction.make(0));
						}
					});
					instantiateParameters(editor, m.getMethod());
					editor.insertAfterBody(new MethodEditor.Patch() {
						@Override
						public void emitTo(MethodEditor.Output w) {
							w.emit(InvokeInstruction.make("()V", "", "<init>", IInvokeInstruction.Dispatch.SPECIAL));
							w.emit(DupInstruction.make(0));
							w.emit(InvokeInstruction.make("(Ljava/lang/Object;)V", "L"+PACKAGE+"InstanceCollector",
									"addInstance", IInvokeInstruction.Dispatch.STATIC));
							w.emit(StoreInstruction.make(instanceType, instanceIndex));
						}
					});
					continue;
				}
				editor.insertAfterBody(new MethodEditor.Patch() {
					@Override
					public void emitTo(MethodEditor.Output w) {
						w.emit(LoadInstruction.make(instanceType, instanceIndex));
					}
				});
				instantiateParameters(editor, m.getMethod());
				editor.insertAfterBody(new MethodEditor.Patch() {
					@Override
					public void emitTo(MethodEditor.Output w) {
						w.emit(InvokeInstruction.make(m.getMethod().getDescriptor().toString(), instanceType,
								m.getMethod().getName().toString(), m.getMethod().getDeclaringClass()
								.isInterface()?IInvokeInstruction.Dispatch.INTERFACE
										:IInvokeInstruction.Dispatch.VIRTUAL));
					}
				});
			} else if(abc.getClass()==StaticMethod.class) {
				StaticMethod st = (StaticMethod)abc;
				editor.insertAfterBody(new MethodEditor.Patch() {
					@Override
					public void emitTo(MethodEditor.Output w) {
						w.emit(InvokeInstruction.make(st.getMethod().getDescriptor().toString(), st.getClassString(),
								st.getMethod().getName().toString(), IInvokeInstruction.Dispatch.STATIC));
					}
				});
			} else if(abc.getClass()==ExplicitDeclaration.class) {
				instrumentExplicitDeclaration(editor, (ExplicitDeclaration)abc, instanceIndex, instanceType);
			}
		}
	}
	
	/**
	 * Instantiates the parameters for a method.
	 * 
	 * @param editor the editor for bytecode instrumentation.
	 * @param method the method for which the parameters are created.
	 */
	private void instantiateParameters(MethodEditor editor, IMethod method) {
		editor.insertAfterBody(new MethodEditor.Patch() {
			@Override
			public void emitTo(MethodEditor.Output w) {
				for(int i=0; i<method.getNumberOfParameters(); i++) {
					TypeReference type = method.getParameterType(i);
					if(type.isPrimitiveType()) {
						w.emit(ConstantInstruction.make(type.getName().toString(), 0));
					} else {
						w.emit(ConstantInstruction.make(type.getName().toString(), null));
					}
				}
			}
		});
	}
	
	/**
	 * Instruments the bytecode for the start phase.
	 * 
	 * @param editor the editor for the start phase method.
	 */
	private void instrumentStartPhase(MethodEditor editor) {
		resetLocalVariables();
		
	}
	
	/**
	 * Instruments the bytecode for the end phase.
	 * 
	 * @param editor the editor for the end phase method.
	 */
	private void instrumentEndPhase(MethodEditor editor) {
		resetLocalVariables();
		instrumentExplicitDeclaration(editor, wrapper.getFramework().getEndPhase().getEnd(), 0, null);
	}
	
	/**
	 * Instruments the bytecode for the working phase.
	 * 
	 * @param editor the editor for the working phase method.
	 */
	private void instrumentWorkingPhase(MethodEditor editor) {
		resetLocalVariables();
	}
}
