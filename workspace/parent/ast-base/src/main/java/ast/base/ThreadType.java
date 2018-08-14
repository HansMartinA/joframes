package ast.base;

/**
 * Options for the threading policy of a working phase.
 * 
 * @author Martin Armbruster
 */
public enum ThreadType {
	/**
	 * Option for a single-threaded working phase.
	 */
	SINGLE,
	/**
	 * Option for a multi-threaded working phase.
	 */
	MULTI;
}
