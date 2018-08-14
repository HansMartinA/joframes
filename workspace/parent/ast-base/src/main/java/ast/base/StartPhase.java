package ast.base;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents the start phase in a framework.
 * 
 * @author Martin Armbruster
 */
public class StartPhase implements AstBaseClass {
	/**
	 * Stores all explicit declarations associated to the start phase.
	 */
	private HashSet<ExplicitDeclaration> declarations;
	
	/**
	 * Creates a new instance.
	 */
	public StartPhase() {
		declarations = new HashSet<>();
	}
	
	/**
	 * Adds an explicit declaration to the start phase.
	 * 
	 * @param declaration the explicit declaration to add.
	 */
	public void addExplicitDeclaration(ExplicitDeclaration declaration) {
		declarations.add(declaration);
	}
	
	/**
	 * Returns a set with all explicit declarations connected to the start phase.
	 * 
	 * @return the set.
	 */
	public Set<ExplicitDeclaration> getDeclarations() {
		return (Set<ExplicitDeclaration>)declarations.clone();
	}
}
