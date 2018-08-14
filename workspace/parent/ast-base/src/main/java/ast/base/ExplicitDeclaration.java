package ast.base;

import java.util.ArrayList;

/**
 * Represents a sequence of method calls.
 * 
 * @author Martin Armbruster
 */
public class ExplicitDeclaration implements AstBaseClass {
	/**
	 * Stores the optional class name associated with this explicit declaration.
	 */
	private String className;
	/**
	 * Stores all calls related to this explicit declaration.
	 */
	private ArrayList<AstBaseClass> calls;
	
	/**
	 * Creates a new instance.
	 */
	public ExplicitDeclaration() {
		calls = new ArrayList<>();
	}
	
	/**
	 * Creates a new instance.
	 * 
	 * @param className the optional class name associated with the newly created explicit declaration.
	 */
	public ExplicitDeclaration(String className) {
		this();
		this.className = className;
	}
	
	/**
	 * Returns the class name associated with this explicit declaration.
	 * 
	 * @return the class name or null if no class was given.
	 */
	public String getClassName() {
		return className;
	}
	
	/**
	 * Adds a call to this explicit declaration.
	 * 
	 * @param call the call to add.
	 */
	public void addCall(Call call) {
		calls.add(call);
	}
	
	/**
	 * Adds an explicit declaration.
	 * 
	 * @param declaration the explicit declaration to add.
	 */
	public void addExplicitDeclaration(ExplicitDeclaration declaration) {
		calls.add(declaration);
	}
}
