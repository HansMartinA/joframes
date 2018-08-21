package edu.kit.ipd.pp.joframes.api.external;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class collects all instances from the application and the artificial main method that will be used in the
 * artificial main method, separated by registered classes.
 * 
 * @author Martin Armbruster
 */
public class InstanceCollector {
	/**
	 * Stores a mapping between a class and the list of instances.
	 */
	private static HashMap<Class<?>, ArrayList<Object>> classToInstances;
	
	static {
		classToInstances = new HashMap<>();
	}
	
	/**
	 * Creates a new instance. Private to avoid instantiation.
	 */
	private InstanceCollector() {
	}
	
	/**
	 * Adds a class.
	 * 
	 * @param addedClass the class to add.
	 */
	public static void addClass(Class<?> addedClass) {
		classToInstances.put(addedClass, new ArrayList<>());
	}
	
	/**
	 * Adds an instance. The instance is added to the lists of registered super types.
	 * 
	 * @param o the instance to add.
	 */
	public static void addInstance(Object o) {
		for(Class<?> cl : classToInstances.keySet()) {
			if(cl.isAssignableFrom(o.getClass())) {
				classToInstances.get(cl).add(o);
			}
		}
	}
	
	/**
	 * Returns an instance collection.
	 * 
	 * @param forClass class for which the collection is returned.
	 * @return the collection as list.
	 */
	public static List<Object> getInstances(Class<?> forClass) {
		return (List<Object>)classToInstances.get(forClass).clone();
	}
}
