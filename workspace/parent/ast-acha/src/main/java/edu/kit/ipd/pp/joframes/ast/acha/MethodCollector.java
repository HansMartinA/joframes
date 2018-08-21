package edu.kit.ipd.pp.joframes.ast.acha;

import com.ibm.wala.classLoader.IMethod;
import edu.kit.ipd.pp.joframes.ast.base.Rule;
import java.util.HashSet;
import java.util.Set;

/**
 * Collection of methods generated by the regular expression and super type rules, represented as a rule.
 * 
 * @author Martin Armbruster
 */
public class MethodCollector extends Rule {
	/**
	 * Stores the methods this object collects.
	 */
	private HashSet<IMethod> methods;
	
	/**
	 * Creates a new instance.
	 */
	public MethodCollector() {
		methods = new HashSet<>();
	}
	
	/**
	 * Adds a method to the collection.
	 * 
	 * @param method the method to add.
	 */
	public void addMethod(IMethod method) {
		methods.add(method);
	}
	
	/**
	 * Returns the set of collected methods.
	 * 
	 * @return the set.
	 */
	public Set<IMethod> getMethodCollection() {
		return (Set<IMethod>)methods.clone();
	}
}
