package edu.kit.ipd.pp.joframes.api;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.shrikeBT.ArrayStoreInstruction;
import com.ibm.wala.shrikeBT.BinaryOpInstruction;
import com.ibm.wala.shrikeBT.CheckCastInstruction;
import com.ibm.wala.shrikeBT.ConditionalBranchInstruction;
import com.ibm.wala.shrikeBT.ConstantInstruction;
import com.ibm.wala.shrikeBT.Constants;
import com.ibm.wala.shrikeBT.ConversionInstruction;
import com.ibm.wala.shrikeBT.ExceptionHandler;
import com.ibm.wala.shrikeBT.GetInstruction;
import com.ibm.wala.shrikeBT.IBinaryOpInstruction;
import com.ibm.wala.shrikeBT.IConditionalBranchInstruction;
import com.ibm.wala.shrikeBT.IInvokeInstruction;
import com.ibm.wala.shrikeBT.InvokeInstruction;
import com.ibm.wala.shrikeBT.LoadInstruction;
import com.ibm.wala.shrikeBT.MethodEditor;
import com.ibm.wala.shrikeBT.NewInstruction;
import com.ibm.wala.shrikeBT.ReturnInstruction;
import com.ibm.wala.shrikeBT.StoreInstruction;
import com.ibm.wala.shrikeBT.Util;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.types.TypeReference;
import edu.kit.ipd.pp.joframes.api.exceptions.InstrumenterException;
import edu.kit.ipd.pp.joframes.api.logging.Log;
import edu.kit.ipd.pp.joframes.ast.acha.MethodCollector;
import edu.kit.ipd.pp.joframes.ast.ap.Block;
import edu.kit.ipd.pp.joframes.ast.base.AstBaseClass;
import edu.kit.ipd.pp.joframes.ast.base.ExplicitDeclaration;
import edu.kit.ipd.pp.joframes.ast.base.Method;
import edu.kit.ipd.pp.joframes.ast.base.Rule;
import edu.kit.ipd.pp.joframes.ast.base.StaticMethod;
import edu.kit.ipd.pp.joframes.ast.base.ThreadType;
import edu.kit.ipd.pp.joframes.ast.base.WorkingPhase;
import edu.kit.ipd.pp.joframes.shrike.ClassInstrumenterWrapper;
import edu.kit.ipd.pp.joframes.shrike.InstructionFactory;
import edu.kit.ipd.pp.joframes.shrike.InstrumenterWrapper;
import edu.kit.ipd.pp.joframes.shrike.MethodWrapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
	private static final String OWN_JAR_FILE = "joframes-api-0.3.jar#" + PACKAGE;
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
	 * Default method signature: ()V.
	 */
	private static final String DEFAULT_SIGNATURE = "()" + Constants.TYPE_void;
	/**
	 * Stores the maximum level for parameter instantiation.
	 */
	private static final int MAX_LEVEL = 3;
	/**
	 * Stores the framework jar files that will be included in the output jar.
	 */
	private String[] frameworkJars;
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
	 * Sets the paths to the jar files containing the framework classes. They will be included in the output jar file.
	 *
	 * @param fwJars the paths. Can be null.
	 */
	public void setFrameworkJars(final String[] fwJars) {
		frameworkJars = fwJars;
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
		Log.log("Instrumenting the bytecode.");
		this.actualOutput = output;
		this.wrapper = frameworkWrapper;
		try {
			Log.logExtended("Loading the jar files.");
			InstrumenterWrapper instrumenter = new InstrumenterWrapper();
			instrumenter.setExclusionRegex(APIConstants.DEFAULT_EXCLUSION_REGEX);
			try {
				for (String appJar : applicationJars) {
					instrumenter.addInputJar(appJar);
				}
				if (frameworkJars != null) {
					for (String fwJar : frameworkJars) {
						instrumenter.addInputJar(fwJar);
					}
				}
			} catch (IllegalArgumentException e) {
				Log.endLog("A jar file is not valid.");
				throw new InstrumenterException("A jar file is not valid.", e);
			}
			// For test purposes, the loading of the external api classes is done by assuming that
			// the project is used within eclipse.
			if (Boolean.getBoolean(APIConstants.TEST_SYSTEM_PROPERTY)) {
				String path = new File("").getAbsoluteFile().getParentFile().getAbsolutePath()
						+ File.separator + "joframes-api-external" + File.separator + "target" + File.separator
						+ "classes" + File.separator;
				instrumenter.addInputDirectory(path);
			} else {
				// Actual class loading for the stand-alone application.
				instrumenter.addInputJarEntry(OWN_JAR_FILE + IC_NAME);
				instrumenter.addInputJarEntry(OWN_JAR_FILE + AC_NAME);
				instrumenter.addInputJarEntry(OWN_JAR_FILE + AC_WW_NAME);
			}
			Log.logExtended("Instrumenting the InstanceCollector.");
			ClassInstrumenterWrapper clInstr = instrumenter.getClassInstrumenter(PACKAGE + IC_NAME);
			MethodWrapper editor = clInstr.getMethod(CLINIT, DEFAULT_SIGNATURE);
			for (IClass cl : wrapper.getFrameworkClasses()) {
				editor.addInstructionAtEnd(ConstantInstruction.makeString(Util.makeClass(cl.getName().toString()
						+ ";")));
				editor.addInstructionAtEnd(InvokeInstruction.make("(" + Constants.TYPE_String + ")"
						+ Constants.TYPE_Class, Constants.TYPE_Class, CLASS_FOR_NAME_METHOD,
						IInvokeInstruction.Dispatch.STATIC));
				editor.addInstructionAtEnd(InvokeInstruction.make("(" + Constants.TYPE_Class + ")"
						+ Constants.TYPE_void, IC_BYTECODE_NAME, "addClass",
						IInvokeInstruction.Dispatch.STATIC));
			}
			Log.logExtended("Instrumenting the application to collect instances.");
			editor.instrumentMethod();
			instrumenter.visitClasses(classWrapper -> {
				classWrapper.visitMethods(methodWrapper -> {
					methodWrapper.visitInstructions(new MethodEditor.Visitor() {
						@Override
						public void visitInvoke(final IInvokeInstruction instruction) {
							if (checkInvokeInstruction(instruction, methodWrapper)) {
								this.insertAfter(new MethodEditor.Patch() {
									@Override
									public void emitTo(final MethodEditor.Output w) {
										w.emit(InstructionFactory.makeDup());
										w.emit(InvokeInstruction.make("(" + Constants.TYPE_Object + ")"
												+ Constants.TYPE_void, IC_BYTECODE_NAME, "addInstance",
												IInvokeInstruction.Dispatch.STATIC));
									}
								});
							}
						}
					});
				});
			});
			Log.logExtended("Instrumenting the ArtificialClass.");
			clInstr = instrumenter.getClassInstrumenter(PACKAGE + AC_NAME);
			editor = clInstr.getMethod(START, DEFAULT_SIGNATURE);
			instrumentStartPhase(editor);
			editor = clInstr.getMethod(WORKING, DEFAULT_SIGNATURE);
			instrumentWorkingPhase(editor);
			List<WorkingPhase> workingPhases = wrapper.getFramework().getWorkingPhases();
			for (int i = 0; i < workingPhases.size(); i++) {
				editor = clInstr.createMethod(Constants.ACC_PROTECTED, "w" + i, DEFAULT_SIGNATURE);
				instrumentActualWorkingPhaseContent(editor, workingPhases.get(i));
			}
			editor = clInstr.getMethod(END, DEFAULT_SIGNATURE);
			instrumentEndPhase(editor);
			clInstr = instrumenter.getClassInstrumenter(PACKAGE + AC_WW_NAME);
			editor = clInstr.getMethod("run", DEFAULT_SIGNATURE);
			instrumentRunnableClassForWorkingPhase(editor);
			instrumenter.setOutput(actualOutput);
			instrumenter.outputClasses();
			Log.logExtended("Finished instrumenting.");
		} catch (IOException e) {
			Log.endLog("Instrumentation failed.");
			throw new InstrumenterException("An IO exception occurred while instrumenting the bytecode.", e);
		} catch (InvalidClassFileException e) {
			Log.endLog("Instrumentation failed.");
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
		IClass subclass = wrapper.getIClass(wrapper.getClassHierarchy().getScope().getApplicationLoader(), className);
		if (subclass == null) {
			return false;
		}
		for (IClass cl : wrapper.getFrameworkClasses()) {
			if (wrapper.isDirectSubtype(cl, subclass)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks that an instruction invokes a constructor which is not called in the constructor of a direct subclass.
	 *
	 * @param instruction the instruction.
	 * @param method method in which the instruction resides.
	 * @return true if the instruction invokes a constructor. false otherwise.
	 */
	private boolean checkInvokeInstruction(final IInvokeInstruction instruction, final MethodWrapper method) {
		String invokedClass = instruction.getClassType().substring(0, instruction.getClassType().length() - 1);
		String methodClass = method.getClassType().substring(0, method.getClassType().length() - 1);
		boolean result =  instruction.getMethodName().equals(APIConstants.INIT)
				&& isSubclassOfFrameworkClasses(invokedClass) && !(method.getMethodName().equals(APIConstants.INIT)
						&& areDirectSubclasses(invokedClass, methodClass));
		if (result) {
			wrapper.countOneInstance(wrapper.getIClass(wrapper.getClassHierarchy().getScope().getApplicationLoader(),
				invokedClass));
		}
		return result;
	}

	/**
	 * Checks that two classes are direct subclasses of each other or equal.
	 *
	 * @param classOne one class.
	 * @param classTwo another class.
	 * @return true if both classes are equal or direct subclasses. false otherwise.
	 */
	private boolean areDirectSubclasses(final String classOne, final String classTwo) {
		IClass one = wrapper.getIClass(wrapper.getClassHierarchy().getScope().getApplicationLoader(), classOne);
		IClass two = wrapper.getIClass(wrapper.getClassHierarchy().getScope().getApplicationLoader(), classTwo);
		if (one == null || two == null) {
			return false;
		}
		return one == two || wrapper.isDirectSubtype(one, two) || wrapper.isDirectSubtype(two, one);
	}

	/**
	 * Instruments the bytecode with instructions to get an instance collection from the InstanceCollector.
	 * After calling this method, the instance collection is on top of the stack.
	 *
	 * @param editor the editor for bytecode instrumentation.
	 * @param cl class for which the instance collection is obtained.
	 */
	private void instrumentForGettingInstanceCollection(final MethodWrapper editor, final IClass cl) {
		int exLabel = editor.allocateLabel();
		int afterExLabel = editor.allocateLabel();
		ExceptionHandler[] h = new ExceptionHandler[]
				{new ExceptionHandler(exLabel, "Ljava/lang/ClassNotFoundException;")};
		editor.addInstructionAtEnd(ConstantInstruction.makeString(Util.makeClass(cl.getName().toString() + ";")));
		editor.addInstructionAtEnd(InvokeInstruction.make("(" + Constants.TYPE_String + ")" + Constants.TYPE_Class,
				Constants.TYPE_Class, CLASS_FOR_NAME_METHOD, IInvokeInstruction.Dispatch.STATIC), h);
		editor.addInstructionAtEnd(InvokeInstruction.make("(" + Constants.TYPE_Class + ")" + LIST_BYTECODE_NAME,
				IC_BYTECODE_NAME, "getInstances", IInvokeInstruction.Dispatch.STATIC));
		editor.addInstructionAtEnd(InstructionFactory.makeGoto(afterExLabel));
		editor.addInstructionAtEnd(exLabel, ReturnInstruction.make(Constants.TYPE_void));
		editor.addLabelAtEnd(afterExLabel);
	}

	/**
	 * Instruments the bytecode with an explicit declaration.
	 *
	 * @param editor the editor for the bytecode instrumentation.
	 * @param declaration the explicit declaration.
	 * @param instanceIndex local variable index of an instance that is put in or -1 if no instance is put in.
	 * @param instanceType type of the instance put in or null if no instance is put in.
	 * @throws InstrumenterException if the instrumentation fails.
	 */
	private void instrumentExplicitDeclaration(final MethodWrapper editor, final ExplicitDeclaration declaration,
			final int instanceIndex, final IClass instanceType) throws InstrumenterException {
		boolean useForLoop = declaration.getClassName() != null;
		IClass actualInstanceType = instanceType;
		int iteratorIndex = 0;
		int localInstanceIndex = instanceIndex;
		int beginLoopLabel = 0;
		int afterLoopLabel = 0;
		if (useForLoop) {
			actualInstanceType = declaration.getIClass();
			iteratorIndex = editor.allocateLocalVariable(ITERATOR_BYTECODE_NAME);
			localInstanceIndex = editor.allocateLocalVariable(declaration.getClassName() + ";");
			beginLoopLabel = editor.allocateLabel();
			afterLoopLabel = editor.allocateLabel();
			instrumentForGettingInstanceCollection(editor, declaration.getIClass());
			editor.addInstructionAtEnd(InvokeInstruction.make("()" + ITERATOR_BYTECODE_NAME, LIST_BYTECODE_NAME,
					"iterator", IInvokeInstruction.Dispatch.INTERFACE));
			editor.addInstructionAtEnd(StoreInstruction.make(ITERATOR_BYTECODE_NAME, iteratorIndex));
			editor.addInstructionAtEnd(beginLoopLabel,
					LoadInstruction.make(ITERATOR_BYTECODE_NAME, iteratorIndex));
			editor.addInstructionAtEnd(InvokeInstruction.make("()" + Constants.TYPE_boolean, ITERATOR_BYTECODE_NAME,
					"hasNext", IInvokeInstruction.Dispatch.INTERFACE));
			editor.addInstructionAtEnd(ConstantInstruction.make(0));
			editor.addInstructionAtEnd(ConditionalBranchInstruction.make(Constants.TYPE_int,
					IConditionalBranchInstruction.Operator.EQ, afterLoopLabel));
			editor.addInstructionAtEnd(LoadInstruction.make(ITERATOR_BYTECODE_NAME, iteratorIndex));
			editor.addInstructionAtEnd(InvokeInstruction.make("()" + Constants.TYPE_Object, ITERATOR_BYTECODE_NAME,
					"next", IInvokeInstruction.Dispatch.INTERFACE));
			editor.addInstructionAtEnd(CheckCastInstruction.make(declaration.getClassName() + ";"));
			editor.addInstructionAtEnd(StoreInstruction.make(declaration.getClassName() + ";", localInstanceIndex));
		}
		instrumentExplicitDeclarationContent(editor, declaration, localInstanceIndex, actualInstanceType);
		if (useForLoop) {
			editor.addInstructionAtEnd(InstructionFactory.makeGoto(beginLoopLabel));
			editor.addLabelAtEnd(afterLoopLabel);
		}
		for (IClass appClass : declaration.getApplicationClasses()) {
			wrapper.countOneInstance(appClass);
			instrumentExplicitDeclarationContent(editor, declaration, editor.allocateLocalVariable(
					appClass.getName().toString() + ";"), appClass);
		}
	}

	/**
	 * Instruments the bytecode with the content of an explicit declaration.
	 *
	 * @param editor the editor for bytecode instrumentation.
	 * @param declaration the explicit declaration.
	 * @param instanceIndex index where the local variable to use is stored.
	 * @param instanceType type of the local variable.
	 * @throws InstrumenterException if the instrumentation fails.
	 */
	private void instrumentExplicitDeclarationContent(final MethodWrapper editor, final ExplicitDeclaration declaration,
			final int instanceIndex, final IClass instanceType) throws InstrumenterException {
		for (int i = 0; i < declaration.getNumberOfCallsAndDeclarations(); i++) {
			AstBaseClass abc = declaration.getCallOrDeclaration(i);
			if (abc.getClass() == Method.class) {
				Method m = (Method) abc;
				if (m.getSignature().equals(APIConstants.CONSTRUCTOR)) {
					IMethod init = declaration.getConstructor(instanceType);
					if (init == null) {
						continue;
					}
					editor.addInstructionAtEnd(NewInstruction.make(instanceType.getName().toString() + ";", 0));
					editor.addInstructionAtEnd(InstructionFactory.makeDup());
					instantiateParameters(editor, init, 1);
					editor.addInstructionAtEnd(InstructionFactory.makeInit(instanceType.getName().toString() + ";",
							init.getDescriptor().toString()));
					editor.addInstructionAtEnd(InstructionFactory.makeDup());
					editor.addInstructionAtEnd(InvokeInstruction.make("(" + Constants.TYPE_Object + ")"
							+ Constants.TYPE_void, IC_BYTECODE_NAME, "addInstance",
							IInvokeInstruction.Dispatch.STATIC));
					editor.addInstructionAtEnd(StoreInstruction.make(instanceType.getName().toString() + ";",
							instanceIndex));
					continue;
				}
				editor.addInstructionAtEnd(LoadInstruction.make(instanceType.getName().toString() + ";",
						instanceIndex));
				instantiateParameters(editor, m.getMethod(), 1);
				editor.addInstructionAtEnd(InvokeInstruction.make(m.getMethod().getDescriptor().toString(),
						instanceType.getName().toString() + ";", m.getMethod().getName().toString(),
								instanceType.isInterface() ? IInvokeInstruction.Dispatch.INTERFACE
										: IInvokeInstruction.Dispatch.VIRTUAL));
				if (m.getMethod().getReturnType() != TypeReference.Void) {
					editor.addInstructionAtEnd(InstructionFactory.makePop());
				}
			} else if (abc.getClass() == StaticMethod.class) {
				StaticMethod st = (StaticMethod) abc;
				instantiateParameters(editor, st.getMethod(), 1);
				editor.addInstructionAtEnd(InvokeInstruction.make(st.getMethod().getDescriptor().toString(),
						st.getIClass().getName().toString() + ";",
						st.getMethod().getName().toString(), IInvokeInstruction.Dispatch.STATIC));
				if (st.getMethod().getReturnType() != TypeReference.Void) {
					editor.addInstructionAtEnd(InstructionFactory.makePop());
				}
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
	 * @param level level of parameter instantiation.
	 * @throws InstrumenterException if a parameter cannot be instantiated.
	 */
	private void instantiateParameters(final MethodWrapper editor, final IMethod method, final int level)
		throws InstrumenterException {
		for (int i = method.isStatic() ? 0 : 1; i < method.getNumberOfParameters(); i++) {
			TypeReference type = method.getParameterType(i);
			if (type.isPrimitiveType()) {
				if (type.getName().toString().equals(Constants.TYPE_char)) {
					editor.addInstructionAtEnd(ConstantInstruction.make('0'));
				} else if (type.getName().toString().equals(Constants.TYPE_boolean)) {
					editor.addInstructionAtEnd(ConstantInstruction.make(0));
				} else {
					editor.addInstructionAtEnd(ConstantInstruction.make(type.getName().toString(), 0));
				}
			} else {
				if (level == MAX_LEVEL) {
					editor.addInstructionAtEnd(ConstantInstruction.make(type.getName().toString() + ";", null));
				} else {
					IClass con = wrapper.getClassHierarchy().lookupClass(type);
					if (con.isArrayClass()) {
						editor.addInstructionAtEnd(ConstantInstruction.make(0));
						editor.addInstructionAtEnd(NewInstruction.make(con.getName().toString() + ";", 1));
						continue;
					}
					IMethod init = wrapper.findInit(con);
					Iterator<IClass> subtypes = wrapper.getSubtypes(con).iterator();
					if (init == null || subtypes.hasNext()) {
						while (subtypes.hasNext()) {
							IClass cl = subtypes.next();
							IMethod nextInit = wrapper.findInit(cl);
							if (nextInit != null && !wrapper.getSubtypes(cl).iterator().hasNext()) {
								con = cl;
								init = nextInit;
								break;
							}
						}
					}
					if (init == null) {
						editor.addInstructionAtEnd(ConstantInstruction.make(con.getName().toString() + ";", null));
						continue;
					}
					editor.addInstructionAtEnd(NewInstruction.make(con.getName().toString() + ";", 0));
					editor.addInstructionAtEnd(InstructionFactory.makeDup());
					instantiateParameters(editor, init, level + 1);
					editor.addInstructionAtEnd(InstructionFactory.makeInit(con.getName().toString() + ";",
							init.getDescriptor().toString()));
				}
			}
		}
	}

	/**
	 * Instruments the bytecode for the start phase.
	 *
	 * @param editor the editor for the start phase method.
	 * @throws InstrumenterException if the instrumentation fails.
	 */
	private void instrumentStartPhase(final MethodWrapper editor) throws InstrumenterException {
		NonDeterministicIfInstrumenter ifInstr = new NonDeterministicIfInstrumenter();
		Set<ExplicitDeclaration> declarations = wrapper.getFramework().getStartPhase().getDeclarations();
		int maxUsesIndex = editor.allocateLocalVariable(Constants.TYPE_double);
		editor.addInstructionAtEnd(ConstantInstruction.make((double) declarations.size()));
		editor.addInstructionAtEnd(StoreInstruction.make(Constants.TYPE_double, maxUsesIndex));
		ifInstr.instrumentBeginning(editor, maxUsesIndex);
		for (ExplicitDeclaration dec : declarations) {
			ifInstr.instrumentCaseBeginning(editor);
			instrumentExplicitDeclaration(editor, dec, -1, null);
		}
		ifInstr.instrumentEnd(editor);
		editor.instrumentMethod();
	}

	/**
	 * Instruments the bytecode for the end phase.
	 *
	 * @param editor the editor for the end phase method.
	 * @throws InstrumenterException if the instrumentation fails.
	 */
	private void instrumentEndPhase(final MethodWrapper editor) throws InstrumenterException {
		instrumentExplicitDeclaration(editor, wrapper.getFramework().getEndPhase().getEnd(), -1, null);
		editor.instrumentMethod();
	}

	/**
	 * Instruments the bytecode for the working phase.
	 *
	 * @param editor the editor for the working phase method.
	 */
	private void instrumentWorkingPhase(final MethodWrapper editor) {
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
		int forLabelCounter = 0;
		int procIndex = 0;
		int threadAIndex = 0;
		int loopIndex = 0;
		for (int i = 0; i < workingPhases.size(); i++) {
			WorkingPhase working = workingPhases.get(i);
			if (working.getThreadType() == ThreadType.SINGLE) {
				editor.addInstructionAtEnd(NewInstruction.make(AC_WW_BYTECODE_NAME, 0));
				editor.addInstructionAtEnd(InstructionFactory.makeDup());
				editor.addInstructionAtEnd(LoadInstruction.make(AC_BYTECODE_NAME, 0));
				editor.addInstructionAtEnd(InstructionFactory.makeDup());
				editor.addInstructionAtEnd(ConstantInstruction.make(i));
				editor.addInstructionAtEnd(InstructionFactory.makeInit(AC_WW_BYTECODE_NAME, "(" + AC_BYTECODE_NAME
						+ AC_BYTECODE_NAME + Constants.TYPE_int + ")" + Constants.TYPE_void));
				editor.addInstructionAtEnd(InvokeInstruction.make(DEFAULT_SIGNATURE, AC_WW_BYTECODE_NAME, "run",
						IInvokeInstruction.Dispatch.VIRTUAL));
			} else if (working.getThreadType() == ThreadType.MULTI) {
				if (procIndex == 0) {
					procIndex = editor.allocateLocalVariable(Constants.TYPE_int);
					threadAIndex = editor.allocateLocalVariable("[" + THREAD_BYTECODE_NAME);
					loopIndex = editor.allocateLocalVariable(Constants.TYPE_int);
					editor.addInstructionAtEnd(InvokeInstruction.make("()" + RUNTIME_BYTECODE_NAME,
							RUNTIME_BYTECODE_NAME, "getRuntime", IInvokeInstruction.Dispatch.STATIC));
					editor.addInstructionAtEnd(InvokeInstruction.make("()" + Constants.TYPE_int, RUNTIME_BYTECODE_NAME,
							"availableProcessors", IInvokeInstruction.Dispatch.VIRTUAL));
					editor.addInstructionAtEnd(StoreInstruction.make(Constants.TYPE_int, procIndex));
					editor.addInstructionAtEnd(LoadInstruction.make(Constants.TYPE_int, procIndex));
					editor.addInstructionAtEnd(NewInstruction.make("[" + THREAD_BYTECODE_NAME, 1));
					editor.addInstructionAtEnd(StoreInstruction.make("[" + THREAD_BYTECODE_NAME, threadAIndex));
				}
				editor.addInstructionAtEnd(ConstantInstruction.make(0));
				editor.addInstructionAtEnd(StoreInstruction.make(Constants.TYPE_int, loopIndex));

				// First loop: creation and start of threads.
				editor.addLabelAtEnd(allocatedLabels.get(forLabelCounter));
				editor.addInstructionsAtEnd(InstructionFactory.makeCompareIntegers(loopIndex, procIndex,
						IConditionalBranchInstruction.Operator.GE, allocatedLabels.get(forLabelCounter + 1)));
				editor.addInstructionAtEnd(LoadInstruction.make("[" + THREAD_BYTECODE_NAME, threadAIndex));
				editor.addInstructionAtEnd(LoadInstruction.make(Constants.TYPE_int, loopIndex));
				editor.addInstructionAtEnd(NewInstruction.make(THREAD_BYTECODE_NAME, 0));
				editor.addInstructionAtEnd(InstructionFactory.makeDup());
				editor.addInstructionAtEnd(NewInstruction.make(AC_WW_BYTECODE_NAME, 0));
				editor.addInstructionAtEnd(InstructionFactory.makeDup());
				editor.addInstructionAtEnd(LoadInstruction.make(AC_BYTECODE_NAME, 0));
				editor.addInstructionAtEnd(InstructionFactory.makeDup());
				editor.addInstructionAtEnd(ConstantInstruction.make(i));
				editor.addInstructionAtEnd(InstructionFactory.makeInit(AC_WW_BYTECODE_NAME, "(" + AC_BYTECODE_NAME
						+ AC_BYTECODE_NAME + Constants.TYPE_int + ")" + Constants.TYPE_void));
				editor.addInstructionAtEnd(InstructionFactory.makeInit(THREAD_BYTECODE_NAME, "(Ljava/lang/Runnable;)"
						+ Constants.TYPE_void));
				editor.addInstructionAtEnd(ArrayStoreInstruction.make("[" + THREAD_BYTECODE_NAME));
				editor.addInstructionsAtEnd(InstructionFactory.makeArrayElementLoad("[" + THREAD_BYTECODE_NAME,
						threadAIndex, loopIndex));
				editor.addInstructionAtEnd(InvokeInstruction.make(DEFAULT_SIGNATURE, THREAD_BYTECODE_NAME, "start",
						IInvokeInstruction.Dispatch.VIRTUAL));
				editor.addInstructionAtEnd(LoadInstruction.make(Constants.TYPE_int, loopIndex));
				editor.addInstructionAtEnd(ConstantInstruction.make(1));
				editor.addInstructionAtEnd(BinaryOpInstruction.make(Constants.TYPE_int,
						IBinaryOpInstruction.Operator.ADD));
				editor.addInstructionAtEnd(StoreInstruction.make(Constants.TYPE_int, loopIndex));
				editor.addInstructionAtEnd(InstructionFactory.makeGoto(allocatedLabels.get(forLabelCounter)));

				// Second loop: every thread will be joined.
				editor.addInstructionAtEnd(allocatedLabels.get(forLabelCounter + 1), ConstantInstruction.make(0));
				editor.addInstructionAtEnd(StoreInstruction.make(Constants.TYPE_int, loopIndex));
				editor.addLabelAtEnd(allocatedLabels.get(forLabelCounter + 2));
				editor.addInstructionsAtEnd(InstructionFactory.makeCompareIntegers(loopIndex, procIndex,
						IConditionalBranchInstruction.Operator.GE, allocatedLabels.get(forLabelCounter + 3)));
				editor.addInstructionsAtEnd(InstructionFactory.makeArrayElementLoad("[" + THREAD_BYTECODE_NAME,
						threadAIndex, loopIndex));
				ExceptionHandler h = new ExceptionHandler(allocatedLabels.get(forLabelCounter + 4),
						"Ljava/lang/InterruptedException;");
				editor.addInstructionAtEnd(InvokeInstruction.make(DEFAULT_SIGNATURE, THREAD_BYTECODE_NAME, "join",
						IInvokeInstruction.Dispatch.VIRTUAL), new ExceptionHandler[] {h});
				editor.addInstructionsAtEnd(InstructionFactory.makeIncrement(loopIndex));
				editor.addInstructionAtEnd(InstructionFactory.makeGoto(allocatedLabels.get(forLabelCounter + 2)));

				// Exception handling of InterruptedException that can occur during join().
				editor.addInstructionAtEnd(allocatedLabels.get(forLabelCounter + 4), InstructionFactory.makePop());
				editor.addInstructionAtEnd(InstructionFactory.makeGoto(allocatedLabels.get(forLabelCounter + 2)));

				// End of loops.
				editor.addLabelAtEnd(allocatedLabels.get(forLabelCounter + 3));
				forLabelCounter += 5;
			}
		}
		editor.instrumentMethod();
	}

	/**
	 * Instruments a new ArtificialClass method with the actual content of a working phase.
	 *
	 * @param editor the edtior for bytecode instrumentation.
	 * @param working the working phase.
	 * @throws InstrumenterException if the instrumentation fails.
	 */
	private void instrumentActualWorkingPhaseContent(final MethodWrapper editor, final WorkingPhase working) throws
		InstrumenterException {
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
		int counterIndex = editor.allocateLocalVariable(Constants.TYPE_double);
		editor.addInstructionAtEnd(ConstantInstruction.make((double) caseCounter));
		editor.addInstructionAtEnd(StoreInstruction.make(Constants.TYPE_double, counterIndex));
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
							editor.addInstructionAtEnd(ConstantInstruction.make(index));
							editor.addInstructionAtEnd(InvokeInstruction.make("(" + Constants.TYPE_int + ")"
									+ Constants.TYPE_Object, LIST_BYTECODE_NAME, "get",
									IInvokeInstruction.Dispatch.INTERFACE), handlers);
							editor.addInstructionAtEnd(CheckCastInstruction.make(cl.getName().toString() + ";"));
							instantiateParameters(editor, m, 1);
							editor.addInstructionAtEnd(InvokeInstruction.make(m.getDescriptor().toString(),
									cl.getName().toString() + ";",
									m.getName().toString(), cl.isInterface()
									? IInvokeInstruction.Dispatch.INTERFACE
											: IInvokeInstruction.Dispatch.VIRTUAL));
							if (m.getReturnType() != TypeReference.Void) {
								editor.addInstructionAtEnd(InstructionFactory.makePop());
							}
						}
						editor.addInstructionAtEnd(InstructionFactory.makeGoto(afterExLabel));
						editor.addInstructionAtEnd(exLabel, InstructionFactory.makePop());
						editor.addLabelAtEnd(afterExLabel);
					}
				}
			} else if (r.getClass() == Block.class) {
				instrumentBlock(editor, ifInstr, (Block) r);
			}
		}
		ifInstr.instrumentEnd(editor);
		loop.instrumentLoopEnd(editor);
		editor.instrumentMethod();
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
	 * @throws InstrumenterException if the instrumentation fails.
	 */
	private void instrumentBlock(final MethodWrapper editor, final NonDeterministicIfInstrumenter ifInstr,
			final Block b) throws InstrumenterException {
		if (b.getInnerBlock() == null) {
			for (int i = 0; i < wrapper.getInstancesCount(b.getIClass()); i++) {
				ifInstr.instrumentCaseBeginning(editor);
				instrumentForGettingInstanceCollection(editor, b.getIClass());
				int index = i;
				int instanceIndex = editor.allocateLocalVariable(b.getClassName() + ";");
				editor.addInstructionAtEnd(ConstantInstruction.make(index));
				editor.addInstructionAtEnd(InvokeInstruction.make("(" + Constants.TYPE_int + ")"
						+ Constants.TYPE_Object, LIST_BYTECODE_NAME, "get", IInvokeInstruction.Dispatch.INTERFACE));
				editor.addInstructionAtEnd(CheckCastInstruction.make(b.getClassName() + ";"));
				editor.addInstructionAtEnd(StoreInstruction.make(b.getClassName() + ";", instanceIndex));
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
	private void instrumentRunnableClassForWorkingPhase(final MethodWrapper editor) {
		int cases = wrapper.getFramework().getWorkingPhases().size();
		ArrayList<Integer> allocatedLabels = new ArrayList<>();
		for (int i = 0; i <= cases; i++) {
			allocatedLabels.add(editor.allocateLabel());
		}
		for (int i = 0; i < cases; i++) {
			editor.addInstructionAtEnd(allocatedLabels.get(i), LoadInstruction.make(AC_WW_BYTECODE_NAME, 0));
			editor.addInstructionAtEnd(GetInstruction.make(Constants.TYPE_int, AC_WW_BYTECODE_NAME, "phaseNumber",
					false));
			editor.addInstructionAtEnd(ConstantInstruction.make(i));
			editor.addInstructionAtEnd(ConditionalBranchInstruction.make(Constants.TYPE_int,
					IConditionalBranchInstruction.Operator.NE, allocatedLabels.get(i + 1)));
			editor.addInstructionAtEnd(LoadInstruction.make(AC_WW_BYTECODE_NAME, 0));
			editor.addInstructionAtEnd(GetInstruction.make(AC_BYTECODE_NAME, AC_WW_BYTECODE_NAME, "outerInstance",
					false));
			editor.addInstructionAtEnd(InvokeInstruction.make(DEFAULT_SIGNATURE, AC_BYTECODE_NAME, "w" + i,
					IInvokeInstruction.Dispatch.VIRTUAL));
		}
		editor.addLabelAtEnd(allocatedLabels.get(cases));
		editor.instrumentMethod();
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
		private void instrumentBeginning(final MethodWrapper editor, final int maxCasesIndex) {
			randomIndex = editor.allocateLocalVariable(Constants.TYPE_int);
			allocatedCaseLabels = new ArrayList<>();
			caseCounter = 0;
			editor.addInstructionAtEnd(InvokeInstruction.make("()" + Constants.TYPE_double, "Ljava/lang/Math;",
					"random", IInvokeInstruction.Dispatch.STATIC));
			editor.addInstructionAtEnd(LoadInstruction.make(Constants.TYPE_double, maxCasesIndex));
			editor.addInstructionAtEnd(BinaryOpInstruction.make(Constants.TYPE_double,
					IBinaryOpInstruction.Operator.MUL));
			editor.addInstructionAtEnd(ConversionInstruction.make(Constants.TYPE_double, Constants.TYPE_int));
			editor.addInstructionAtEnd(StoreInstruction.make(Constants.TYPE_int, randomIndex));
		}

		/**
		 * Instruments bytecode with the beginning of a case. Must be called before the actual content of the case is
		 * added.
		 *
		 * @param editor the editor for bytecode instrumentation.
		 */
		private void instrumentCaseBeginning(final MethodWrapper editor) {
			if (caseCounter == 0) {
				allocatedCaseLabels.add(editor.allocateLabel());
			}
			allocatedCaseLabels.add(editor.allocateLabel());
			editor.addInstructionAtEnd(allocatedCaseLabels.get(caseCounter),
					LoadInstruction.make(Constants.TYPE_int, randomIndex));
			editor.addInstructionAtEnd(ConstantInstruction.make(caseCounter));
			editor.addInstructionAtEnd(ConditionalBranchInstruction.make(Constants.TYPE_int,
					IConditionalBranchInstruction.Operator.NE, allocatedCaseLabels.get(caseCounter + 1)));
			caseCounter++;
		}

		/**
		 * Instruments bytecode with the end of the clause.
		 *
		 * @param editor the editor for bytecode instrumentation.
		 */
		private void instrumentEnd(final MethodWrapper editor) {
			if (caseCounter != 0) {
				editor.addLabelAtEnd(allocatedCaseLabels.get(caseCounter));
			}
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
		private void instrumentLoopBeginning(final MethodWrapper editor) {
			loopIndex = editor.allocateLocalVariable(Constants.TYPE_int);
			int randomIndex = editor.allocateLocalVariable(Constants.TYPE_int);
			beginLabel = editor.allocateLabel();
			endLabel = editor.allocateLabel();
			editor.addInstructionAtEnd(InvokeInstruction.make("()" + Constants.TYPE_double, "Ljava/lang/Math;",
					"random", IInvokeInstruction.Dispatch.STATIC));
			final double maxLoops = 1000000.0;
			editor.addInstructionAtEnd(ConstantInstruction.make(maxLoops));
			editor.addInstructionAtEnd(BinaryOpInstruction.make(Constants.TYPE_double,
					IBinaryOpInstruction.Operator.MUL));
			editor.addInstructionAtEnd(ConversionInstruction.make(Constants.TYPE_double, Constants.TYPE_int));
			editor.addInstructionAtEnd(StoreInstruction.make(Constants.TYPE_int, randomIndex));
			editor.addInstructionAtEnd(ConstantInstruction.make(0));
			editor.addInstructionAtEnd(StoreInstruction.make(Constants.TYPE_int, loopIndex));
			editor.addLabelAtEnd(beginLabel);
			editor.addInstructionsAtEnd(InstructionFactory.makeCompareIntegers(loopIndex, randomIndex,
					IConditionalBranchInstruction.Operator.GE, endLabel));
		}

		/**
		 * Instruments the bytecode with th end of the loop.
		 *
		 * @param editor the editor for bytecode instrumentation.
		 */
		private void instrumentLoopEnd(final MethodWrapper editor) {
			editor.addInstructionsAtEnd(InstructionFactory.makeIncrement(loopIndex));
			editor.addInstructionAtEnd(InstructionFactory.makeGoto(beginLabel));
			editor.addLabelAtEnd(endLabel);
		}
	}
}
