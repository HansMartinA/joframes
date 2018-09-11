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
import com.ibm.wala.shrikeBT.ConversionInstruction;
import com.ibm.wala.shrikeBT.DupInstruction;
import com.ibm.wala.shrikeBT.ExceptionHandler;
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
import com.ibm.wala.shrikeBT.PopInstruction;
import com.ibm.wala.shrikeBT.ReturnInstruction;
import com.ibm.wala.shrikeBT.StoreInstruction;
import com.ibm.wala.shrikeBT.info.LocalAllocator;
import com.ibm.wala.shrikeBT.shrikeCT.CTUtils;
import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;
import com.ibm.wala.shrikeBT.shrikeCT.OfflineInstrumenter;
import com.ibm.wala.shrikeCT.ClassWriter;
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
	private static final String OWN_JAR_FILE = ".jar#" + PACKAGE;
	/**
	 * Simple name of the InstanceCollector class.
	 */
	private static final String IC = "InstanceCollector";
	/**
	 * Name of the InstanceCollector class file.
	 */
	private static final String IC_NAME = IC + ".class";
	/**
	 * Bytecode name of the InstanceCollector class.
	 */
	private static final String IC_BYTECODE_NAME = "L" + PACKAGE + IC + ";";
	/**
	 * Simple name of the ArtificialClass class.
	 */
	private static final String AC = "ArtificialClass";
	/**
	 * Name of the ArtifcialClass class file.
	 */
	private static final String AC_NAME = AC + ".class";
	/**
	 * Bytecode name of the ArtificialClass class.
	 */
	private static final String AC_BYTECODE_NAME = "L" + PACKAGE + AC + ";";
	/**
	 * Simple name of the WorkingWorker class within the ArtificialClass class.
	 */
	private static final String AC_WWW = AC + "$WorkingWorker";
	/**
	 * Name of the WorkingWorker class file.
	 */
	private static final String AC_WW_NAME = AC_WWW + ".class";
	/**
	 * Bytecode name of the WorkingWorker class within the ArtificialClass class.
	 */
	private static final String AC_WW_BYTECODE_NAME = "L" + PACKAGE + AC_WWW + ";";
	/**
	 * Bytecode name of the List interface.
	 */
	private static final String LIST_BYTECODE_NAME = "Ljava/util/List;";
	/**
	 * Bytecode name of the Iterator interface.
	 */
	private static final String ITERATOR_BYTECODE_NAME = "Ljava/util/Iterator;";
	/**
	 * Bytecode name of the Thread class.
	 */
	private static final String THREAD_BYTECODE_NAME = "Ljava/lang/Thread;";
	/**
	 * Bytecode name of the Runtime class.
	 */
	private static final String RUNTIME_BYTECODE_NAME = "Ljava/lang/Runtime;";
	/**
	 * Name of the static initializer method.
	 */
	private static final String CLINIT = "<clinit>";
	/**
	 * Name of the forName method in the Class class.
	 */
	private static final String CLASS_FOR_NAME_METHOD = "forName";
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
	 * Stores the actual output name.
	 */
	private String actualOutput;

	/**
	 * Returns the path to the actual ouput jar file.
	 *
	 * @return the path to the output jar file or null if the bytecode is not yet instrumented.
	 */
	String getOutput() {
		return actualOutput;
	}

	/**
	 * Instruments the bytecode for a specific framework and application.
	 *
	 * @param frameworkWrapper the framework with the results of the class hierarchy analysis.
	 * @param applicationJars paths to the jar files containing the application classes.
	 * @throws InstrumenterException when an exception occurs during instrumentation.
	 */
	void instrumentBytecode(final FrameworkWrapper frameworkWrapper, final String[] applicationJars)
			throws InstrumenterException {
		instrumentBytecode(wrapper, applicationJars,
				String.format(DEFAULT_OUTPUT_NAME, wrapper.getFramework().getName()));
	}

	/**
	 * Instruments the bytecode for a specific framework and application.
	 *
	 * @param frameworkWrapper the framework with the results of the class hierarchy analysis.
	 * @param applicationJars paths to the jar files containing the application classes.
	 * @param output name of the output jar with the instrumented bytecode.
	 * @throws InstrumenterException when an exception occurs during instrumentation.
	 */
	void instrumentBytecode(final FrameworkWrapper frameworkWrapper, final String[] applicationJars,
			final String output)
			throws InstrumenterException {
		String tempEnd = "-temp.jar";
		this.actualOutput = output;
		this.wrapper = frameworkWrapper;
		try {
			OfflineInstrumenter offInstr = new OfflineInstrumenter();
			offInstr.setPassUnmodifiedClasses(true);
			offInstr.setOutputJar(new File(output + tempEnd));
			for (String appJar : applicationJars) {
				offInstr.addInputJar(new File(appJar));
			}
			// For test purposes, the loading of the external api classes is done by assuming that
			// the project is used within eclipse.
			if (Boolean.getBoolean(APIConstants.TEST_SYSTEM_PROPERTY)) {
				String path = new File("").getAbsoluteFile().getParentFile().getAbsolutePath()
						+ File.separator + "api-external" + File.separator + "target" + File.separator + "classes"
						+ File.separator;
				offInstr.addInputDirectory(new File(path), new File(path));
			} else {
				// Actual class loading for the stand-alone application.
				offInstr.addInputElement(new File("."), OWN_JAR_FILE + IC_NAME);
				offInstr.addInputElement(new File("."), OWN_JAR_FILE + AC_NAME);
				offInstr.addInputElement(new File("."), OWN_JAR_FILE + AC_WW_NAME);
			}
			offInstr.beginTraversal();
			for (int i = 0; i < offInstr.getNumInputClasses(); i++) {
				ClassInstrumenter clInstr = offInstr.nextClass();
				if (clInstr != null && clInstr.getInputName().endsWith(PACKAGE + IC_NAME)) {
					clInstr.visitMethods(data -> {
						if (data.getName().equals(CLINIT)) {
							MethodEditor editor = new MethodEditor(data);
							editor.beginPass();
							editor.insertBefore(editor.getInstructions().length - 1, new MethodEditor.Patch() {
								@Override
								public void emitTo(final MethodEditor.Output w) {
									for (IClass cl : wrapper.getFrameworkClasses()) {
										// WALA classes stores class names in the bytecode format, but the used method
										// Class.forName requires a full-qualified class name. Therefore, the bytecode
										// name is converted to a fully qualified class name.
										w.emit(ConstantInstruction.makeString(cl.getName().toString()
												.substring(1).replace("/", ".")));
										w.emit(InvokeInstruction.make("(" + Constants.TYPE_String + ")"
												+ Constants.TYPE_Class, Constants.TYPE_Class, CLASS_FOR_NAME_METHOD,
												IInvokeInstruction.Dispatch.STATIC));
										w.emit(InvokeInstruction.make("(" + Constants.TYPE_Class + ")"
												+ Constants.TYPE_void, IC_BYTECODE_NAME, "addClass",
												IInvokeInstruction.Dispatch.STATIC));
									}
								}
							});
							editor.applyPatches();
							editor.endPass();
						}
					});
					offInstr.outputModifiedClass(clInstr);
					break;
				}
			}
			offInstr.beginTraversal();
			for (int i = 0; i < offInstr.getNumInputClasses(); i++) {
				ClassInstrumenter clInstr = offInstr.nextClass();
				if (clInstr == null) {
					continue;
				}
				clInstr.visitMethods(data -> {
					MethodEditor editor = new MethodEditor(data);
					if (editor.getData().getClassType().contains(PACKAGE)) {
						return;
					}
					editor.beginPass();
					editor.visitInstructions(new MethodEditor.Visitor() {
						@Override
						public void visitInvoke(final IInvokeInstruction instruction) {
							String invokedClass = instruction.getClassType()
									.substring(0, instruction.getClassType().length() - 1);
							if (instruction.getMethodName().equals(APIConstants.INIT)
									&& isSubclassOfFrameworkClasses(invokedClass)
									&& !(data.getName().equals(APIConstants.INIT)
											&& areDirectSubclasses(invokedClass, data.getClassType()
													.substring(0, data.getClassType().length() - 1)))) {
								this.insertAfter(new MethodEditor.Patch() {
									@Override
									public void emitTo(final MethodEditor.Output w) {
										w.emit(DupInstruction.make(0));
										w.emit(InvokeInstruction.make("(" + Constants.TYPE_Object + ")"
												+ Constants.TYPE_void, IC_BYTECODE_NAME, "addInstance",
												IInvokeInstruction.Dispatch.STATIC));
									}
								});
							}
						}
					});
					editor.applyPatches();
					editor.endPass();
				});
				if (clInstr.isChanged()) {
					offInstr.outputModifiedClass(clInstr);
				}
			}
			offInstr.beginTraversal();
			for (int i = 0; i < offInstr.getNumInputClasses(); i++) {
				ClassInstrumenter clInstr = offInstr.nextClass();
				if (clInstr == null) {
					continue;
				}
				if (clInstr.getInputName().endsWith(PACKAGE + AC_NAME)) {
					ArrayList<MethodData> workingMethods = new ArrayList<>();
					clInstr.visitMethods(data -> {
						MethodEditor editor = new MethodEditor(data);
						editor.beginPass();
						boolean emptyMethod = editor.getInstructions().length == 1;
						if (emptyMethod) {
							editor.replaceWith(0, new MethodEditor.Patch() {
								@Override
								public void emitTo(final MethodEditor.Output w) {
								}
							});
						}
						if (data.getName().equals(START)) {
							instrumentStartPhase(editor);
						} else if (data.getName().equals(WORKING)) {
							instrumentWorkingPhase(clInstr, editor, workingMethods);
						} else if (data.getName().equals(END)) {
							instrumentEndPhase(editor);
						}
						if (emptyMethod) {
							editor.insertAfter(0, new MethodEditor.Patch() {
								@Override
								public void emitTo(final MethodEditor.Output w) {
									w.emit(ReturnInstruction.make(Constants.TYPE_void));
								}
							});
						}
						editor.applyPatches();
						editor.endPass();
					});
					ClassWriter writer = clInstr.emitClass();
					for (MethodData data : workingMethods) {
						CTUtils.compileAndAddMethodToClassWriter(data, writer, null);
					}
					offInstr.outputModifiedClass(clInstr, writer);
				} else if (clInstr.getInputName().endsWith(PACKAGE + AC_WW_NAME)) {
					clInstr.visitMethods(data -> {
						MethodEditor editor = new MethodEditor(data);
						editor.beginPass();
						if (data.getName().equals("run")) {
							editor.replaceWith(0, new MethodEditor.Patch() {
								@Override
								public void emitTo(final MethodEditor.Output w) {
								}
							});
							instrumentRunnableClassForWorkingPhase(editor);
						}
						editor.applyPatches();
						editor.endPass();
					});
					offInstr.outputModifiedClass(clInstr);
				}
			}
			offInstr.writeUnmodifiedClasses();
			offInstr.close();
			offInstr = new OfflineInstrumenter();
			offInstr.addInputJar(new File(output + tempEnd));
			offInstr.setPassUnmodifiedClasses(true);
			offInstr.setOutputJar(new File(output));
			offInstr.beginTraversal();
			for (int i = 0; i < offInstr.getNumInputClasses(); i++) {
				ClassInstrumenter clInstr = offInstr.nextClass();
				if (clInstr == null) {
					continue;
				}
				if (clInstr.getInputName().endsWith(AC_NAME)) {
					clInstr.visitMethods(data -> {
						if (data.getName().startsWith("w")) {
							MethodEditor editor = new MethodEditor(data);
							editor.beginPass();
							editor.insertAfterBody(new MethodEditor.Patch() {
								@Override
								public void emitTo(final MethodEditor.Output w) {
									w.emit(ConstantInstruction.make(0));
									w.emit(PopInstruction.make(1));
								}
							});
							editor.applyPatches();
							editor.endPass();
						}
					});
					offInstr.outputModifiedClass(clInstr);
				}
			}
			offInstr.writeUnmodifiedClasses();
			offInstr.close();
			new File(output + tempEnd).delete();
		} catch (IOException e) {
			new File(output + tempEnd).delete();
			throw new InstrumenterException("An IO exception occurred while instrumenting the bytecode.", e);
		} catch (InvalidClassFileException e) {
			new File(output + tempEnd).delete();
			throw new InstrumenterException("Bytcode instrumentation resulted in an invalid class.", e);
		}
	}

	/**
	 * Checks if a class is a subclass of a framework class.
	 *
	 * @param className name of the class that is checked.
	 * @return true if the class is a subclass of a framework class. false otherwise.
	 */
	private boolean isSubclassOfFrameworkClasses(final String className) {
		IClass subclass = wrapper.getClassHierarchy().lookupClass(TypeReference.findOrCreate(
				wrapper.getClassHierarchy().getScope().getApplicationLoader(), className));

		for (IClass cl : wrapper.getFrameworkClasses()) {
			if (wrapper.getClassHierarchy().isSubclassOf(subclass, cl)
					|| wrapper.getClassHierarchy().implementsInterface(subclass, cl)) {
				wrapper.countOneInstance(subclass);
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks that two classes are direct subclasses of each other or equal.
	 *
	 * @param classOne one class.
	 * @param classTwo another class.
	 * @return true if both classes are equal or direct subclasses. false otherwise.
	 */
	private boolean areDirectSubclasses(final String classOne, final String classTwo) {
		IClass one = wrapper.getClassHierarchy().lookupClass(TypeReference
				.findOrCreate(wrapper.getClassHierarchy().getScope().getApplicationLoader(), classOne));
		IClass two = wrapper.getClassHierarchy().lookupClass(TypeReference
				.findOrCreate(wrapper.getClassHierarchy().getScope().getApplicationLoader(), classTwo));
		return one == two || wrapper.getClassHierarchy().isSubclassOf(one, two)
				|| wrapper.getClassHierarchy().isSubclassOf(two, one)
				|| wrapper.getClassHierarchy().implementsInterface(one, two)
				|| wrapper.getClassHierarchy().implementsInterface(one, two);
	}

	/**
	 * Instruments the bytecode with instructions to get an instance collection from the InstanceCollector.
	 * After calling this method, the instance collection is on top of the stack.
	 *
	 * @param editor the editor for bytecode instrumentation.
	 * @param cl class for which the instance collection is obtained.
	 */
	private void instrumentForGettingInstanceCollection(final MethodEditor editor, final IClass cl) {
		int exLabel = editor.allocateLabel();
		int afterExLabel = editor.allocateLabel();
		editor.insertAfter(0, new MethodEditor.Patch() {
			@Override
			public void emitTo(final MethodEditor.Output w) {
				ExceptionHandler h = new ExceptionHandler(exLabel, "Ljava/lang/ClassNotFoundException;");
				w.emit(ConstantInstruction.makeString(cl.getName().toString().substring(1).replace("/", ".")));
				w.emit(InvokeInstruction.make("(" + Constants.TYPE_String + ")" + Constants.TYPE_Class,
						Constants.TYPE_Class, CLASS_FOR_NAME_METHOD, IInvokeInstruction.Dispatch.STATIC),
						new ExceptionHandler[] {h});
				w.emit(InvokeInstruction.make("(" + Constants.TYPE_Class + ")" + LIST_BYTECODE_NAME,
						IC_BYTECODE_NAME, "getInstances",
						IInvokeInstruction.Dispatch.STATIC));
				w.emit(GotoInstruction.make(afterExLabel));
				w.emitLabel(exLabel);
				w.emit(ReturnInstruction.make(Constants.TYPE_void));
				w.emitLabel(afterExLabel);
			}
		});
	}

	/**
	 * Instruments the bytecode with an explicit declaration.
	 *
	 * @param editor the editor for the bytecode instrumentation.
	 * @param declaration the explicit declaration.
	 * @param instanceIndex local variable index of an instance that is put in or -1 if no instance is put in.
	 * @param instanceType type of the instance put in or null if no instance is put in.
	 */
	private void instrumentExplicitDeclaration(final MethodEditor editor, final ExplicitDeclaration declaration,
			final int instanceIndex, final IClass instanceType) {
		boolean useForLoop = declaration.getClassName() != null;
		IClass actualInstanceType = instanceType;
		int iteratorIndex = 0;
		int localInstanceIndex = instanceIndex;
		int beginLoopLabel = 0;
		int afterLoopLabel = 0;
		if (useForLoop) {
			actualInstanceType = declaration.getIClass();
			iteratorIndex = LocalAllocator.allocate(editor.getData(), ITERATOR_BYTECODE_NAME);
			localInstanceIndex = LocalAllocator.allocate(editor.getData(), declaration.getClassName());
			beginLoopLabel = editor.allocateLabel();
			afterLoopLabel = editor.allocateLabel();
			int iteratorIndexCopy = iteratorIndex;
			int localInstanceIndexCopy = localInstanceIndex;
			int beginLoopLabelCopy = beginLoopLabel;
			int afterLoopLabelCopy = afterLoopLabel;
			instrumentForGettingInstanceCollection(editor, declaration.getIClass());
			editor.insertAfter(0, new MethodEditor.Patch() {
				@Override
				public void emitTo(final MethodEditor.Output w) {
					w.emit(InvokeInstruction.make("()" + ITERATOR_BYTECODE_NAME, LIST_BYTECODE_NAME, "iterator",
							IInvokeInstruction.Dispatch.INTERFACE));
					w.emit(StoreInstruction.make(ITERATOR_BYTECODE_NAME, iteratorIndexCopy));
					w.emitLabel(beginLoopLabelCopy);
					w.emit(LoadInstruction.make(ITERATOR_BYTECODE_NAME, iteratorIndexCopy));
					w.emit(InvokeInstruction.make("()" + Constants.TYPE_boolean, ITERATOR_BYTECODE_NAME, "hasNext",
							IInvokeInstruction.Dispatch.INTERFACE));
					w.emit(ConstantInstruction.make(0));
					w.emit(ConditionalBranchInstruction.make(Constants.TYPE_int,
							IConditionalBranchInstruction.Operator.EQ, afterLoopLabelCopy));
					w.emit(LoadInstruction.make(ITERATOR_BYTECODE_NAME, iteratorIndexCopy));
					w.emit(InvokeInstruction.make("()" + Constants.TYPE_Object, ITERATOR_BYTECODE_NAME, "next",
							IInvokeInstruction.Dispatch.INTERFACE));
					w.emit(CheckCastInstruction.make(declaration.getClassName() + ";"));
					w.emit(StoreInstruction.make(declaration.getClassName() + ";", localInstanceIndexCopy));
				}
			});
		}
		instrumentExplicitDeclarationContent(editor, declaration, localInstanceIndex, actualInstanceType);
		if (useForLoop) {
			int beginLoopLabelCopy = beginLoopLabel;
			int afterLoopLabelCopy = afterLoopLabel;
			editor.insertAfter(0, new MethodEditor.Patch() {
				@Override
				public void emitTo(final MethodEditor.Output w) {
					w.emit(GotoInstruction.make(beginLoopLabelCopy));
					w.emitLabel(afterLoopLabelCopy);
				}
			});
		}
		for (IClass appClass : declaration.getApplicationClasses()) {
			wrapper.countOneInstance(appClass);
			instrumentExplicitDeclarationContent(editor, declaration, LocalAllocator.allocate(editor.getData(),
					appClass.getName().toString()), appClass);
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
	private void instrumentExplicitDeclarationContent(final MethodEditor editor, final ExplicitDeclaration declaration,
			final int instanceIndex, final IClass instanceType) {
		for (int i = 0; i < declaration.getNumberOfCallsAndDeclarations(); i++) {
			AstBaseClass abc = declaration.getCallOrDeclaration(i);
			if (abc.getClass() == Method.class) {
				Method m = (Method) abc;
				if (m.getSignature().equals(APIConstants.CONSTRUCTOR)) {
					IMethod init = declaration.getConstructor(instanceType);
					if (init == null) {
						continue;
					}
					editor.insertAfter(0, new MethodEditor.Patch() {
						@Override
						public void emitTo(final MethodEditor.Output w) {
							w.emit(NewInstruction.make(instanceType.getName().toString() + ";", 0));
							w.emit(DupInstruction.make(0));
						}
					});
					instantiateParameters(editor, init);
					editor.insertAfter(0, new MethodEditor.Patch() {
						@Override
						public void emitTo(final MethodEditor.Output w) {
							w.emit(InvokeInstruction.make(init.getDescriptor().toString(),
									instanceType.getName().toString() + ";", APIConstants.INIT,
									IInvokeInstruction.Dispatch.SPECIAL));
							w.emit(DupInstruction.make(0));
							w.emit(InvokeInstruction.make("(" + Constants.TYPE_Object + ")" + Constants.TYPE_void,
									IC_BYTECODE_NAME, "addInstance", IInvokeInstruction.Dispatch.STATIC));
							w.emit(StoreInstruction.make(instanceType.getName().toString() + ";", instanceIndex));
						}
					});
					continue;
				}
				editor.insertAfter(0, new MethodEditor.Patch() {
					@Override
					public void emitTo(final MethodEditor.Output w) {
						w.emit(LoadInstruction.make(instanceType.getName().toString() + ";", instanceIndex));
					}
				});
				instantiateParameters(editor, m.getMethod());
				editor.insertAfter(0, new MethodEditor.Patch() {
					@Override
					public void emitTo(final MethodEditor.Output w) {
						w.emit(InvokeInstruction.make(m.getMethod().getDescriptor().toString(),
								instanceType.getName().toString() + ";", m.getMethod().getName().toString(),
										IInvokeInstruction.Dispatch.VIRTUAL));
					}
				});
			} else if (abc.getClass() == StaticMethod.class) {
				StaticMethod st = (StaticMethod) abc;
				instantiateParameters(editor, st.getMethod());
				editor.insertAfter(0, new MethodEditor.Patch() {
					@Override
					public void emitTo(final MethodEditor.Output w) {
						w.emit(InvokeInstruction.make(st.getMethod().getDescriptor().toString(),
								st.getClassString() + ";",
								st.getMethod().getName().toString(), IInvokeInstruction.Dispatch.STATIC));
					}
				});
			} else if (abc.getClass() == ExplicitDeclaration.class) {
				instrumentExplicitDeclaration(editor, (ExplicitDeclaration) abc, instanceIndex, instanceType);
			}
		}
	}

	/**
	 * Instantiates the parameters for a method.
	 *
	 * @param editor the editor for bytecode instrumentation.
	 * @param method the method for which the parameters are created.
	 */
	private void instantiateParameters(final MethodEditor editor, final IMethod method) {
		editor.insertAfter(0, new MethodEditor.Patch() {
			@Override
			public void emitTo(final MethodEditor.Output w) {
				for (int i = method.isStatic() ? 0 : 1; i < method.getNumberOfParameters(); i++) {
					TypeReference type = method.getParameterType(i);
					if (type.isPrimitiveType()) {
						w.emit(ConstantInstruction.make(type.getName().toString(), 0));
					} else {
						w.emit(ConstantInstruction.make(type.getName().toString() + ";", null));
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
	private void instrumentStartPhase(final MethodEditor editor) {
		NonDeterministicIfInstrumenter ifInstr = new NonDeterministicIfInstrumenter();
		Set<ExplicitDeclaration> declarations = wrapper.getFramework().getStartPhase().getDeclarations();
		int maxUsesIndex = LocalAllocator.allocate(editor.getData(), Constants.TYPE_double);
		editor.insertAfter(0, new MethodEditor.Patch() {
			@Override
			public void emitTo(final MethodEditor.Output w) {
				w.emit(ConstantInstruction.make((double) declarations.size()));
				w.emit(StoreInstruction.make(Constants.TYPE_double, maxUsesIndex));
			}
		});
		ifInstr.instrumentBeginning(editor, maxUsesIndex);
		for (ExplicitDeclaration dec : declarations) {
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
	private void instrumentEndPhase(final MethodEditor editor) {
		instrumentExplicitDeclaration(editor, wrapper.getFramework().getEndPhase().getEnd(), 0, null);
	}

	/**
	 * Instruments the bytecode for the working phase.
	 *
	 * @param clInstr instrumenter of the artificial class.
	 * @param editor the editor for the working phase method.
	 * @param workingDatas list for generated methods.
	 */
	private void instrumentWorkingPhase(final ClassInstrumenter clInstr, final MethodEditor editor,
			final ArrayList<MethodData> workingDatas) {
		List<WorkingPhase> workingPhases = wrapper.getFramework().getWorkingPhases();
		ArrayList<Integer> allocatedLabels = new ArrayList<>();
		for (WorkingPhase working : workingPhases) {
			if (working.getThreadType() == ThreadType.MULTI) {
				allocatedLabels.add(editor.allocateLabel());
				allocatedLabels.add(editor.allocateLabel());
				allocatedLabels.add(editor.allocateLabel());
				allocatedLabels.add(editor.allocateLabel());
				allocatedLabels.add(editor.allocateLabel());
			}
		}
		editor.insertAfter(0, new MethodEditor.Patch() {
			@Override
			public void emitTo(final MethodEditor.Output w) {
				int forLabelCounter = 0;
				int procIndex = 0;
				int threadAIndex = 0;
				int loopIndex = 0;
				for (int i = 0; i < workingPhases.size(); i++) {
					WorkingPhase working = workingPhases.get(i);
					if (working.getThreadType() == ThreadType.SINGLE) {
						w.emit(NewInstruction.make(AC_WW_BYTECODE_NAME, 0));
						w.emit(DupInstruction.make(0));
						w.emit(LoadInstruction.make(AC_BYTECODE_NAME, 0));
						w.emit(DupInstruction.make(0));
						w.emit(ConstantInstruction.make(i));
						w.emit(InvokeInstruction.make("(" + AC_BYTECODE_NAME + AC_BYTECODE_NAME + Constants.TYPE_int
								+ ")" + Constants.TYPE_void, AC_WW_BYTECODE_NAME, APIConstants.INIT,
								IInvokeInstruction.Dispatch.SPECIAL));
						w.emit(InvokeInstruction.make("()" + Constants.TYPE_void, AC_WW_BYTECODE_NAME, "run",
								IInvokeInstruction.Dispatch.INTERFACE));
					} else if (working.getThreadType() == ThreadType.MULTI) {
						if (procIndex == 0) {
							procIndex = LocalAllocator.allocate(editor.getData(), Constants.TYPE_int);
							threadAIndex = LocalAllocator.allocate(editor.getData(), "[" + THREAD_BYTECODE_NAME);
							loopIndex = LocalAllocator.allocate(editor.getData(), Constants.TYPE_int);
							w.emit(InvokeInstruction.make("()" + RUNTIME_BYTECODE_NAME, RUNTIME_BYTECODE_NAME,
									"getRuntime", IInvokeInstruction.Dispatch.STATIC));
							w.emit(InvokeInstruction.make("()" + Constants.TYPE_int, RUNTIME_BYTECODE_NAME,
									"availableProcessors", IInvokeInstruction.Dispatch.VIRTUAL));
							w.emit(StoreInstruction.make(Constants.TYPE_int, procIndex));
							w.emit(LoadInstruction.make(Constants.TYPE_int, procIndex));
							w.emit(NewInstruction.make("[" + THREAD_BYTECODE_NAME, 1));
							w.emit(StoreInstruction.make("[" + THREAD_BYTECODE_NAME, threadAIndex));
						}
						w.emit(ConstantInstruction.make(0));
						w.emit(StoreInstruction.make(Constants.TYPE_int, loopIndex));

						// First loop: creation and start of threads.
						w.emitLabel(allocatedLabels.get(forLabelCounter));
						w.emit(LoadInstruction.make(Constants.TYPE_int, loopIndex));
						w.emit(LoadInstruction.make(Constants.TYPE_int, procIndex));
						w.emit(ConditionalBranchInstruction.make(Constants.TYPE_int,
								IConditionalBranchInstruction.Operator.GE, allocatedLabels.get(forLabelCounter + 1)));
						w.emit(LoadInstruction.make("[" + THREAD_BYTECODE_NAME, threadAIndex));
						w.emit(LoadInstruction.make(Constants.TYPE_int, loopIndex));
						w.emit(NewInstruction.make(THREAD_BYTECODE_NAME, 0));
						w.emit(DupInstruction.make(0));
						w.emit(NewInstruction.make(AC_WW_BYTECODE_NAME, 0));
						w.emit(DupInstruction.make(0));
						w.emit(LoadInstruction.make(AC_BYTECODE_NAME, 0));
						w.emit(DupInstruction.make(0));
						w.emit(ConstantInstruction.make(i));
						w.emit(InvokeInstruction.make("(" + AC_BYTECODE_NAME + AC_BYTECODE_NAME + Constants.TYPE_int
								+ ")" + Constants.TYPE_void, AC_WW_BYTECODE_NAME, APIConstants.INIT,
								IInvokeInstruction.Dispatch.SPECIAL));
						w.emit(InvokeInstruction.make("(Ljava/lang/Runnable;)" + Constants.TYPE_void,
								THREAD_BYTECODE_NAME, APIConstants.INIT, IInvokeInstruction.Dispatch.SPECIAL));
						w.emit(ArrayStoreInstruction.make("[" + THREAD_BYTECODE_NAME));
						w.emit(LoadInstruction.make("[" + THREAD_BYTECODE_NAME, threadAIndex));
						w.emit(LoadInstruction.make(Constants.TYPE_int, loopIndex));
						w.emit(ArrayLoadInstruction.make("[" + THREAD_BYTECODE_NAME));
						w.emit(InvokeInstruction.make("()" + Constants.TYPE_void, THREAD_BYTECODE_NAME, "start",
								IInvokeInstruction.Dispatch.VIRTUAL));
						w.emit(LoadInstruction.make(Constants.TYPE_int, loopIndex));
						w.emit(ConstantInstruction.make(1));
						w.emit(BinaryOpInstruction.make(Constants.TYPE_int, IBinaryOpInstruction.Operator.ADD));
						w.emit(StoreInstruction.make(Constants.TYPE_int, loopIndex));
						w.emit(GotoInstruction.make(allocatedLabels.get(forLabelCounter)));

						// Second loop: every thread will be joined.
						w.emitLabel(allocatedLabels.get(forLabelCounter + 1));
						w.emit(ConstantInstruction.make(0));
						w.emit(StoreInstruction.make(Constants.TYPE_int, loopIndex));
						w.emitLabel(allocatedLabels.get(forLabelCounter + 2));
						w.emit(LoadInstruction.make(Constants.TYPE_int, loopIndex));
						w.emit(LoadInstruction.make(Constants.TYPE_int, procIndex));
						w.emit(ConditionalBranchInstruction.make(Constants.TYPE_int,
								IConditionalBranchInstruction.Operator.GE, allocatedLabels.get(forLabelCounter + 3)));
						w.emit(LoadInstruction.make("[" + THREAD_BYTECODE_NAME, threadAIndex));
						w.emit(LoadInstruction.make(Constants.TYPE_int, loopIndex));
						w.emit(ArrayLoadInstruction.make("[" + THREAD_BYTECODE_NAME));
						ExceptionHandler h = new ExceptionHandler(allocatedLabels.get(forLabelCounter + 4),
								"Ljava/lang/InterruptedException;");
						w.emit(InvokeInstruction.make("()" + Constants.TYPE_void, THREAD_BYTECODE_NAME, "join",
								IInvokeInstruction.Dispatch.VIRTUAL), new ExceptionHandler[] {h});
						w.emit(LoadInstruction.make(Constants.TYPE_int, loopIndex));
						w.emit(ConstantInstruction.make(1));
						w.emit(BinaryOpInstruction.make(Constants.TYPE_int, IBinaryOpInstruction.Operator.ADD));
						w.emit(StoreInstruction.make(Constants.TYPE_int, loopIndex));
						w.emit(GotoInstruction.make(allocatedLabels.get(forLabelCounter + 2)));

						// Exception handling of InterruptedException that can occur during join().
						w.emitLabel(allocatedLabels.get(forLabelCounter + 4));
						w.emit(PopInstruction.make(1));
						w.emit(GotoInstruction.make(allocatedLabels.get(forLabelCounter + 2)));

						// End of loops.
						w.emitLabel(allocatedLabels.get(forLabelCounter + 3));
						forLabelCounter += 5;
					}
				}
			}
		});
		for (int i = 0; i < workingPhases.size(); i++) {
			WorkingPhase working = workingPhases.get(i);
			MethodData data = clInstr.createEmptyMethodData("w" + i, "()" + Constants.TYPE_void,
					Constants.ACC_PROTECTED);
			MethodEditor wEditor = new MethodEditor(data);
			instrumentActualWorkingPhaseContent(wEditor, working);
			workingDatas.add(data);
		}
	}

	/**
	 * Instruments a new ArtificialClass method with the actual content of a working phase.
	 *
	 * @param editor the edtior for bytecode instrumentation.
	 * @param working the working phase.
	 */
	private void instrumentActualWorkingPhaseContent(final MethodEditor editor, final WorkingPhase working) {
		editor.beginPass();
		editor.replaceWith(0, new MethodEditor.Patch() {
			@Override
			public void emitTo(final MethodEditor.Output w) {
			}
		});
		NonDeterministicLoopInstrumenter loop = new NonDeterministicLoopInstrumenter();
		NonDeterministicIfInstrumenter ifInstr = new NonDeterministicIfInstrumenter();
		loop.instrumentLoopBeginning(editor);
		int caseCounter = 0;
		for (Rule r : working.getRules()) {
			if (r.getClass() == MethodCollector.class) {
				MethodCollector coll = (MethodCollector) r;
				for (IClass cl : coll.getFrameworkClasses()) {
					caseCounter += coll.getMethodCollection(cl).size() * wrapper.getInstancesCount(cl);
				}
			} else if (r.getClass() == Block.class) {
				caseCounter += countBlocks((Block) r);
			}
		}
		int caseCounterCopy = caseCounter;
		int counterIndex = LocalAllocator.allocate(editor.getData(), Constants.TYPE_double);
		editor.insertAfter(0, new MethodEditor.Patch() {
			@Override
			public void emitTo(final MethodEditor.Output w) {
				w.emit(ConstantInstruction.make((double) caseCounterCopy));
				w.emit(StoreInstruction.make(Constants.TYPE_double, counterIndex));
			}
		});
		ifInstr.instrumentBeginning(editor, counterIndex);
		for (Rule r : working.getRules()) {
			if (r.getClass() == MethodCollector.class) {
				MethodCollector coll = (MethodCollector) r;
				for (IClass cl : coll.getFrameworkClasses()) {
					int instancesCount = wrapper.getInstancesCount(cl);
					for (int i = 0; i < instancesCount; i++) {
						int exLabel = editor.allocateLabel();
						int afterExLabel = editor.allocateLabel();
						ExceptionHandler[] handlers = new ExceptionHandler[] {new ExceptionHandler(exLabel,
								"Ljava/lang/IndexOutOfBoundsException;")};
						for (IMethod m : coll.getMethodCollection(cl)) {
							ifInstr.instrumentCaseBeginning(editor);
							instrumentForGettingInstanceCollection(editor, cl);
							int index = i;
							editor.insertAfter(0, new MethodEditor.Patch() {
								@Override
								public void emitTo(final MethodEditor.Output w) {
									w.emit(ConstantInstruction.make(index));
									w.emit(InvokeInstruction.make("(" + Constants.TYPE_int + ")"
											+ Constants.TYPE_Object, LIST_BYTECODE_NAME, "get",
											IInvokeInstruction.Dispatch.INTERFACE), handlers);
									w.emit(CheckCastInstruction.make(cl.getName().toString() + ";"));
								}
							});
							instantiateParameters(editor, m);
							editor.insertAfter(0, new MethodEditor.Patch() {
								@Override
								public void emitTo(final MethodEditor.Output w) {
									w.emit(InvokeInstruction.make(m.getDescriptor().toString(),
											cl.getName().toString() + ";",
											m.getName().toString(), cl.isInterface()
											? IInvokeInstruction.Dispatch.INTERFACE
													: IInvokeInstruction.Dispatch.VIRTUAL));
								}
							});
						}
						editor.insertAfter(0, new MethodEditor.Patch() {
							@Override
							public void emitTo(final MethodEditor.Output w) {
								w.emit(GotoInstruction.make(afterExLabel));
								w.emitLabel(exLabel);
								w.emit(PopInstruction.make(1));
								w.emitLabel(afterExLabel);
							}
						});
					}
				}
			} else if (r.getClass() == Block.class) {
				instrumentBlock(editor, ifInstr, (Block) r);
			}
		}
		ifInstr.instrumentEnd(editor);
		loop.instrumentLoopEnd(editor);
		editor.insertAfter(0, new MethodEditor.Patch() {
			@Override
			public void emitTo(final MethodEditor.Output w) {
				w.emit(ReturnInstruction.make(Constants.TYPE_void));
			}
		});
		editor.applyPatches();
		editor.endPass();
	}

	/**
	 * Counts the number of resulting blocks for a block rule.
	 *
	 * @param b the block rule.
	 * @return the number of blocks.
	 */
	private int countBlocks(final Block b) {
		return wrapper.getInstancesCount(b.getIClass()) * (b.getInnerBlock() != null ? countBlocks(b.getInnerBlock())
				: 0);
	}

	/**
	 * Instruments the bytecode with a block rule.
	 *
	 * @param editor the editor for bytecode instrumentation.
	 * @param ifInstr instrumenter for the non-deterministic if-else-if-clause in the working phase.
	 * @param b the block rule.
	 */
	private void instrumentBlock(final MethodEditor editor, final NonDeterministicIfInstrumenter ifInstr,
			final Block b) {
		if (b.getInnerBlock() == null) {
			for (int i = 0; i < wrapper.getInstancesCount(b.getIClass()); i++) {
				ifInstr.instrumentCaseBeginning(editor);
				instrumentForGettingInstanceCollection(editor, b.getIClass());
				int index = i;
				int instanceIndex = LocalAllocator.allocate(editor.getData(), b.getClassName());
				editor.insertAfter(0, new MethodEditor.Patch() {
					@Override
					public void emitTo(final MethodEditor.Output w) {
						w.emit(ConstantInstruction.make(index));
						w.emit(InvokeInstruction.make("(" + Constants.TYPE_int + ")" + Constants.TYPE_Object,
								LIST_BYTECODE_NAME, "get", IInvokeInstruction.Dispatch.INTERFACE));
						w.emit(CheckCastInstruction.make(b.getClassName() + ";"));
						w.emit(StoreInstruction.make(b.getClassName() + ";", instanceIndex));
					}
				});
				instrumentExplicitDeclaration(editor, b.getDeclaration(), instanceIndex, b.getIClass());
			}
		} else {
			for (int i = 0; i < wrapper.getInstancesCount(b.getIClass()); i++) {
				instrumentBlock(editor, ifInstr, b.getInnerBlock());
			}
		}
	}

	/**
	 * Instruments the bytecode of the runnable class used in the working phase.
	 *
	 * @param editor the editor for byteocde instrumentation.
	 */
	private void instrumentRunnableClassForWorkingPhase(final MethodEditor editor) {
		int cases = wrapper.getFramework().getWorkingPhases().size();
		ArrayList<Integer> allocatedLabels = new ArrayList<>();
		for (int i = 0; i <= cases; i++) {
			allocatedLabels.add(editor.allocateLabel());
		}
		editor.insertAfter(0, new MethodEditor.Patch() {
			@Override
			public void emitTo(final MethodEditor.Output w) {
				for (int i = 0; i < cases; i++) {
					w.emitLabel(allocatedLabels.get(i));
					w.emit(LoadInstruction.make(AC_WW_BYTECODE_NAME, 0));
					w.emit(GetInstruction.make(Constants.TYPE_int, AC_WW_BYTECODE_NAME, "phaseNumber", false));
					w.emit(ConstantInstruction.make(i));
					w.emit(ConditionalBranchInstruction.make(Constants.TYPE_int,
							IConditionalBranchInstruction.Operator.NE, allocatedLabels.get(i + 1)));
					w.emit(LoadInstruction.make(AC_WW_BYTECODE_NAME, 0));
					w.emit(GetInstruction.make(AC_BYTECODE_NAME, AC_WW_BYTECODE_NAME, "outerInstance", false));
					w.emit(InvokeInstruction.make("()" + Constants.TYPE_void, AC_BYTECODE_NAME, "w" + i,
							IInvokeInstruction.Dispatch.VIRTUAL));
				}
				w.emitLabel(allocatedLabels.get(cases));
				w.emit(ReturnInstruction.make(Constants.TYPE_void));
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
		private void instrumentBeginning(final MethodEditor editor, final int maxCasesIndex) {
			randomIndex = LocalAllocator.allocate(editor.getData(), Constants.TYPE_int);
			allocatedCaseLabels = new ArrayList<>();
			caseCounter = 0;
			editor.insertAfter(0, new MethodEditor.Patch() {
				@Override
				public void emitTo(final MethodEditor.Output w) {
					w.emit(InvokeInstruction.make("()" + Constants.TYPE_double, "Ljava/lang/Math;", "random",
							IInvokeInstruction.Dispatch.STATIC));
					w.emit(LoadInstruction.make(Constants.TYPE_double, maxCasesIndex));
					w.emit(BinaryOpInstruction.make(Constants.TYPE_double, IBinaryOpInstruction.Operator.MUL));
					w.emit(ConversionInstruction.make(Constants.TYPE_double, Constants.TYPE_int));
					w.emit(StoreInstruction.make(Constants.TYPE_int, randomIndex));
				}
			});
		}

		/**
		 * Instruments bytecode with the beginning of a case. Must be called before the actual content of the case is
		 * added.
		 *
		 * @param editor the editor for bytecode instrumentation.
		 */
		private void instrumentCaseBeginning(final MethodEditor editor) {
			if (caseCounter == 0) {
				allocatedCaseLabels.add(editor.allocateLabel());
			}
			allocatedCaseLabels.add(editor.allocateLabel());
			int caseCounterCopy = caseCounter;
			editor.insertAfter(0, new MethodEditor.Patch() {
				@Override
				public void emitTo(final MethodEditor.Output w) {
					w.emitLabel(allocatedCaseLabels.get(caseCounterCopy));
					w.emit(LoadInstruction.make(Constants.TYPE_int, randomIndex));
					w.emit(ConstantInstruction.make(caseCounterCopy));
					w.emit(ConditionalBranchInstruction.make(Constants.TYPE_int,
							IConditionalBranchInstruction.Operator.NE, allocatedCaseLabels.get(caseCounterCopy + 1)));
				}
			});
			caseCounter++;
		}

		/**
		 * Instruments bytecode with the end of the clause.
		 *
		 * @param editor the editor for bytecode instrumentation.
		 */
		private void instrumentEnd(final MethodEditor editor) {
			editor.insertAfter(0, new MethodEditor.Patch() {
				@Override
				public void emitTo(final MethodEditor.Output w) {
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
		private void instrumentLoopBeginning(final MethodEditor editor) {
			loopIndex = LocalAllocator.allocate(editor.getData(), Constants.TYPE_int);
			int randomIndex = LocalAllocator.allocate(editor.getData(), Constants.TYPE_int);
			beginLabel = editor.allocateLabel();
			endLabel = editor.allocateLabel();
			editor.insertAfter(0, new MethodEditor.Patch() {
				@Override
				public void emitTo(final MethodEditor.Output w) {
					w.emit(InvokeInstruction.make("()" + Constants.TYPE_double, "Ljava/lang/Math;", "random",
							IInvokeInstruction.Dispatch.STATIC));
					final double maxLoops = 1000000.0;
					w.emit(ConstantInstruction.make(maxLoops));
					w.emit(BinaryOpInstruction.make(Constants.TYPE_double, IBinaryOpInstruction.Operator.MUL));
					w.emit(ConversionInstruction.make(Constants.TYPE_double, Constants.TYPE_int));
					w.emit(StoreInstruction.make(Constants.TYPE_int, randomIndex));
					w.emit(ConstantInstruction.make(0));
					w.emit(StoreInstruction.make(Constants.TYPE_int, loopIndex));
					w.emitLabel(beginLabel);
					w.emit(LoadInstruction.make(Constants.TYPE_int, loopIndex));
					w.emit(LoadInstruction.make(Constants.TYPE_int, randomIndex));
					w.emit(ConditionalBranchInstruction.make(Constants.TYPE_int,
							IConditionalBranchInstruction.Operator.GE, endLabel));
				}
			});
		}

		/**
		 * Instruments the bytecode with th end of the loop.
		 *
		 * @param editor the editor for bytecode instrumentation.
		 */
		private void instrumentLoopEnd(final MethodEditor editor) {
			editor.insertAfter(0, new MethodEditor.Patch() {
				@Override
				public void emitTo(final MethodEditor.Output w) {
					w.emit(LoadInstruction.make(Constants.TYPE_int, loopIndex));
					w.emit(ConstantInstruction.make(1));
					w.emit(BinaryOpInstruction.make(Constants.TYPE_int, IBinaryOpInstruction.Operator.ADD));
					w.emit(StoreInstruction.make(Constants.TYPE_int, loopIndex));
					w.emit(GotoInstruction.make(beginLabel));
					w.emitLabel(endLabel);
				}
			});
		}
	}
}
