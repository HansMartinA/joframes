package edu.kit.ipd.pp.joframes.api;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.Selector;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.config.AnalysisScopeReader;
import edu.kit.ipd.pp.joframes.api.exceptions.ClassHierarchyCreationException;
import edu.kit.ipd.pp.joframes.ast.acha.MethodCollector;
import edu.kit.ipd.pp.joframes.ast.ap.Block;
import edu.kit.ipd.pp.joframes.ast.ap.Regex;
import edu.kit.ipd.pp.joframes.ast.ap.Supertype;
import edu.kit.ipd.pp.joframes.ast.base.AstBaseClass;
import edu.kit.ipd.pp.joframes.ast.base.ExplicitDeclaration;
import edu.kit.ipd.pp.joframes.ast.base.Framework;
import edu.kit.ipd.pp.joframes.ast.base.Method;
import edu.kit.ipd.pp.joframes.ast.base.Rule;
import edu.kit.ipd.pp.joframes.ast.base.StaticMethod;
import edu.kit.ipd.pp.joframes.ast.base.WorkingPhase;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.jar.JarFile;

/**
 * Analyzes the framework and application class hierarchy considering the parsed framework specification.
 *
 * @author Martin Armbruster
 */
class ClassHierarchyAnalyzer {
	/**
	 * Framework name for Swing.
	 */
	private static final String SWING = "Swing";
	/**
	 * Classes belonging to AWT include this string in their package name.
	 */
	private static final String AWT = "awt";
	/**
	 * Framework name for JavaFX.
	 */
	private static final String JAVAFX = "JavaFX";
	/**
	 * Method name within the framework specification to identify constructors.
	 */
	private static final String CONSTRUCTOR = "Constructor";
	/**
	 * Stores the wrapper of the framework.
	 */
	private FrameworkWrapper wrapper;
	/**
	 * Stores the extension class loader.
	 */
	private ClassLoaderReference extensionLoader;
	/**
	 * Stores the application class loader.
	 */
	private ClassLoaderReference applicationLoader;
	/**
	 * Stores the name of the class containing the main method.
	 */
	private String mainClassName;

	/**
	 * Analyzes the class hierarchy of the framework and application.
	 *
	 * @param framework the framework specification that will be modified.
	 * @param frameworkJars list of all jar files containing the framework classes. Can be null.
	 * @param applicationJars list of all jar files containing the application classes.
	 * @return the modified framework with additional information in a FrameworkWrapper.
	 * @throws ClassHierarchyCreationException when the creation of the class hierarchy fails.
	 */
	FrameworkWrapper analyzeClassHierarchy(final Framework framework, final String[] frameworkJars,
			final String[] applicationJars) throws ClassHierarchyCreationException {
		return analyzeClassHierarchy(framework, frameworkJars, applicationJars, null);
	}

	/**
	 * Analyzes the class hierarchy of the framework and application.
	 *
	 * @param framework the framework specification that will be modified.
	 * @param frameworkJars list of all jar files containing the framework classes. Can be null.
	 * @param applicationJars list of all jar files containing the application classes.
	 * @param mainClass name of the class containing the main method.
	 * @return the modified framework with additional information in a FrameworkWrapper.
	 * @throws ClassHierarchyCreationException when the creation of the class hierarchy fails.
	 */
	FrameworkWrapper analyzeClassHierarchy(final Framework framework, final String[] frameworkJars,
			final String[] applicationJars, final String mainClass) throws ClassHierarchyCreationException {
		this.mainClassName = mainClass;
		wrapper = new FrameworkWrapper(framework);
		ClassHierarchy hierarchy = makeClassHierarchy(frameworkJars, applicationJars);
		wrapper.setClassHierarchy(hierarchy);
		extensionLoader = hierarchy.getScope().getExtensionLoader();
		applicationLoader = hierarchy.getScope().getApplicationLoader();
		analyzeFramework(hierarchy);
		return wrapper;
	}

