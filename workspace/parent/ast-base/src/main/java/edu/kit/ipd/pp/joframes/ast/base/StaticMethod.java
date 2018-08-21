package edu.kit.ipd.pp.joframes.ast.base;

import com.ibm.wala.classLoader.IClass;

/**
 * Represents a call to a static method.
 * 
 * @author Martin Armbruster
 */
public class StaticMethod extends Call {
	/**
	 * Stores the class name the method belongs to.
	 */
	private String classString;
	/**
	 * Stores the class corresponding to the class string.
	 */
	private IClass correspondingClass;
	
	/**
	 * Creates a new instance.
	 * 
	 * @param classString class name the method belongs to.
	 * @param signature signature of the method this call goes to.
	 */
	public StaticMethod(String classString, String signature) {
		super(signature);
		this.classString = classString;
	}
	
	/**
	 * Returns the class name the method belongs to.
	 * 
	 * @return the class name the method belongs to.
	 */
	public String getClassString() {
		return classString;
	}
	
	/**
	 * Sets the corresponding class to the contained class name.
	 * 
	 * @param correspondingClass the corresponding class.
	 */
	public void setIClass(IClass correspondingClass) {
		this.correspondingClass = correspondingClass;
	}
	
	/**
	 * Returns the class corresponding to the contained class name.
	 * 
	 * @return the corresponding class.
	 */
	public IClass getIClass() {
		return correspondingClass;
	}
}
