package edu.kit.ipd.pp.joframes.ast.base;

import com.ibm.wala.classLoader.IClass;
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
	 * Stores the class corresponding to the class name.
	 */
	private IClass correspondingClass;
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
	 * Sets the class corresponding to the contained class name.
	 * 
	 * @param correspondingClass the corresponding class.
	 */
	public void setIClass(IClass correspondingClass) {
		this.correspondingClass = correspondingClass;
	}
	
	/**
	 * Returns the class corresponding to the contained class name.
	 * 
	 * @return the corresponding class or null if the class name is null.
	 */
	public IClass getIClass() {
		return correspondingClass;
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
	
	/**
	 * Returns the number of added method and static method calls and explicit declarations.
	 * 
	 * @return the number.
	 */
	public int getNumberOfCallsAndDeclarations() {
		return calls.size();
	}
	
	/**
	 * Returns an added method or static method call or explicit declaration. 
	 * 
	 * @param number the number of the call or explicit declaration to return. Must be within 0 and
	 *               getNumberOfCallsAndDeclarations().
	 * @return the call or explicit declaration.
	 */
	public AstBaseClass getCallOrDeclaration(int number) {
		return calls.get(number);
	}
}
