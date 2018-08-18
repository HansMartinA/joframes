package edu.kit.ipd.pp.joframes.ast.base;

/**
 * Represents the class name of the resource loader.
 * 
 * @author Martin Armbruster
 */
public class ResourceLoader implements AstBaseClass {
	/**
	 * Stores the class name of the resource loader.
	 */
	private String className;
	
	/**
	 * Creates a new instance.
	 * 
	 * @param className the class name of the resource loader.
	 */
	public ResourceLoader(String className) {
		this.className = className;
	}
	
	/**
	 * Returns the class name of the resource loader.
	 * 
	 * @return the class name.
	 */
	public String getClassName() {
		return className;
	}
}
