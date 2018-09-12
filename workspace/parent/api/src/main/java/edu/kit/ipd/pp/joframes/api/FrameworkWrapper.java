package edu.kit.ipd.pp.joframes.api;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import edu.kit.ipd.pp.joframes.ast.base.Framework;
import java.util.HashMap;
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
}