	/**
	 * Creates the class hierarchy for the framework and application.
	 *
	 * @param frameworkJars list of all jar files containing the framework classes. Can be null.
	 * @param applicationJars list of all jar files containing the application classes.
	 * @return the created class hierarchy.
	 * @throws ClassHierarchyCreationException when the creation of the class hierarchy fails.
	 */
	private ClassHierarchy makeClassHierarchy(final String[] frameworkJars, final String[] applicationJars) throws
		ClassHierarchyCreationException {
		try {
			AnalysisScope scope = AnalysisScopeReader.makePrimordialScope(new File("cha-exclusions.txt"));
			if (!wrapper.getFramework().getName().equals(SWING) && !wrapper.getFramework().getName().equals(JAVAFX)) {
				if (frameworkJars.length == 0) {
					throw new ClassHierarchyCreationException("There are no framework jar files.");
				}
				for (String fwJar : frameworkJars) {
					if (!new File(fwJar).exists()) {
						throw new ClassHierarchyCreationException("The given framework jar file (" + fwJar
								+ ") does not exist.");
					}
					scope.addToScope(scope.getExtensionLoader(), new JarFile(fwJar));
				}
			}
			if (applicationJars.length == 0) {
				throw new ClassHierarchyCreationException("There are no application jar files.");
			}
			for (String appJar : applicationJars) {
				if (!new File(appJar).exists()) {
					throw new ClassHierarchyCreationException("The given application jar file (" + appJar
							+ ") does not exist.");
				}
				scope.addToScope(scope.getApplicationLoader(), new JarFile(appJar));
			}
			return ClassHierarchyFactory.make(scope);
		} catch (IOException e) {
			throw new ClassHierarchyCreationException("An IO exception occurred while creating the class hierarchy.",
					e);
		} catch (ClassHierarchyException e) {
			throw new ClassHierarchyCreationException("The class hierarchy could not be created.", e);
		}
	}

	/**
	 * Analyzes the framework and its classes.
	 *
	 * @param hierarchy the class hierarchy.
	 */
	private void analyzeFramework(final ClassHierarchy hierarchy) {
		for (ExplicitDeclaration declaration : wrapper.getFramework().getStartPhase().getDeclarations()) {
			analyzeForExplicitDeclaration(hierarchy, declaration, null);
		}
		analyzeForExplicitDeclaration(hierarchy, wrapper.getFramework().getEndPhase().getEnd(), null);
		for (WorkingPhase working : wrapper.getFramework().getWorkingPhases()) {
			analyzeForWorkingPhase(hierarchy, working);
		}
	}

	/**
	 * Analyzes the class hierarchy for an explicit declaration.
	 *
	 * @param hierarchy the class hierarchy.
	 * @param declaration the explicit declaration.
	 * @param boundClass the class to which the explicit declaration is bound. Can be null.
	 */
	private void analyzeForExplicitDeclaration(final ClassHierarchy hierarchy, final ExplicitDeclaration declaration,
			final IClass boundClass) {
		IClass actualBoundClass = boundClass;
		if (declaration.getClassName() != null) {
			actualBoundClass = hierarchy.lookupClass(TypeReference.findOrCreate(extensionLoader,
					declaration.getClassName()));
			declaration.setIClass(actualBoundClass);
			wrapper.addFrameworkClass(actualBoundClass);
		}
		for (int i = 0; i < declaration.getNumberOfCallsAndDeclarations(); i++) {
			AstBaseClass abc = declaration.getCallOrDeclaration(i);
			if (abc.getClass() == ExplicitDeclaration.class) {
				analyzeForExplicitDeclaration(hierarchy, (ExplicitDeclaration) abc, actualBoundClass);
			} else if (abc.getClass() == Method.class) {
				Method method = (Method) abc;
				IMethod m = null;
				if (method.getSignature().equals(CONSTRUCTOR)) {
					if (actualBoundClass.isInterface()) {
						for (IClass cl : hierarchy.getImplementors(actualBoundClass.getReference())) {
							if (checkSubclassForExplicitDeclaration(cl)) {
								declaration.addApplicationClass(cl, findInit(cl));
							}
						}
					} else {
						ArrayDeque<IClass> subclasses = new ArrayDeque<>();
						subclasses.addAll(hierarchy.getImmediateSubclasses(actualBoundClass));
						while (!subclasses.isEmpty()) {
							IClass cl = subclasses.poll();
							subclasses.addAll(hierarchy.getImmediateSubclasses(cl));
							if (checkSubclassForExplicitDeclaration(cl)) {
								declaration.addApplicationClass(cl, findInit(cl));
							}
						}
					}
				} else {
					m = actualBoundClass.getMethod(Selector.make(method.getSignature()));
				}
				method.setMethod(m);
			} else if (abc.getClass() == StaticMethod.class) {
				StaticMethod stMethod = (StaticMethod) abc;
				if (stMethod.getClassString() == null
						&& stMethod.getSignature().equals(FrameworkSpecificationParser.MAIN_SIGNATURE)) {
					if (mainClassName == null) {
						stMethod.setIClass(findNextMainClass(hierarchy));
					} else {
						stMethod.setIClass(hierarchy.lookupClass(
								TypeReference.findOrCreate(applicationLoader, mainClassName)));
					}
				} else {
					stMethod.setIClass(hierarchy.lookupClass(TypeReference.findOrCreate(
							extensionLoader, stMethod.getClassString())));
				}
				stMethod.setMethod(stMethod.getIClass().getMethod(Selector.make(stMethod.getSignature())));
			}
		}
	}

