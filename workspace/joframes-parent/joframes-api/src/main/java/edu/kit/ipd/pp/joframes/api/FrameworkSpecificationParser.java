package edu.kit.ipd.pp.joframes.api;

import edu.kit.ipd.pp.joframes.api.exceptions.ParseException;
import edu.kit.ipd.pp.joframes.api.logging.Log;
import edu.kit.ipd.pp.joframes.ast.ap.Block;
import edu.kit.ipd.pp.joframes.ast.ap.BlockQuantor;
import edu.kit.ipd.pp.joframes.ast.ap.Regex;
import edu.kit.ipd.pp.joframes.ast.ap.Supertype;
import edu.kit.ipd.pp.joframes.ast.base.Call;
import edu.kit.ipd.pp.joframes.ast.base.EndPhase;
import edu.kit.ipd.pp.joframes.ast.base.ExplicitDeclaration;
import edu.kit.ipd.pp.joframes.ast.base.Framework;
import edu.kit.ipd.pp.joframes.ast.base.Method;
import edu.kit.ipd.pp.joframes.ast.base.ResourceLoader;
import edu.kit.ipd.pp.joframes.ast.base.Rule;
import edu.kit.ipd.pp.joframes.ast.base.StartPhase;
import edu.kit.ipd.pp.joframes.ast.base.StaticMethod;
import edu.kit.ipd.pp.joframes.ast.base.ThreadType;
import edu.kit.ipd.pp.joframes.ast.base.WorkingPhase;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;

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
	 * Name of the static method call element within an explicit declaration.
	 */
	private static final String ELEMENT_STATIC_METHOD_CALL = "static-call";
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
	 * @throws ParseException when it is tried to parse an invalid framework specification or an exception occurs
	 *                        during parsing.
	 */
	Framework parse(final String file) throws ParseException {
		Log.logExtended("Validating the framework specification " + file + ".");
		Source xmlFile = new StreamSource(new File(file));
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			Schema schema = schemaFactory.newSchema(getClass().getClassLoader()
					.getResource("framework_specification_language_model.xsd"));
			Validator validator = schema.newValidator();
			validator.validate(xmlFile);
		} catch (SAXException e) {
			Log.endLog("The framework specification is not valid.");
			throw new ParseException("The provided xml file is not valid.", e);
		} catch (IOException e) {
			Log.endLog("The framework specification cannot be validated.");
			throw new ParseException("The provided xml file cannot be validated.", e);
		}
		Log.logExtended("The framework specification is valid.");
		Log.log("Parsing the framework specification " + file + ".");
		Framework framework = null;
		try (InputStream in = new FileInputStream(file)) {
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader xmlStream = factory.createXMLStreamReader(in);
			while (xmlStream.hasNext()) {
				switch (xmlStream.getEventType()) {
					case XMLStreamConstants.START_ELEMENT:
						if (xmlStream.getLocalName().equals(ELEMENT_FRAMEWORK)) {
							framework = parseFramework(xmlStream);
						}
						break;
					case XMLStreamConstants.END_DOCUMENT:
						xmlStream.close();
						break;
					default: break;
				}
				xmlStream.next();
			}
		} catch (XMLStreamException e) {
			Log.endLog("The framework specification could not be parsed.");
			throw new ParseException("An exception occurred while parsing.", e);
		} catch (IOException e) {
			Log.endLog("The framework specification could not be parsed.");
			throw new ParseException("Thr provided xml file cannot be parsed.", e);
		}
		Log.logExtended("Successfully parsed the framework specification.");
		return framework;
	}

	/**
	 * Parses the framework element.
	 *
	 * @param xmlStream the xml parser.
	 * @return the parsed Framework object.
	 * @throws XMLStreamException when reading or parsing the xml file fails.
	 */
	private Framework parseFramework(final XMLStreamReader xmlStream) throws XMLStreamException {
		String frameworkName = null;
		for (int i = 0; i < xmlStream.getAttributeCount(); i++) {
			if (xmlStream.getAttributeLocalName(i).equals(ATTRIBUTE_FRAMEWORK_NAME)) {
				frameworkName = xmlStream.getAttributeValue(i);
			}
		}
		Framework framework = new Framework(frameworkName);
		while (xmlStream.hasNext()) {
			switch (xmlStream.getEventType()) {
				case XMLStreamConstants.START_ELEMENT:
					String elementName = xmlStream.getLocalName();
					if (elementName.equals(ELEMENT_START_PHASE)) {
						StartPhase start = parseStartPhase(xmlStream);
						framework.setStartPhase(start);
					} else if (elementName.equals(ELEMENT_END_PHASE)) {
						EndPhase end = parseEndPhase(xmlStream);
						framework.setEndPhase(end);
					} else if (elementName.equals(ELEMENT_WORKING_PHASE)) {
						WorkingPhase working = parseWorkingPhase(xmlStream);
						framework.addWorkingPhase(working);
					} else if (elementName.equals(ELEMENT_RESOURCE_LOADER)) {
						ResourceLoader loader = parseResourceLoader(xmlStream);
						framework.setResourceLoader(loader);
					}
					break;
				case XMLStreamConstants.END_ELEMENT:
					if (xmlStream.getLocalName().equals(ELEMENT_FRAMEWORK)) {
						return framework;
					}
					break;
				default: break;
			}
			xmlStream.next();
		}
		return null;
	}

	/**
	 * Parses the resource loader element.
	 *
	 * @param xmlStream the xml parser.
	 * @return the parsed resource loader.
	 * @throws XMLStreamException when reading or parsing the xml file fails.
	 */
	private ResourceLoader parseResourceLoader(final XMLStreamReader xmlStream) throws XMLStreamException {
		while (xmlStream.hasNext()) {
			switch (xmlStream.getEventType()) {
				case XMLStreamConstants.CHARACTERS:
					return new ResourceLoader(xmlStream.getText().trim());
				default: break;
			}
			xmlStream.next();
		}
		return null;
	}

	/**
	 * Parses the start phase element.
	 *
	 * @param xmlStream the xml parser.
	 * @return the parsed start phase.
	 * @throws XMLStreamException when reading or parsing the xml file fails.
	 */
	private StartPhase parseStartPhase(final XMLStreamReader xmlStream) throws XMLStreamException {
		StartPhase start = new StartPhase();
		while (xmlStream.hasNext()) {
			switch (xmlStream.getEventType()) {
				case XMLStreamConstants.START_ELEMENT:
					if (xmlStream.getLocalName().equals(ELEMENT_EXPLICIT_DECLARATION)) {
						ExplicitDeclaration declaration = parseExplicitDeclaration(xmlStream);
						start.addExplicitDeclaration(declaration);
					}
					break;
				case XMLStreamConstants.END_ELEMENT:
					if (xmlStream.getLocalName().equals(ELEMENT_START_PHASE)) {
						return start;
					}
					break;
				default: break;
			}
			xmlStream.next();
		}
		return start;
	}

	/**
	 * Parses the end phase element.
	 *
	 * @param xmlStream the xml parser.
	 * @return the parsed end phase.
	 * @throws XMLStreamException when reading or parsing the xml file fails.
	 */
	private EndPhase parseEndPhase(final XMLStreamReader xmlStream) throws XMLStreamException {
		while (xmlStream.hasNext()) {
			switch (xmlStream.getEventType()) {
				case XMLStreamConstants.START_ELEMENT:
					if (xmlStream.getLocalName().equals(ELEMENT_EXPLICIT_DECLARATION)) {
						ExplicitDeclaration declaration = parseExplicitDeclaration(xmlStream);
						return new EndPhase(declaration);
					}
					break;
				default: break;
			}
			xmlStream.next();
		}
		return null;
	}

	/**
	 * Parses a working phase element.
	 *
	 * @param xmlStream the xml parser.
	 * @return the parsed working phase.
	 * @throws XMLStreamException when reading or parsing the xml file fails.
	 */
	private WorkingPhase parseWorkingPhase(final XMLStreamReader xmlStream) throws XMLStreamException {
		WorkingPhase working = null;
		for (int i = 0; i < xmlStream.getAttributeCount(); i++) {
			if (xmlStream.getAttributeLocalName(i).equals(ATTRIBUTE_THREAD_TYPE)) {
				ThreadType type = ThreadType.valueOf(xmlStream.getAttributeValue(i).toUpperCase());
				working = new WorkingPhase(type);
			}
		}
		while (xmlStream.hasNext()) {
			switch (xmlStream.getEventType()) {
				case XMLStreamConstants.START_ELEMENT:
					if (xmlStream.getLocalName().equals(ELEMENT_RULE_REGEX)) {
						Rule regex = parseRegexRule(xmlStream);
						working.addRule(regex);
					} else if (xmlStream.getLocalName().equals(ELEMENT_RULE_SUPERTYPE)) {
						Rule supertype = parseSupertypeRule(xmlStream);
						working.addRule(supertype);
					} else if (xmlStream.getLocalName().equals(ELEMENT_RULE_BLOCK)) {
						Rule block = parseBlockRule(xmlStream);
						working.addRule(block);
					}
					break;
				case XMLStreamConstants.END_ELEMENT:
					if (xmlStream.getLocalName().equals(ELEMENT_WORKING_PHASE)) {
						return working;
					}
					break;
				default: break;
			}
			xmlStream.next();
		}
		return null;
	}

	/**
	 * Parses an explicit declaration element.
	 *
	 * @param xmlStream the xml parser.
	 * @return the parsed explicit declaration.
	 * @throws XMLStreamException when reading or parsing the xml file fails.
	 */
	private ExplicitDeclaration parseExplicitDeclaration(final XMLStreamReader xmlStream) throws XMLStreamException {
		String className = null;
		for (int i = 0; i < xmlStream.getAttributeCount(); i++) {
			if (xmlStream.getAttributeLocalName(i).equals(ATTRIBUTE_EXPLICIT_DECLARATION_FOR_ALL)) {
				className = xmlStream.getAttributeValue(i);
			}
		}
		ExplicitDeclaration declaration;
		if (className == null) {
			declaration = new ExplicitDeclaration();
		} else {
			declaration = new ExplicitDeclaration(className);
		}
		xmlStream.next();
		while (xmlStream.hasNext()) {
			switch (xmlStream.getEventType()) {
				case XMLStreamConstants.START_ELEMENT:
					if (xmlStream.getLocalName().equals(ELEMENT_METHOD_CALL)) {
						Call call = parseMethodCall(xmlStream);
						declaration.addCall(call);
					} else if (xmlStream.getLocalName().equals(ELEMENT_STATIC_METHOD_CALL)) {
						Call call = parseStaticMethodCall(xmlStream);
						declaration.addCall(call);
					} else if (xmlStream.getLocalName().equals(ELEMENT_EXPLICIT_DECLARATION)) {
						ExplicitDeclaration innerDeclaration = parseExplicitDeclaration(xmlStream);
						declaration.addExplicitDeclaration(innerDeclaration);
					}
					break;
				case XMLStreamConstants.END_ELEMENT:
					if (xmlStream.getLocalName().equals(ELEMENT_EXPLICIT_DECLARATION)) {
						return declaration;
					}
					break;
				default: break;
			}
			xmlStream.next();
		}
		return null;
	}

	/**
	 * Parses a method call.
	 *
	 * @param xmlStream the xml parser.
	 * @return the parsed method call.
	 * @throws XMLStreamException when reading or parsing the xml file fails.
	 */
	private Call parseMethodCall(final XMLStreamReader xmlStream) throws XMLStreamException {
		while (xmlStream.hasNext()) {
			switch (xmlStream.getEventType()) {
				case XMLStreamConstants.CHARACTERS:
					return new Method(xmlStream.getText().trim());
				default: break;
			}
			xmlStream.next();
		}
		return null;
	}

	/**
	 * Parses a static method call.
	 *
	 * @param xmlStream the xml parser.
	 * @return the parsed static method call.
	 * @throws XMLStreamException when reading or parsing the xml file fails.
	 */
	private Call parseStaticMethodCall(final XMLStreamReader xmlStream) throws XMLStreamException {
		while (xmlStream.hasNext()) {
			switch (xmlStream.getEventType()) {
				case XMLStreamConstants.CHARACTERS:
					String[] splittedText = xmlStream.getText().trim().split(" ");
					if (splittedText.length == 1 && splittedText[0].equals(APIConstants.MAIN_SIGNATURE)) {
						return new StaticMethod(null, splittedText[0]);
					}
					return new StaticMethod(splittedText[0], splittedText[1]);
				default: break;
			}
			xmlStream.next();
		}
		return null;
	}

	/**
	 * Parses a regular expression rule element.
	 *
	 * @param xmlStream the xml parser.
	 * @return the parsed regular expression rule.
	 * @throws XMLStreamException when reading or parsing the xml file fails.
	 */
	private Rule parseRegexRule(final XMLStreamReader xmlStream) throws XMLStreamException {
		while (xmlStream.hasNext()) {
			switch (xmlStream.getEventType()) {
				case XMLStreamConstants.CHARACTERS:
					return new Regex(xmlStream.getText().trim());
				default: break;
			}
			xmlStream.next();
		}
		return null;
	}

	/**
	 * Parses the supertype rule element.
	 *
	 * @param xmlStream the xml parser.
	 * @return the parsed supertype rule.
	 * @throws XMLStreamException when reading or parsing the xml file fails.
	 */
	private Rule parseSupertypeRule(final XMLStreamReader xmlStream) throws XMLStreamException {
		while (xmlStream.hasNext()) {
			switch (xmlStream.getEventType()) {
				case XMLStreamConstants.CHARACTERS:
					return new Supertype(xmlStream.getText().trim());
				default: break;
			}
			xmlStream.next();
		}
		return null;
	}

	/**
	 * Parses the block rule element.
	 *
	 * @param xmlStream the xml parser.
	 * @return the parsed block rule.
	 * @throws XMLStreamException when reading or parsing the xml file fails.
	 */
	private Block parseBlockRule(final XMLStreamReader xmlStream) throws XMLStreamException {
		BlockQuantor quantor = null;
		String className = null;
		for (int i = 0; i < xmlStream.getAttributeCount(); i++) {
			String attributeName = xmlStream.getAttributeLocalName(i);
			if (attributeName.equals(ATTRIBUTE_BLOCK_QUANTOR)) {
				quantor = BlockQuantor.valueOf(xmlStream.getAttributeValue(i).toUpperCase());
			} else if (attributeName.equals(ATTRIBUTE_BLOCK_CLASS)) {
				className = xmlStream.getAttributeValue(i);
			}
		}
		xmlStream.next();
		Block innerBlock = null;
		ExplicitDeclaration declaration = null;
		while (xmlStream.hasNext()) {
			switch (xmlStream.getEventType()) {
				case XMLStreamConstants.START_ELEMENT:
					if (xmlStream.getLocalName().equals(ELEMENT_RULE_BLOCK)) {
						innerBlock = parseBlockRule(xmlStream);
					} else if (xmlStream.getLocalName().equals(ELEMENT_EXPLICIT_DECLARATION)) {
						declaration = parseExplicitDeclaration(xmlStream);
					}
					break;
				case XMLStreamConstants.END_ELEMENT:
					if (xmlStream.getLocalName().equals(ELEMENT_RULE_BLOCK)) {
						Block block = null;
						if (innerBlock != null) {
							block = new Block(quantor, className, innerBlock);
						} else {
							block = new Block(quantor, className, declaration);
						}
						return block;
					}
					break;
				default: break;
			}
			xmlStream.next();
		}
		return null;
	}
}
