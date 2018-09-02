package edu.kit.ipd.pp.joframes.api;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.shrikeBT.ArrayLoadInstruction;
import com.ibm.wala.shrikeBT.ArrayStoreInstruction;
import com.ibm.wala.shrikeBT.BinaryOpInstruction;
import com.ibm.wala.shrikeBT.CheckCastInstruction;
import com.ibm.wala.shrikeBT.ConditionalBranchInstruction;
import com.ibm.wala.shrikeBT.ConstantInstruction;
import com.ibm.wala.shrikeBT.Constants;
import com.ibm.wala.shrikeBT.DupInstruction;
import com.ibm.wala.shrikeBT.GetInstruction;
import com.ibm.wala.shrikeBT.GotoInstruction;
import com.ibm.wala.shrikeBT.IBinaryOpInstruction;
import com.ibm.wala.shrikeBT.IConditionalBranchInstruction;
import com.ibm.wala.shrikeBT.IInvokeInstruction;
import com.ibm.wala.shrikeBT.InvokeInstruction;
import com.ibm.wala.shrikeBT.LoadInstruction;
import com.ibm.wala.shrikeBT.MethodData;
import com.ibm.wala.shrikeBT.MethodEditor;
import com.ibm.wala.shrikeBT.NewInstruction;
import com.ibm.wala.shrikeBT.StoreInstruction;
import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;
import com.ibm.wala.shrikeBT.shrikeCT.OfflineInstrumenter;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.types.TypeReference;
import edu.kit.ipd.pp.joframes.api.exceptions.InstrumenterException;
import edu.kit.ipd.pp.joframes.ast.acha.MethodCollector;
import edu.kit.ipd.pp.joframes.ast.ap.Block;
import edu.kit.ipd.pp.joframes.ast.base.AstBaseClass;
import edu.kit.ipd.pp.joframes.ast.base.ExplicitDeclaration;
import edu.kit.ipd.pp.joframes.ast.base.Method;
import edu.kit.ipd.pp.joframes.ast.base.Rule;
import edu.kit.ipd.pp.joframes.ast.base.StaticMethod;
import edu.kit.ipd.pp.joframes.ast.base.ThreadType;
import edu.kit.ipd.pp.joframes.ast.base.WorkingPhase;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Generates the artificial method out of the abstract syntax tree.
 * 
 * @author Martin Armbruster
 */
class BytecodeInstrumenter {
	/**
	 * Default name for the output jar with the instrumented bytecode.
	 */
	private static final String DEFAULT_OUTPUT_NAME = "instrumented-application-of-%s.jar";
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
	 * Bytecode name of the ArtificialClass class.
	 */
	private static final String AC_BYTECODE_NAME = "L"+PACKAGE+"ArtificialClass";
	/**
	 * Name of the WorkingWorker class within the artificial class.
	 */
	private static final String AC_WW_NAME = "ArtificialClass$WorkingWorker.class";
	/**
	 * Bytecode name of the WorkingWorker class within the ArtificialClass class.
	 */
	private static final String AC_WW_BYTECODE_NAME = AC_BYTECODE_NAME+"$WorkingWorker";
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
	 * @throws InstrumenterException when an exception occurs during instrumentation.
	 */
	void instrumentBytecode(FrameworkWrapper wrapper, String[] applicationJars) throws InstrumenterException {
		instrumentBytecode(wrapper, applicationJars,
				String.format(DEFAULT_OUTPUT_NAME, wrapper.getFramework().getName()));
	}
	
