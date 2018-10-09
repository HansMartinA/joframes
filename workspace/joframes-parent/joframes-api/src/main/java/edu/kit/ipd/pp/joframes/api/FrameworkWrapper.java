package edu.kit.ipd.pp.joframes.api;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.Selector;
import com.ibm.wala.types.TypeReference;
import edu.kit.ipd.pp.joframes.ast.base.Framework;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Wraps a framework with additional information for exchange between different stages within the pipeline.
 *
 * @author Martin Armbruster
 */
class FrameworkWrapper {
	/**
	 * Stores the actual framework.
	 */
	private Framework framework;
	/**
	 * Stores the class hierarchy associated with the framework and application.
	 */
	private ClassHierarchy hierarchy;
	/**
	 * Stores the framework classes found during the class hierarchy analysis with a counter of found and created
	 * instances during bytecode instrumentation.
	 */
	private HashMap<IClass, Integer> frameworkClassesToInstancesCount;

	/**
	 * Creates a new instance.
	 *
	 * @param wrapFramework the wrapped framework.
	 */
	FrameworkWrapper(final Framework wrapFramework) {
		this.framework = wrapFramework;
		frameworkClassesToInstancesCount = new HashMap<>();
	}

	/**
	 * Returns the wrapped framework.
	 *
	 * @return the wrapped framework.
	 */
	Framework getFramework() {
		return framework;
	}

	/**
	 * Sets the class hierarchy to wrap.
	 *
	 * @param wrapHierarchy the class hierarchy.
	 */
	void setClassHierarchy(final ClassHierarchy wrapHierarchy) {
		this.hierarchy = wrapHierarchy;
	}

	/**
	 * Returns the wrapped class hierarchy.
	 *
	 * @return the class hierarchy.
	 */
	ClassHierarchy getClassHierarchy() {
		return hierarchy;
	}

	/**
	 * Adds a framework class.
	 *
	 * @param frameworkClass the framework class.
	 */
	void addFrameworkClass(final IClass frameworkClass) {
		frameworkClassesToInstancesCount.put(frameworkClass, 0);
	}

	/**
	 * Returns the framework classes in form of WALA classes.
	 *
	 * @return the set with the framework classes.
	 */
	Set<IClass> getFrameworkClasses() {
		return frameworkClassesToInstancesCount.keySet();
	}

	/**
	 * Counts one instance for a class. For every registered supertype of someClass, the instance is counted towards
	 * the supertype.
	 *
	 * @param someClass the class.
	 */
	void countOneInstance(final IClass someClass) {
		for (IClass cl : frameworkClassesToInstancesCount.keySet()) {
			if (hierarchy.isSubclassOf(someClass, cl) || hierarchy.implementsInterface(someClass, cl)) {
				frameworkClassesToInstancesCount.put(cl, frameworkClassesToInstancesCount.get(cl) + 1);
			}
		}
	}

	/**
	 * Returns the counted instances for a framework class.
	 *
	 * @param frameworkClass the framework class.
	 * @return the number of found and created instances.
	 */
	int getInstancesCount(final IClass frameworkClass) {
		return frameworkClassesToInstancesCount.get(frameworkClass);
	}

	/**
	 * Returns the application loader of the class hierarchy.
	 *
	 * @return the application loader.
	 */
	ClassLoaderReference getApplicationLoader() {
		return hierarchy.getScope().getApplicationLoader();
	}

	/**
	 * Returns the extension loader of the class hierarchy.
	 *
	 * @return the extension loader.
	 */
	ClassLoaderReference getExtensionLoader() {
		return hierarchy.getScope().getExtensionLoader();
	}

	/**
	 * Returns a class for a class name.
	 *
	 * @param clRef class loader for the class.
	 * @param className name of the class.
	 * @return the class or null if it cannot be found.
	 */
	IClass getIClass(final ClassLoaderReference clRef, final String className) {
		return hierarchy.lookupClass(TypeReference.findOrCreate(clRef, className));
	}

	/**
	 * Returns all subtypes for a supertype. The supertype is not included.
	 *
	 * @param supertype the supertype.
	 * @return an iterable object with the subtypes.
	 */
	Iterable<IClass> getSubtypes(final IClass supertype) {
		if (supertype.isInterface()) {
			return hierarchy.getImplementors(supertype.getReference());
		} else {
			HashSet<IClass> resultingClasses = new HashSet<>();
			ArrayDeque<IClass> currentSubtypes = new ArrayDeque<>();
			currentSubtypes.addAll(hierarchy.getImmediateSubclasses(supertype));
			while (!currentSubtypes.isEmpty()) {
				IClass nextType = currentSubtypes.poll();
				resultingClasses.add(nextType);
				currentSubtypes.addAll(hierarchy.getImmediateSubclasses(nextType));
			}
			return resultingClasses;
		}
	}

	/**
	 * Checks if a class is a direct subclass of another class or if a class implements an interface.
	 *
	 * @param supertype the supertype: a superclass or an interface.
	 * @param subtype the subtype.
	 * @return true if the class given as subtype is a direct subtype of supertype. false otherwise.
	 */
	boolean isDirectSubtype(final IClass supertype, final IClass subtype) {
		return hierarchy.isSubclassOf(subtype, supertype) || hierarchy.implementsInterface(subtype, supertype);
	}

	/**
	 * Searches a class for a constructor.
	 *
	 * @param cl the class.
	 * @return the constructor or null if no can be found.
	 */
	IMethod findInit(final IClass cl) {
		IMethod m = cl.getMethod(Selector.make(APIConstants.DEFAULT_CONSTRUCTOR_SIGNATURE));
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
}
