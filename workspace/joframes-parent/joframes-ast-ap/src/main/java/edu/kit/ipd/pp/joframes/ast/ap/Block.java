package edu.kit.ipd.pp.joframes.ast.ap;

import com.ibm.wala.classLoader.IClass;
import edu.kit.ipd.pp.joframes.ast.base.ExplicitDeclaration;
import edu.kit.ipd.pp.joframes.ast.base.Rule;

/**
 * Represents a block as a rule for a working phase.
 *
 * @author Martin Armbruster
 */
public class Block extends Rule {
	/**
	 * Stores the quantor for this block.
	 */
	private BlockQuantor quantor;
	/**
	 * Stores the class name associated with this block.
	 */
	private String className;
	/**
	 * Stores the class corresponding to the class name.
	 */
	private IClass correspondingClass;
	/**
	 * Stores the next block within this block.
	 */
	private Block nextBlock;
	/**
	 * Stores the explicit declaration within this block.
	 */
	private ExplicitDeclaration declaration;

	/**
	 * Creates a new instance.
	 *
	 * @param blockQuantor the quantor for the block.
	 * @param clName class name associated with the block.
	 * @param innerBlock the next block within the newly created block.
	 */
	public Block(final BlockQuantor blockQuantor, final String clName, final Block innerBlock) {
		this.quantor = blockQuantor;
		this.className = clName;
		this.nextBlock = innerBlock;
	}

	/**
	 * Creates a new instance.
	 *
	 * @param blockQuantor the quantor for the block.
	 * @param clName class name associated with the block.
	 * @param decl the explicit declaration within the block.
	 */
	public Block(final BlockQuantor blockQuantor, final String clName, final ExplicitDeclaration decl) {
		this.quantor = blockQuantor;
		this.className = clName;
		this.declaration = decl;
	}

	/**
	 * Returns the quantor for this block.
	 *
	 * @return the quantor.
	 */
	public BlockQuantor getQuantor() {
		return quantor;
	}

	/**
	 * Returns the class name associated with this block.
	 *
	 * @return the class name.
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
	 * @return the corresponding class.
	 */
	public IClass getIClass() {
		return correspondingClass;
	}

	/**
	 * Returns the block within this block.
	 *
	 * @return the block or null if the block contains an explicit declaration.
	 */
	public Block getInnerBlock() {
		return nextBlock;
	}

	/**
	 * Returns the explicit declaration within this block.
	 *
	 * @return the explicit declaration or null if the block contains a block.
	 */
	public ExplicitDeclaration getDeclaration() {
		return declaration;
	}
}