	/**
	 * Instruments the bytecode for a specific framework and application.
	 * 
	 * @param wrapper the framework with the results of the class hierarchy analysis.
	 * @param applicationJars paths to the jar files containing the application classes.
	 * @param output name of the output jar with the instrumented bytecode.
	 * @throws InstrumenterException when an exception occurs during instrumentation.
	 */
	void instrumentBytecode(FrameworkWrapper wrapper, String[] applicationJars, String output)
			throws InstrumenterException {
		this.wrapper = wrapper;
		try {
			OfflineInstrumenter offInstr = new OfflineInstrumenter();
			offInstr.setPassUnmodifiedClasses(true);
			offInstr.setOutputJar(new File(output));
			for(String appJar : applicationJars) {
				offInstr.addInputJar(new File(appJar));
			}
			// At the first try to load the external api classes, they are searched in the jar file corresponding to
			// this application. Otherwise, it is assumed that they are used in the eclipse project.
			if(!offInstr.addInputElement(new File("."), OWN_JAR_FILE+IC_NAME)) {
				String path = "../api-external/target/classes/"+PACKAGE;
				offInstr.addInputClass(new File(path+IC_NAME), new File(path+IC_NAME));
				offInstr.addInputClass(new File(path+AC_NAME), new File(path+AC_NAME));
				offInstr.addInputClass(new File(path+AC_WW_NAME), new File(path+AC_WW_NAME));
			} else {
				offInstr.addInputElement(new File("."), OWN_JAR_FILE+AC_NAME);
				offInstr.addInputElement(new File("."), OWN_JAR_FILE+AC_WW_NAME);
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
							instrumentWorkingPhase(clInstr, editor);
						} else if(data.getName().equals(END)) {
							instrumentEndPhase(editor);
						}
						editor.applyPatches();
						editor.endPass();
					});
					clInstr.emitClass();
				} else if(clInstr.getInputName().endsWith(PACKAGE+AC_WW_NAME)) {
					clInstr.visitMethods(data -> {
						MethodEditor editor = new MethodEditor(data);
						editor.beginPass();
						if(data.getName().equals("run")) {
							instrumentRunnableClassForWorkingPhase(editor);
						}
						editor.applyPatches();
						editor.endPass();
					});
					clInstr.emitClass();
				}
			}
			offInstr.close();
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
		wrapper.countOneInstance(subclass);
		for(IClass cl : wrapper.getFrameworkClasses()) {
			if(wrapper.getClassHierarchy().isSubclassOf(subclass, cl)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Instruments the bytecode with instructions to get an instance collection from the InstanceCollector.
	 * After calling this method, the instance collection is on top of the stack.
	 * 
	 * @param editor the editor for bytecode instrumentation.
	 * @param cl class for which the instance collection is obtained.
	 */
	private void instrumentForGettingInstanceCollection(MethodEditor editor, IClass cl) {
		editor.insertAfterBody(new MethodEditor.Patch() {
			@Override
			public void emitTo(MethodEditor.Output w) {
				w.emit(ConstantInstruction.makeString(cl.getName().toString()));
				w.emit(InvokeInstruction.make("(Ljava/lang/String;)Ljava/lang/Class;",
						"Ljava/lang/Class", "forName", IInvokeInstruction.Dispatch.STATIC));
				w.emit(InvokeInstruction.make("(Ljava/lang/Class;)Ljava/util/List;",
						"L"+PACKAGE+"InstanceCollector", "getInstances",
						IInvokeInstruction.Dispatch.STATIC));
			}
		});
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
			instrumentForGettingInstanceCollection(editor, declaration.getIClass());
			editor.insertAfterBody(new MethodEditor.Patch() {
				@Override
				public void emitTo(MethodEditor.Output w) {
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
			wrapper.countOneInstance(appClass);
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
		NonDeterministicIfInstrumenter ifInstr = new NonDeterministicIfInstrumenter();
		Set<ExplicitDeclaration> declarations = wrapper.getFramework().getStartPhase().getDeclarations();
		int maxUsesIndex = getNextLocalIndex();
		editor.insertAfterBody(new MethodEditor.Patch() {
			@Override
			public void emitTo(MethodEditor.Output w) {
				w.emit(ConstantInstruction.make((double)declarations.size()));
				w.emit(StoreInstruction.make("D", maxUsesIndex));
			}
		});
		ifInstr.instrumentBeginning(editor, maxUsesIndex);
		for(ExplicitDeclaration dec : declarations) {
			ifInstr.instrumentCaseBeginning(editor);
			instrumentExplicitDeclaration(editor, dec, -1, null);
		}
		ifInstr.instrumentEnd(editor);
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
	 * @param clInstr instrumenter of the artificial class.
	 * @param editor the editor for the working phase method.
	 */
	private void instrumentWorkingPhase(ClassInstrumenter clInstr, MethodEditor editor) {
		resetLocalVariables();
		List<WorkingPhase> workingPhases = wrapper.getFramework().getWorkingPhases();
		ArrayList<Integer> allocatedLabels = new ArrayList<>();
		for(WorkingPhase working : workingPhases) {
			if(working.getThreadType()==ThreadType.MULTI) {
				allocatedLabels.add(editor.allocateLabel());
				allocatedLabels.add(editor.allocateLabel());
				allocatedLabels.add(editor.allocateLabel());
				allocatedLabels.add(editor.allocateLabel());
			}
		}
		editor.insertAfterBody(new MethodEditor.Patch() {
			@Override
			public void emitTo(MethodEditor.Output w) {
				int forLabelCounter = 0;
				int procIndex = 0;
				int threadAIndex = 0;
				int loopIndex = 0;
				for(int i=0; i<workingPhases.size(); i++) {
					WorkingPhase working = workingPhases.get(i);
					if(working.getThreadType()==ThreadType.SINGLE) {
						w.emit(LoadInstruction.make(AC_BYTECODE_NAME, 0));
						w.emit(NewInstruction.make(AC_WW_BYTECODE_NAME, 0));
						w.emit(DupInstruction.make(0));
						w.emit(LoadInstruction.make(AC_BYTECODE_NAME, 0));
						w.emit(ConstantInstruction.make(i));
						w.emit(InvokeInstruction.make("("+AC_BYTECODE_NAME+";I)V", AC_WW_BYTECODE_NAME, "<init>",
								IInvokeInstruction.Dispatch.SPECIAL));
						w.emit(InvokeInstruction.make("()V", AC_WW_BYTECODE_NAME, "run",
								IInvokeInstruction.Dispatch.INTERFACE));
					} else if(working.getThreadType()==ThreadType.MULTI) {
						Runtime.getRuntime().availableProcessors();
						if(procIndex==0) {
							procIndex = getNextLocalIndex();
							threadAIndex = getNextLocalIndex();
							loopIndex = getNextLocalIndex();
							w.emit(InvokeInstruction.make("()Ljava/lang/Runtime;", "Ljava/lang/Runtime", "getRuntime",
									IInvokeInstruction.Dispatch.STATIC));
							w.emit(InvokeInstruction.make("()I", "Ljava/lang/Runtime", "availableProcessors",
									IInvokeInstruction.Dispatch.VIRTUAL));
							w.emit(StoreInstruction.make("I", procIndex));
							w.emit(LoadInstruction.make("I", procIndex));
							w.emit(NewInstruction.make("Ljava/lang/Thread", 1));
							w.emit(StoreInstruction.make("Ljava/lang/Thread", threadAIndex));
						}
						w.emit(ConstantInstruction.make(0));
						w.emit(StoreInstruction.make("I", loopIndex));
						
						w.emitLabel(allocatedLabels.get(forLabelCounter));
						w.emit(LoadInstruction.make("I", loopIndex));
						w.emit(LoadInstruction.make("I", procIndex));
						w.emit(ConditionalBranchInstruction.make("I", IConditionalBranchInstruction.Operator.LT,
								allocatedLabels.get(forLabelCounter+1)));
						w.emit(LoadInstruction.make("[Ljava/lang/Thread", threadAIndex));
						w.emit(LoadInstruction.make("I", loopIndex));
						w.emit(NewInstruction.make("Ljava/lang/Thread", 0));
						w.emit(DupInstruction.make(0));
						w.emit(NewInstruction.make(AC_WW_BYTECODE_NAME, 0));
						w.emit(DupInstruction.make(0));
						w.emit(LoadInstruction.make(AC_BYTECODE_NAME, 0));
						w.emit(ConstantInstruction.make(i));
						w.emit(InvokeInstruction.make("()V", AC_WW_BYTECODE_NAME, "<init>",
								IInvokeInstruction.Dispatch.SPECIAL));
						w.emit(InvokeInstruction.make("(Ljava/lang/Runnable;)V", "Ljava/lang/Thread", "<init>",
								IInvokeInstruction.Dispatch.SPECIAL));
						w.emit(ArrayStoreInstruction.make("[Ljava/lang/Thread"));
						w.emit(LoadInstruction.make("[Ljava/lang/Thread", threadAIndex));
						w.emit(LoadInstruction.make("I", loopIndex));
						w.emit(ArrayLoadInstruction.make("[Ljava/lang/Thread"));
						w.emit(InvokeInstruction.make("()V", "Ljava/lang/Thread", "start",
								IInvokeInstruction.Dispatch.VIRTUAL));
						w.emit(LoadInstruction.make("I", loopIndex));
						w.emit(ConstantInstruction.make(1));
						w.emit(BinaryOpInstruction.make("I", IBinaryOpInstruction.Operator.ADD));
						w.emit(StoreInstruction.make("I", loopIndex));
						w.emit(GotoInstruction.make(allocatedLabels.get(forLabelCounter)));
						
						w.emitLabel(allocatedLabels.get(forLabelCounter+1));
						w.emit(ConstantInstruction.make(0));
						w.emit(StoreInstruction.make("I", loopIndex));
						w.emitLabel(allocatedLabels.get(forLabelCounter+2));
						w.emit(LoadInstruction.make("I", loopIndex));
						w.emit(LoadInstruction.make("I", procIndex));
						w.emit(ConditionalBranchInstruction.make("I", IConditionalBranchInstruction.Operator.LT,
								allocatedLabels.get(forLabelCounter+3)));
						w.emit(LoadInstruction.make("[Ljava/lang/Thread", threadAIndex));
						w.emit(LoadInstruction.make("I", loopIndex));
						w.emit(ArrayLoadInstruction.make("[Ljava/lang/Thread"));
						w.emit(InvokeInstruction.make("()V", "Ljava/lang/Thread", "join",
								IInvokeInstruction.Dispatch.VIRTUAL));
						w.emit(LoadInstruction.make("I", loopIndex));
						w.emit(ConstantInstruction.make(1));
						w.emit(BinaryOpInstruction.make("I", IBinaryOpInstruction.Operator.ADD));
						w.emit(StoreInstruction.make("I", loopIndex));
						w.emit(GotoInstruction.make(allocatedLabels.get(forLabelCounter+2)));
						
						w.emitLabel(allocatedLabels.get(forLabelCounter+3));
						forLabelCounter++;
					}
				}
			}
		});
		for(int i=0; i<workingPhases.size(); i++) {
			WorkingPhase working = workingPhases.get(i);
			MethodData data = clInstr.createEmptyMethodData("w"+i, "()V", Constants.ACC_PROTECTED);
			MethodEditor wEditor = new MethodEditor(data);
			instrumentActualWorkingPhaseContent(wEditor, working);
		}
	}
	
	/**
	 * Instruments a new ArtificialClass method with the actual content of a working phase.
	 * 
	 * @param editor the edtior for bytecode instrumentation.
	 * @param working the working phase.
	 */
	private void instrumentActualWorkingPhaseContent(MethodEditor editor, WorkingPhase working) {
		NonDeterministicLoopInstrumenter loop = new NonDeterministicLoopInstrumenter();
		NonDeterministicIfInstrumenter ifInstr = new NonDeterministicIfInstrumenter();
		loop.instrumentLoopBeginning(editor);
		int caseCounter = 0;
		for(Rule r : working.getRules()) {
			if(r.getClass()==MethodCollector.class) {
				MethodCollector coll = (MethodCollector)r;
				for(IClass cl : coll.getFrameworkClasses()) {
					caseCounter += coll.getMethodCollection(cl).size()*wrapper.getInstancesCount(cl);
				}
			} else if(r.getClass()==Block.class) {
				caseCounter += countBlocks((Block)r);
			}
		}
		int caseCounterCopy = caseCounter;
		int counterIndex = getNextLocalIndex();
		editor.insertAfterBody(new MethodEditor.Patch() {
			@Override
			public void emitTo(MethodEditor.Output w) {
				w.emit(ConstantInstruction.make(caseCounterCopy));
				w.emit(StoreInstruction.make("I", counterIndex));
			}
		});
		ifInstr.instrumentBeginning(editor, counterIndex);
		for(Rule r : working.getRules()) {
			if(r.getClass()==MethodCollector.class) {
				MethodCollector coll = (MethodCollector)r;
				for(IClass cl : coll.getFrameworkClasses()) {
					int instancesCount = wrapper.getInstancesCount(cl);
					for(int i=0; i<instancesCount; i++) {
						for(IMethod m : coll.getMethodCollection(cl)) {
							ifInstr.instrumentCaseBeginning(editor);
							instrumentForGettingInstanceCollection(editor, cl);
							int index = i;
							editor.insertAfterBody(new MethodEditor.Patch() {
								@Override
								public void emitTo(MethodEditor.Output w) {
									w.emit(ConstantInstruction.make(index));
									w.emit(InvokeInstruction.make("(I)Ljava/lang/Object;", "Ljava/util/List", "get",
											IInvokeInstruction.Dispatch.INTERFACE));
									w.emit(CheckCastInstruction.make(cl.getName().toString()));
								}
							});
							instantiateParameters(editor, m);
							editor.insertAfterBody(new MethodEditor.Patch() {
								@Override
								public void emitTo(MethodEditor.Output w) {
									w.emit(InvokeInstruction.make(m.getDescriptor().toString(), cl.getName().toString(),
											m.getName().toString(), cl.isInterface()
											?IInvokeInstruction.Dispatch.INTERFACE
													:IInvokeInstruction.Dispatch.VIRTUAL));
								}
							});
						}
					}
				}
			} else if(r.getClass()==Block.class) {
				instrumentBlock(editor, ifInstr, (Block)r);
			}
		}
		ifInstr.instrumentEnd(editor);
		loop.instrumentLoopEnd(editor);
	}
	
	/**
	 * Counts the number of resulting blocks for a block rule.
	 * 
	 * @param b the block rule.
	 * @return the number of blocks.
	 */
	private int countBlocks(Block b) {
		return wrapper.getInstancesCount(b.getIClass())*(b.getInnerBlock()!=null?countBlocks(b.getInnerBlock()):0);
	}
	
	/**
	 * Instruments the bytecode with a block rule.
	 * 
	 * @param editor the editor for bytecode instrumentation.
	 * @param ifInstr instrumenter for the non-deterministic if-else-if-clause in the working phase.
	 * @param b the block rule.
	 */
	private void instrumentBlock(MethodEditor editor, NonDeterministicIfInstrumenter ifInstr, Block b) {
		if(b.getInnerBlock()==null) {
			for(int i=0; i<wrapper.getInstancesCount(b.getIClass()); i++) {
				ifInstr.instrumentCaseBeginning(editor);
				instrumentForGettingInstanceCollection(editor, b.getIClass());
				int index = i;
				int instanceIndex = getNextLocalIndex();
				editor.insertAfterBody(new MethodEditor.Patch() {
					@Override
					public void emitTo(MethodEditor.Output w) {
						w.emit(ConstantInstruction.make(index));
						w.emit(InvokeInstruction.make("(I)Ljava/lang/Object;", "Ljava/util/List", "get",
								IInvokeInstruction.Dispatch.INTERFACE));
						w.emit(CheckCastInstruction.make(b.getClassName()));
						w.emit(StoreInstruction.make(b.getClassName(), instanceIndex));
						instrumentExplicitDeclaration(editor, b.getDeclaration(), instanceIndex, b.getClassName());
					}
				});
			}
		} else {
			for(int i=0; i<wrapper.getInstancesCount(b.getIClass()); i++) {
				instrumentBlock(editor, ifInstr, b.getInnerBlock());
			}
		}
	}
	
	/**
	 * Instruments the bytecode of the runnable class used in the working phase.
	 * 
	 * @param editor the editor for byteocde instrumentation.
	 */
	private void instrumentRunnableClassForWorkingPhase(MethodEditor editor) {
		resetLocalVariables();
		int cases = wrapper.getFramework().getWorkingPhases().size();
		ArrayList<Integer> allocatedLabels = new ArrayList<>();
		for(int i=0; i<=cases; i++) {
			allocatedLabels.add(editor.allocateLabel());
		}
		editor.insertAfterBody(new MethodEditor.Patch() {
			@Override
			public void emitTo(MethodEditor.Output w) {
				for(int i=0; i<cases; i++) {
					w.emitLabel(allocatedLabels.get(i));
					w.emit(LoadInstruction.make(AC_WW_BYTECODE_NAME, 0));
					w.emit(GetInstruction.make("I", AC_WW_BYTECODE_NAME, "phaseNumber", false));
					w.emit(ConstantInstruction.make(i));
					w.emit(ConditionalBranchInstruction.make("I", IConditionalBranchInstruction.Operator.EQ,
							allocatedLabels.get(i+1)));
					w.emit(LoadInstruction.make(AC_WW_BYTECODE_NAME, 0));
					w.emit(GetInstruction.make(AC_BYTECODE_NAME, AC_WW_BYTECODE_NAME, "outerInstance", false));
					w.emit(InvokeInstruction.make("()V", AC_BYTECODE_NAME, "w"+i, IInvokeInstruction.Dispatch.VIRTUAL));
				}
			}
		});
	}

	/**
	 * Instruments bytecode with a non-deterministic if-else-if-clause.
	 * 
	 * @author Martin Armbruster
	 */
	private class NonDeterministicIfInstrumenter {
		/**
		 * Stores the index where the random generated number of the case to take is stored.
		 */
		private int randomIndex;
		/**
		 * Stores a list of all allocated labels for the cases.
		 */
		private ArrayList<Integer> allocatedCaseLabels;
		/**
		 * Stores the counter of the current case.
		 */
		private int caseCounter;
		
		/**
		 * Instruments bytecode with the beginning of the clause.
		 * 
		 * @param editor the editor for bytecode instrumentation.
		 * @param maxCasesIndex index where the number of the overall cases is stored.
		 */
		private void instrumentBeginning(MethodEditor editor, int maxCasesIndex) {
			randomIndex = getNextLocalIndex();
			allocatedCaseLabels = new ArrayList<>();
			caseCounter = 0;
			editor.insertAfterBody(new MethodEditor.Patch() {
				@Override
				public void emitTo(MethodEditor.Output w) {
					w.emit(InvokeInstruction.make("()D", "Ljava/lang/Math", "random",
							IInvokeInstruction.Dispatch.STATIC));
					w.emit(LoadInstruction.make("D", maxCasesIndex));
					w.emit(BinaryOpInstruction.make("D", IBinaryOpInstruction.Operator.MUL));
					w.emit(CheckCastInstruction.make("I"));
					w.emit(StoreInstruction.make("I", randomIndex));
				}
			});
		}
		
		/**
		 * Instruments bytecode with the beginning of a case. Must be called before the actual content of the case is
		 * added.
		 * 
		 * @param editor the editor for bytecode instrumentation.
		 */
		private void instrumentCaseBeginning(MethodEditor editor) {
			if(caseCounter==0) {
				allocatedCaseLabels.add(editor.allocateLabel());
			}
			allocatedCaseLabels.add(editor.allocateLabel());
			editor.insertAfterBody(new MethodEditor.Patch() {
				@Override
				public void emitTo(MethodEditor.Output w) {
					w.emitLabel(allocatedCaseLabels.get(caseCounter));
					w.emit(LoadInstruction.make("I", randomIndex));
					w.emit(ConstantInstruction.make(caseCounter));
					w.emit(ConditionalBranchInstruction.make("I", IConditionalBranchInstruction.Operator.EQ,
							allocatedCaseLabels.get(caseCounter+1)));
				}
			});
			caseCounter++;
		}
		
		/**
		 * Instruments bytecode with the end of the clause.
		 * 
		 * @param editor the editor for bytecode instrumentation.
		 */
		private void instrumentEnd(MethodEditor editor) {
			editor.insertAfterBody(new MethodEditor.Patch() {
				@Override
				public void emitTo(MethodEditor.Output w) {
					w.emitLabel(allocatedCaseLabels.get(caseCounter));
				}
			});
		}
	}
	
	/**
	 * Instruments bytecode with a non-deterministic loop.
	 * 
	 * @author Martin Armbruster
	 */
	private class NonDeterministicLoopInstrumenter {
		/**
		 * Stores the index where the loop counter is stored.
		 */
		private int loopIndex;
		/**
		 * Stores the label where the loop begins.
		 */
		private int beginLabel;
		/**
		 * Stores the label where the loop ends.
		 */
		private int endLabel;
		
		/**
		 * Instruments the bytecode with the beginning of the loop.
		 * 
		 * @param editor the editor for bytecode instrumentation.
		 */
		private void instrumentLoopBeginning(MethodEditor editor) {
			loopIndex = getNextLocalIndex();
			int randomIndex = getNextLocalIndex();
			beginLabel = editor.allocateLabel();
			endLabel = editor.allocateLabel();
			editor.insertAfterBody(new MethodEditor.Patch() {
				@Override
				public void emitTo(MethodEditor.Output w) {
					w.emit(InvokeInstruction.make("()D", "Ljava/lang/Math", "random",
							IInvokeInstruction.Dispatch.STATIC));
					w.emit(ConstantInstruction.make(1000000.0));
					w.emit(BinaryOpInstruction.make("D", IBinaryOpInstruction.Operator.MUL));
					w.emit(CheckCastInstruction.make("I"));
					w.emit(StoreInstruction.make("I", randomIndex));
					w.emit(ConstantInstruction.make(0));
					w.emit(StoreInstruction.make("I", loopIndex));
					w.emitLabel(beginLabel);
					w.emit(LoadInstruction.make("I", loopIndex));
					w.emit(LoadInstruction.make("I", randomIndex));
					w.emit(ConditionalBranchInstruction.make("I", IConditionalBranchInstruction.Operator.LT, endLabel));
				}
			});
		}
		
		/**
		 * Instruments the bytecode with th end of the loop.
		 * 
		 * @param editor the editor for bytecode instrumentation.
		 */
		private void instrumentLoopEnd(MethodEditor editor) {
			editor.insertAfterBody(new MethodEditor.Patch() {
				@Override
				public void emitTo(MethodEditor.Output w) {
					w.emit(LoadInstruction.make("I", loopIndex));
					w.emit(ConstantInstruction.make(1));
					w.emit(BinaryOpInstruction.make("I", IBinaryOpInstruction.Operator.ADD));
					w.emit(StoreInstruction.make("I", loopIndex));
					w.emit(GotoInstruction.make(beginLabel));
					w.emitLabel(endLabel);
				}
			});
		}
	}
}
