package edu.kit.ipd.pp.joframes.api;

import edu.kit.ipd.pp.joframes.ast.base.Framework;

/**
 * This class parses a framework specification and generates an abstract syntax tree out of it.
 * 
 * @author Martin Armbruster
 */
class FrameworkSpecificationParser {
	/**
	 * Name of the root element.
	 */
	private static final String ELEMENT_FRAMEWORK = "framework-specification";
	/**
	 * Name of the resource loader element.
	 */
	private static final String ELEMENT_RESOURCE_LOADER = "resource-loader";
	/**
	 * Name of the start phase element.
	 */
	private static final String ELEMENT_START_PHASE = "start";
	/**
	 * Name of the end phase element.
	 */
	private static final String ELEMENT_END_PHASE = "end";
	/**
	 * Name of the working phase element.
	 */
	private static final String ELEMENT_WORKING_PHASE = "working";
	/**
	 * Name of the explicit declaration element.
	 */
	private static final String ELEMENT_EXPLICIT_DECLARATION = "explicit-declaration";
	/**
	 * Name of the method call element within an explicit declaration.
	 */
	private static final String ELEMENT_METHOD_CALL = "method-call";
	/**
	 * Name of the static call element within an explicit declaration.
	 */
	private static final String ELEMENT_METHOD_STATIC_CALL = "static-call";
	/**
	 * Name of the regular expression rule element.
	 */
	private static final String ELEMENT_RULE_REGEX = "rule-regex";
	/**
	 * Name of the super type rule element.
	 */
	private static final String ELEMENT_RULE_SUPERTYPE = "rule-supertype";
	/**
	 * Name of the block rule element.
	 */
	private static final String ELEMENT_RULE_BLOCK = "rule-block";
	/**
	 * Name of the thread type attribute within a working phase element.
	 */
	private static final String ATTRIBUTE_THREAD_TYPE = "threads";
	/**
	 * Name of the framework name attribute.
	 */
	private static final String ATTRIBUTE_FRAMEWORK_NAME = "name";
	/**
	 * Name of the block quantor attribute.
	 */
	private static final String ATTRIBUTE_BLOCK_QUANTOR = "quantor";
	/**
	 * Name of the class name attribute within a block rule.
	 */
	private static final String ATTRIBUTE_BLOCK_CLASS = "class";
	/**
	 * Name of the class name attribute within an explicit declaration.
	 */
	private static final String ATTRIBUTE_EXPLICIT_DECLARATION_FOR_ALL = "for_all";
	
	/**
	 * Parses a framework specification.
	 * 
	 * @param file xml-file containing the framework specification.
	 * @return the parsed framework specification as abstract syntax tree.
	 */
	Framework parse(String file) {
		return null;
	}
}
