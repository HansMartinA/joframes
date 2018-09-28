package edu.kit.ipd.pp.joframes.ast.base;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

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
	 * Stores all application classes that are subclasses of the correspondingClass with a mapping to their constructor.
	 */
	private HashMap<IClass, IMethod> applicationClassToInit;
	/**
	 * Stores all calls related to this explicit declaration.
	 */
	private ArrayList<AstBaseClass> calls;

	/**
	 * Creates a new instance.
	 */
	public ExplicitDeclaration() {
		calls = new ArrayList<>();
		applicationClassToInit = new HashMap<>();
	}

	/**
	 * Creates a new instance.
	 *
	 * @param clName the optional class name associated with the newly created explicit declaration.
	 */
	public ExplicitDeclaration(final String clName) {
		this();
		this.className = clName;
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
	 * @param correspondingCl the corresponding class.
	 */
	public void setIClass(final IClass correspondingCl) {
		this.correspondingClass = correspondingCl;
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
	 * Adds an application class which is a subclass to the corresponding class of this explicit declaration
	 * and its constructor.
	 *
	 * @param cl the application class.
	 * @param init constructor of the application class.
	 */
	public void addApplicationClass(final IClass cl, final IMethod init) {
		applicationClassToInit.put(cl, init);
	}

	/**
	 * Returns a set of all application classes that are subclasses of the corresponding class of this explicit
	 * declaration.
	 *
	 * @return the set of all application classes.
	 */
	public Set<IClass> getApplicationClasses() {
		return applicationClassToInit.keySet();
	}

	/**
	 * Returns the constructor for an application class.
	 *
	 * @param appClass the application class.
	 * @return the constructor.
	 */
	public IMethod getConstructor(final IClass appClass) {
		return applicationClassToInit.get(appClass);
	}

	/**
	 * Adds a call to this explicit declaration.
	 *
	 * @param call the call to add.
	 */
	public void addCall(final Call call) {
		calls.add(call);
	}

	/**
	 * Adds an explicit declaration.
	 *
	 * @param declaration the explicit declaration to add.
	 */
	public void addExplicitDeclaration(final ExplicitDeclaration declaration) {
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
	public AstBaseClass getCallOrDeclaration(final int number) {
		return calls.get(number);
	}

	/**
	 * Visits all contained classes.
	 *
	 * @param visitor the visitor.
	 */
	public void visitContent(final ExplicitDeclarationVisitor visitor) {
		for (int i = 0; i < getNumberOfCallsAndDeclarations(); i++) {
			AstBaseClass nextElement = getCallOrDeclaration(i);
			if (nextElement.getClass() == ExplicitDeclaration.class) {
				visitor.visitExplicitDeclaration((ExplicitDeclaration) nextElement);
			} else if (nextElement.getClass() == Method.class) {
				visitor.visitMethod((Method) nextElement);
			} else if (nextElement.getClass() == StaticMethod.class) {
				visitor.visitStaticMethod((StaticMethod) nextElement);
			}
		}
	}
}
