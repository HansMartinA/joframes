package edu.kit.ipd.pp.joframes.api;

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.util.config.AnalysisScopeReader;
import edu.kit.ipd.pp.joframes.ast.base.Framework;
import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

/**
 * Analyzes the framework and application class hierarchy considering the parsed framework specification.
 * 
 * @author Martin Armbruster
 */
class ClassHierarchyAnalyzer {
	/**
	 * Analyzes the class hierarchy of the framework and application.
	 * 
	 * @param framework the framework specification that will be modified.
	 * @param frameworkJars list of all jar files containing the framework classes. Can be null.
	 * @param applicationJars list of all jar files containing the application classes.
	 * @throws ClassHierarchyCreationException when the creation of the class hierarchy fails.
	 */
	void analyzeClassHierarchy(Framework framework, String[] frameworkJars, String[] applicationJars) throws
		ClassHierarchyCreationException {
		ClassHierarchy hierarchy = makeClassHierarchy(frameworkJars, applicationJars);
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
}