	/**
	 * Checks if a subclass of a framework class in an explicit declaration is an concrete application class.
	 *
	 * @param cl the subclass.
	 * @return true if the subclass is a concrete application class. false otherwise.
	 */
	private boolean checkSubclassForExplicitDeclaration(final IClass cl) {
		return cl.getClassLoader().getReference() == applicationLoader
				&& !(cl.isAbstract() || cl.isInterface() || cl.isPrivate());
	}

	/**
	 * Searches a class for a constructor.
	 *
	 * @param cl the class.
	 * @return the constructor of null if no can be found.
	 */
	private IMethod findInit(final IClass cl) {
		IMethod m = cl.getMethod(Selector.make("<init>()V"));
		if (m == null) {
			for (IMethod possibleInitMethod : cl.getDeclaredMethods()) {
				if (possibleInitMethod.isInit()) {
					m = possibleInitMethod;
					break;
				}
			}
		}
		return m;
	}

	/**
	 * When a call to a main method is given without a class, this method looks for the next class containing a main
	 * method. First, lookups in application classes are performed. Afterwards, there are lookups in framework classes
	 * and last in all classes.
	 *
	 * @param hierarchy the class hierarchy for analysis.
	 * @return the first found class with a main method or null if no class can be found.
	 */
	private IClass findNextMainClass(final ClassHierarchy hierarchy) {
		for (IClass cl : hierarchy) {
			if (cl.getClassLoader().getReference() == applicationLoader && checkClassForMain(cl)) {
				return cl;
			}
		}
		for (IClass cl : hierarchy) {
			if (isClassInFramework(cl) && checkClassForMain(cl)) {
				return cl;
			}
		}
		for (IClass cl : hierarchy) {
			if (checkClassForMain(cl)) {
				return cl;
			}
		}
		return null;
	}

	/**
	 * Checks if a class contains a main method.
	 *
	 * @param cl the class to check.
	 * @return true if the class contains a main method. false otherwise.
	 */
	private boolean checkClassForMain(final IClass cl) {
		IMethod possibleMain = cl.getMethod(Selector.make(FrameworkSpecificationParser.MAIN_SIGNATURE));
		return possibleMain != null && possibleMain.isStatic() && possibleMain.isPublic();
	}

	/**
	 * Analyzes the class hierarchy for a working phase.
	 *
	 * @param hierarchy the class hierarchy.
	 * @param working the working phase.
	 */
	private void analyzeForWorkingPhase(final ClassHierarchy hierarchy, final WorkingPhase working) {
		MethodCollector methods = new MethodCollector();
		for (Rule r : working.getRules()) {
			if (r.getClass() == Regex.class) {
				Regex rr = (Regex) r;
				analyzeForRegexRule(hierarchy, rr.getRegularExpression(), methods);
				working.removeRule(r);
			} else if (r.getClass() == Supertype.class) {
				Supertype st = (Supertype) r;
				IClass type = hierarchy.lookupClass(TypeReference.findOrCreate(extensionLoader, st.getSuperType()));
				analyzeForSupertypeRule(hierarchy, type, methods);
				working.removeRule(r);
			} else if (r.getClass() == Block.class) {
				analyzeForBlock(hierarchy, (Block) r);
			}
		}
		working.addRule(methods);
	}

