package edu.kit.ipd.pp.joframes.ast.base;

import com.ibm.wala.classLoader.IMethod;

/**
 * Represents a method call.
 * 
 * @author Martin Armbruster
 */
public abstract class Call implements AstBaseClass {
	/**
	 * Stores the signature of the method this call goes to.
	 */
	private String signature;
	/**
	 * Stores the corresponding method to the signature.
	 */
	private IMethod method;
	
	/**
	 * Creates a new instance.
	 * 
	 * @param signature signature of the method this call goes to.
	 */
	protected Call(String signature) {
		this.signature = signature;
	}
	
	/**
	 * Returns the signature of the method this call goes to.
	 * 
	 * @return the signature of the method this call goes to.
	 */
	public String getSignature() {
		return signature;
	}
	
	/**
	 * Sets the corresponding method to the containing signature.
	 * 
	 * @param method the corresponding method.
	 */
	public void setMethod(IMethod method) {
		this.method = method;
	}
	
	/**
	 * Returns the method corresponding to the contained signature.
	 * 
	 * @return the method.
	 */
	public IMethod getMethod() {
		return method;
	}
}
