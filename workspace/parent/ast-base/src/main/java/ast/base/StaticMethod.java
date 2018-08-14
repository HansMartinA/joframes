package ast.base;

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
}