	/**
	 * Analyzes the class hierarchy for a block rule.
	 *
	 * @param hierarchy the class hierarchy.
	 * @param block the block rule.
	 */
	private void analyzeForBlock(final ClassHierarchy hierarchy, final Block block) {
		IClass blockClass = hierarchy.lookupClass(TypeReference.findOrCreate(extensionLoader,
				block.getClassName()));
		block.setIClass(blockClass);
		wrapper.addFrameworkClass(blockClass);
		if (block.getInnerBlock() == null) {
			analyzeForExplicitDeclaration(hierarchy, block.getDeclaration(), blockClass);
		} else {
			analyzeForBlock(hierarchy, block.getInnerBlock());
		}
	}

	/**
	 * Analyzes the class hierarchy, to be specific framework classes and their methods, with a regular expression from
	 * a regex rule and adds matching methods to a method collection.
	 *
	 * @param hierarchy the class hierarchy.
	 * @param regex the regular expression which is matched against method names.
	 * @param collector the method collection.
	 */
	private void analyzeForRegexRule(final ClassHierarchy hierarchy, final String regex,
			final MethodCollector collector) {
		Iterator<IClass> iterator = hierarchy.iterator();
		while (iterator.hasNext()) {
			IClass type = iterator.next();
			if (isClassInFramework(type)) {
				for (IMethod method : type.getDeclaredMethods()) {
					if (method.getName().toString().matches(regex) && checkMethodForObjectMethod(method)) {
						collector.addMethod(method);
						wrapper.addFrameworkClass(method.getDeclaringClass());
					}
				}
			}
		}
	}

	/**
	 * Analyzes the class hierarchy for the supertype rule.
	 *
	 * @param hierarchy the class hierarchy.
	 * @param supertype the supertype.
	 * @param collector the method collection.
	 */
	private void analyzeForSupertypeRule(final ClassHierarchy hierarchy, final IClass supertype,
			final MethodCollector collector) {
		collector.addAllMethods(supertype.getDeclaredMethods());
		wrapper.addFrameworkClass(supertype);
		if (supertype.isInterface()) {
			for (IClass cl : hierarchy.getImplementors(supertype.getReference())) {
				if (isClassInFramework(cl)) {
					for (IMethod m : cl.getDeclaredMethods()) {
						if (checkMethodForObjectMethod(m)) {
							collector.addMethod(m);
						}
					}
					wrapper.addFrameworkClass(cl);
				}
			}
		} else {
			ArrayDeque<IClass> types = new ArrayDeque<>();
			types.add(supertype);
			while (!types.isEmpty()) {
				IClass type = types.poll();
				types.addAll(hierarchy.getImmediateSubclasses(type));
				if (isClassInFramework(type)) {
					for (IMethod m : type.getDeclaredMethods()) {
						if (checkMethodForObjectMethod(m)) {
							collector.addMethod(m);
						}
					}
					wrapper.addFrameworkClass(type);
				}
			}
		}
	}

	/**
	 * Checks if a method is a callable object method.
	 *
	 * @param m the method
	 * @return true if the method is callable. false otherwise.
	 */
	private boolean checkMethodForObjectMethod(final IMethod m) {
		return !(m.isClinit() || m.isInit() || m.isPrivate() || m.isStatic());
	}

	/**
	 * Checks if a class belongs to the framework. Usually, the extension loader is used to load framework classes.
	 * In case of Swing or JavaFX, the primordial loader loads the classes so that the classes are identified based
	 * on their package name.
	 *
	 * @param cl the class that will be checked.
	 * @return true if the class belongs to the framework. false otherwise.
	 */
	private boolean isClassInFramework(final IClass cl) {
		if (cl.getClassLoader().getReference() == applicationLoader) {
			return false;
		}
		String packageName = cl.getName().getPackage().toString();
		if (wrapper.getFramework().getName().equals(SWING)) {
			return packageName.contains(SWING.toLowerCase()) || packageName.contains(AWT);
		} else if (wrapper.getFramework().getName().equals(JAVAFX)) {
			return packageName.contains(JAVAFX.toLowerCase());
		} else {
			return cl.getClassLoader().getReference() == extensionLoader;
		}
	}
}
