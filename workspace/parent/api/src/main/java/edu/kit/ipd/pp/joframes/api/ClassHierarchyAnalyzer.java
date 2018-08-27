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
	private final static String SWING = "Swing";
	/**
	 * Classes belonging to AWT include this string in their package name.
	 */
	private final static String AWT = "awt";
	/**
	 * Framework name for JavaFX.
	 */
	private final static String JAVAFX = "JavaFX";
	/**
	 * Method name within the framework specification to identify constructors. 
	 */
	private final static String CONSTRUCTOR = "Constructor";
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
	 * Analyzes the class hierarchy of the framework and application.
	 * 
	 * @param framework the framework specification that will be modified.
	 * @param frameworkJars list of all jar files containing the framework classes. Can be null.
	 * @param applicationJars list of all jar files containing the application classes.
	 * @throws ClassHierarchyCreationException when the creation of the class hierarchy fails.
	 */
	FrameworkWrapper analyzeClassHierarchy(Framework framework, String[] frameworkJars, String[] applicationJars) throws
		ClassHierarchyCreationException {
		wrapper = new FrameworkWrapper(framework);
		ClassHierarchy hierarchy = makeClassHierarchy(frameworkJars, applicationJars);
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
	private ClassHierarchy makeClassHierarchy(String[] frameworkJars, String[] applicationJars) throws
		ClassHierarchyCreationException {
		try {
			AnalysisScope scope = AnalysisScopeReader.makePrimordialScope(new File("cha-exclusions.txt"));
			if(frameworkJars!=null) {
				for(String fwJar : frameworkJars) {
					scope.addToScope(scope.getExtensionLoader(), new JarFile(fwJar));
				}
			}
			for(String appJar : applicationJars) {
				scope.addToScope(scope.getApplicationLoader(), new JarFile(appJar));
			}
			return ClassHierarchyFactory.make(scope);
		} catch(IOException e) {
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
	private void analyzeFramework(ClassHierarchy hierarchy) {
		for(ExplicitDeclaration declaration : wrapper.getFramework().getStartPhase().getDeclarations()) {
			analyzeForExplicitDeclaration(hierarchy, declaration, null);
		}
		analyzeForExplicitDeclaration(hierarchy, wrapper.getFramework().getEndPhase().getEnd(), null);
		for(WorkingPhase working : wrapper.getFramework().getWorkingPhases()) {
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
	private void analyzeForExplicitDeclaration(ClassHierarchy hierarchy, ExplicitDeclaration declaration,
			IClass boundClass) {
		if(declaration.getClassName()!=null) {
			boundClass = hierarchy.lookupClass(TypeReference.findOrCreate(extensionLoader,
					declaration.getClassName()));
			declaration.setIClass(boundClass);
			wrapper.addFrameworkClass(boundClass);
		}
		for(int i=0; i<declaration.getNumberOfCallsAndDeclarations(); i++) {
			AstBaseClass abc = declaration.getCallOrDeclaration(i);
			if(abc.getClass()==ExplicitDeclaration.class) {
				analyzeForExplicitDeclaration(hierarchy, (ExplicitDeclaration)abc, boundClass);
			} else if(abc.getClass()==Method.class) {
				Method method = (Method)abc;
				IMethod m = null;
				if(method.getSignature().equals(CONSTRUCTOR)) {
					m = boundClass.getMethod(Selector.make("<init>()V"));
				} else {
					boundClass.getMethod(Selector.make(method.getSignature()));
				}
				method.setMethod(m);
			} else if(abc.getClass()==StaticMethod.class) {
				StaticMethod stMethod = (StaticMethod)abc;
				stMethod.setIClass(hierarchy.lookupClass(TypeReference.findOrCreate(
						extensionLoader, stMethod.getClassString())));
				stMethod.setMethod(stMethod.getIClass().getMethod(Selector.make(stMethod.getSignature())));
			}
		}
	}
	
	/**
	 * Analyzes the class hierarchy for a working phase. 
	 * 
	 * @param hierarchy the class hierarchy.
	 * @param working the working phase.
	 */
	private void analyzeForWorkingPhase(ClassHierarchy hierarchy, WorkingPhase working) {
		MethodCollector methods = new MethodCollector();
		for(Rule r : working.getRules()) {
			if(r.getClass()==Regex.class) {
				Regex rr = (Regex)r;
				analyzeWithRegex(hierarchy, hierarchy.getRootClass(), rr.getRegularExpression(), methods);
				working.removeRule(r);
			} else if(r.getClass()==Supertype.class) {
				Supertype st = (Supertype)r;
				IClass type = hierarchy.lookupClass(TypeReference.findOrCreate(extensionLoader, st.getSuperType()));
				methods.addAllMethods(type.getDeclaredMethods());
				analyzeWithRegex(hierarchy, type, ".*", methods);
				working.removeRule(r);
			} else if(r.getClass()==Block.class) {
				analyzeForBlock(hierarchy, (Block)r);
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
	private void analyzeForBlock(ClassHierarchy hierarchy, Block block) {
		IClass blockClass = hierarchy.lookupClass(TypeReference.findOrCreate(extensionLoader,
				block.getClassName()));
		block.setIClass(blockClass);
		wrapper.addFrameworkClass(blockClass);
		if(block.getInnerBlock()==null) {
			analyzeForExplicitDeclaration(hierarchy, block.getDeclaration(), blockClass);
		} else {
			analyzeForBlock(hierarchy, block.getInnerBlock());
		}
	}
	
	/**
	 * Analyzes the class hierarchy, to be specific framework classes and their methods, with a regular expression
	 * and adds matching methods to a method collection.
	 * 
	 * @param hierarchy the class hierarchy.
	 * @param root the root class with which the analysis starts. Must not be out of the framework.
	 * @param regex the regular expression which is matched against method names.
	 * @param collector the method collection.
	 */
	private void analyzeWithRegex(ClassHierarchy hierarchy, IClass root, String regex, MethodCollector collector) {
		ArrayDeque<IClass> types = new ArrayDeque<>();
		types.add(root);
		while(!types.isEmpty()) {
			IClass type = types.poll();
			types.addAll(hierarchy.getImmediateSubclasses(type));
			if(isClassInFramework(type)) {
				for(IMethod method : type.getDeclaredMethods()) {
					if(method.getName().toString().matches(regex)) {
						collector.addMethod(method);
						wrapper.addFrameworkClass(method.getDeclaringClass());
					}
				}
			}
		}
	}
	
	/**
	 * Checks if a class belongs to the framework. Usually, the extension loader is used to load framework classes.
	 * In case of Swing or JavaFX, the primordial loader loads the classes so that the classes are identified based
	 * on their package name. 
	 * 
	 * @param cl the class that will be checked.
	 * @return true if the class belongs to the framework. false otherwise.
	 */
	private boolean isClassInFramework(IClass cl) {
		if(cl.getClassLoader().getReference()==applicationLoader) {
			return false;
		}
		String packageName = cl.getName().getPackage().toString();
		if(wrapper.getFramework().getName().equals(SWING)) {
			return packageName.contains(SWING.toLowerCase()) || packageName.contains(AWT);
		} else if(wrapper.getFramework().getName().equals(JAVAFX)) {
			return packageName.contains(JAVAFX.toLowerCase());
		} else {
			return cl.getClassLoader().getReference()==extensionLoader;
		}
	}
}
