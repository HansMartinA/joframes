package ast.base;

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
}
