package edu.kit.ipd.pp.joframes.api;

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
import java.io.InputStream;
import java.io.IOException;
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
	 */
	Framework parse(String file) {
		Source xmlFile = new StreamSource(new File(file));
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			Schema schema = schemaFactory.newSchema(getClass().getClassLoader()
					.getResource("framework_specification_language_model.xsd"));
			Validator validator = schema.newValidator();
			validator.validate(xmlFile);
		} catch(SAXException e) {
			System.out.println("The provided xml file is not valid: "+e.getLocalizedMessage());
			System.exit(1);
		} catch(IOException e) {
		}
		Framework framework = null;
		try(InputStream in = new FileInputStream(file)) {
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader xmlParser = factory.createXMLStreamReader(in);
			while(xmlParser.hasNext()) {
				switch(xmlParser.getEventType()) {
					case XMLStreamConstants.START_ELEMENT:
						if(xmlParser.getLocalName().equals(ELEMENT_FRAMEWORK)) {
							framework = parseFramework(xmlParser);
						}
						break;
					case XMLStreamConstants.END_DOCUMENT:
						xmlParser.close();
						break;
				}
				xmlParser.next();
			}
		} catch(IOException | XMLStreamException e) {
		}
		return framework;
	}
	
	/**
	 * Parses the framework element.
	 * 
	 * @param xmlParser the xml parser.
	 * @return the parsed Framework object.
	 */
	private Framework parseFramework(XMLStreamReader xmlParser) {
		try {
			String frameworkName = null;
			for(int i=0; i<xmlParser.getAttributeCount(); i++) {
				if(xmlParser.getAttributeLocalName(i).equals(ATTRIBUTE_FRAMEWORK_NAME)) {
					frameworkName = xmlParser.getAttributeValue(i);
				}
			}
			Framework framework = new Framework(frameworkName);
			while(xmlParser.hasNext()) {
				switch(xmlParser.getEventType()) {
					case XMLStreamConstants.START_ELEMENT:
						String elementName = xmlParser.getLocalName();
						if(elementName.equals(ELEMENT_START_PHASE)) {
							StartPhase start = parseStartPhase(xmlParser);
							framework.setStartPhase(start);
						} else if(elementName.equals(ELEMENT_END_PHASE)) {
							EndPhase end = parseEndPhase(xmlParser);
							framework.setEndPhase(end);
						} else if(elementName.equals(ELEMENT_WORKING_PHASE)) {
							WorkingPhase working = parseWorkingPhase(xmlParser);
							framework.addWorkingPhase(working);
						} else if(elementName.equals(ELEMENT_RESOURCE_LOADER)) {
							ResourceLoader loader = parseResourceLoader(xmlParser);
							framework.setResourceLoader(loader);
						}
						break;
					case XMLStreamConstants.END_ELEMENT:
						if(xmlParser.getLocalName().equals(ELEMENT_FRAMEWORK)) {
							return framework;
						}
						break;
				}
				xmlParser.next();
			}
		} catch(XMLStreamException e) {
		}
		return null;
	}
	
	/**
	 * Parses the resource loader element.
	 * 
	 * @param xmlParser the xml parser.
	 * @return the parsed resource loader.
	 */
	private ResourceLoader parseResourceLoader(XMLStreamReader xmlParser) {
		try {
			while(xmlParser.hasNext()) {
				switch(xmlParser.getEventType()) {
					case XMLStreamConstants.CHARACTERS:
						return new ResourceLoader(xmlParser.getText().trim());
				}
				xmlParser.next();
			}
		} catch(XMLStreamException e) {
		}
		return null;
	}
	
	/**
	 * Parses the start phase element.
	 * 
	 * @param xmlParser the xml parser.
	 * @return the parsed start phase.
	 */
	private StartPhase parseStartPhase(XMLStreamReader xmlParser) {
		StartPhase start = new StartPhase();
		try {
			while(xmlParser.hasNext()) {
				switch(xmlParser.getEventType()) {
					case XMLStreamConstants.START_ELEMENT:
						if(xmlParser.getLocalName().equals(ELEMENT_EXPLICIT_DECLARATION)) {
							ExplicitDeclaration declaration = parseExplicitDeclaration(xmlParser);
							start.addExplicitDeclaration(declaration);
						}
						break;
					case XMLStreamConstants.END_ELEMENT:
						if(xmlParser.getLocalName().equals(ELEMENT_START_PHASE)) {
							return start;
						}
						break;
				}
				xmlParser.next();
			}
		} catch(XMLStreamException e) {
		}
		return start;
	}
	
	/**
	 * Parses the end phase element.
	 * 
	 * @param xmlParser the xml parser.
	 * @return the parsed end phase.
	 */
	private EndPhase parseEndPhase(XMLStreamReader xmlParser) {
		try {
			while(xmlParser.hasNext()) {
				switch(xmlParser.getEventType()) {
					case XMLStreamConstants.START_ELEMENT:
						if(xmlParser.getLocalName().equals(ELEMENT_EXPLICIT_DECLARATION)) {
							ExplicitDeclaration declaration = parseExplicitDeclaration(xmlParser);
							return new EndPhase(declaration);
						}
						break;
				}
				xmlParser.next();
			}
		} catch(XMLStreamException e) {
		}
		return null;
	}
	
	/**
	 * Parses a working phase element.
	 * 
	 * @param xmlParser the xml parser.
	 * @return the parsed working phase.
	 */
	private WorkingPhase parseWorkingPhase(XMLStreamReader xmlParser) {
		try {
			WorkingPhase working = null;
			for(int i=0; i<xmlParser.getAttributeCount(); i++) {
				if(xmlParser.getAttributeLocalName(i).equals(ATTRIBUTE_THREAD_TYPE)) {
					ThreadType type = ThreadType.valueOf(xmlParser.getAttributeValue(i).toUpperCase());
					working = new WorkingPhase(type);
				}
			}
			while(xmlParser.hasNext()) {
				switch(xmlParser.getEventType()) {
					case XMLStreamConstants.START_ELEMENT:
						if(xmlParser.getLocalName().equals(ELEMENT_RULE_REGEX)) {
							Rule regex = parseRegexRule(xmlParser);
							working.addRule(regex);
						} else if(xmlParser.getLocalName().equals(ELEMENT_RULE_SUPERTYPE)) {
							Rule supertype = parseSupertypeRule(xmlParser);
							working.addRule(supertype);
						} else if(xmlParser.getLocalName().equals(ELEMENT_RULE_BLOCK)) {
							Rule block = parseBlockRule(xmlParser);
							working.addRule(block);
						}
						break;
					case XMLStreamConstants.END_ELEMENT:
						if(xmlParser.getLocalName().equals(ELEMENT_WORKING_PHASE)) {
							return working;
						}
						break;
				}
				xmlParser.next();
			}
		} catch(XMLStreamException e) {
		}
		return null;
	}
	
	/**
	 * Parses an explicit declaration element.
	 * 
	 * @param xmlParser the xml parser.
	 * @return the parsed explicit declaration.
	 */
	private ExplicitDeclaration parseExplicitDeclaration(XMLStreamReader xmlParser) {
		try {
			String className = null;
			for(int i=0; i<xmlParser.getAttributeCount(); i++) {
				if(xmlParser.getAttributeLocalName(i).equals(ATTRIBUTE_EXPLICIT_DECLARATION_FOR_ALL)) {
					className = xmlParser.getAttributeValue(i);
				}
			}
			ExplicitDeclaration declaration;
			if(className==null) {
				declaration = new ExplicitDeclaration();
			} else {
				declaration = new ExplicitDeclaration(className);
			}
			xmlParser.next();
			while(xmlParser.hasNext()) {
				switch(xmlParser.getEventType()) {
					case XMLStreamConstants.START_ELEMENT:
						if(xmlParser.getLocalName().equals(ELEMENT_METHOD_CALL)) {
							Call call = parseMethodCall(xmlParser);
							declaration.addCall(call);
						} else if(xmlParser.getLocalName().equals(ELEMENT_STATIC_METHOD_CALL)) {
							Call call = parseStaticMethodCall(xmlParser);
							declaration.addCall(call);
						} else if(xmlParser.getLocalName().equals(ELEMENT_EXPLICIT_DECLARATION)) {
							ExplicitDeclaration innerDeclaration = parseExplicitDeclaration(xmlParser);
							declaration.addExplicitDeclaration(innerDeclaration);
						}
						break;
					case XMLStreamConstants.END_ELEMENT:
						if(xmlParser.getLocalName().equals(ELEMENT_EXPLICIT_DECLARATION)) {
							return declaration;
						}
						break;
				}
				xmlParser.next();
			}
		} catch(XMLStreamException e) {
		}
		return null;
	}
	
	/**
	 * Parses a method call.
	 * 
	 * @param xmlParser the xml parser.
	 * @return the parsed method call.
	 */
	private Call parseMethodCall(XMLStreamReader xmlParser) {
		try {
			while(xmlParser.hasNext()) {
				switch(xmlParser.getEventType()) {
					case XMLStreamConstants.CHARACTERS:
						return new Method(xmlParser.getText().trim());
				}
				xmlParser.next();
			}
		} catch(XMLStreamException e) {
		}
		return null;
	}
	
	/**
	 * Parses a static method call.
	 * 
	 * @param xmlParser the xml parser.
	 * @return the parsed static method call.
	 */
	private Call parseStaticMethodCall(XMLStreamReader xmlParser) {
		try {
			while(xmlParser.hasNext()) {
				switch(xmlParser.getEventType()) {
					case XMLStreamConstants.CHARACTERS:
						String[] splittedText = xmlParser.getText().trim().split(" ");
						return new StaticMethod(splittedText[0], splittedText[1]);
				}
				xmlParser.next();
			}
		} catch(XMLStreamException e) {
		}
		return null;
	}
	
	/**
	 * Parses a regular expression rule element.
	 * 
	 * @param xmlParser the xml parser.
	 * @return the parsed regular expression rule.
	 */
	private Rule parseRegexRule(XMLStreamReader xmlParser) {
		try {
			while(xmlParser.hasNext()) {
				switch(xmlParser.getEventType()) {
					case XMLStreamConstants.CHARACTERS:
						return new Regex(xmlParser.getText().trim());
				}
				xmlParser.next();
			}
		} catch(XMLStreamException e) {
		}
		return null;
	}
	
	/**
	 * Parses the supertype rule element.
	 * 
	 * @param xmlParser the xml parser.
	 * @return the parsed supertype rule.
	 */
	private Rule parseSupertypeRule(XMLStreamReader xmlParser) {
		try {
			while(xmlParser.hasNext()) {
				switch(xmlParser.getEventType()) {
					case XMLStreamConstants.CHARACTERS:
						return new Supertype(xmlParser.getText().trim());
				}
				xmlParser.next();
			}
		} catch(XMLStreamException e) {
		}
		return null;
	}
	
	/**
	 * Parses the block rule element.
	 * 
	 * @param xmlParser the xml parser.
	 * @return the parsed block rule.
	 */
	private Block parseBlockRule(XMLStreamReader xmlParser) {
		try {
			BlockQuantor quantor = null;
			String className = null;
			for(int i=0; i<xmlParser.getAttributeCount(); i++) {
				String attributeName = xmlParser.getAttributeLocalName(i);
				if(attributeName.equals(ATTRIBUTE_BLOCK_QUANTOR)) {
					quantor = BlockQuantor.valueOf(xmlParser.getAttributeValue(i).toUpperCase());
				} else if(attributeName.equals(ATTRIBUTE_BLOCK_CLASS)) {
					className = xmlParser.getAttributeValue(i);
				}
			}
			xmlParser.next();
			Block innerBlock = null;
			ExplicitDeclaration declaration = null;
			while(xmlParser.hasNext()) {
				switch(xmlParser.getEventType()) {
					case XMLStreamConstants.START_ELEMENT:
						if(xmlParser.getLocalName().equals(ELEMENT_RULE_BLOCK)) {
							innerBlock = parseBlockRule(xmlParser);
						} else if(xmlParser.getLocalName().equals(ELEMENT_EXPLICIT_DECLARATION)) {
							declaration = parseExplicitDeclaration(xmlParser);
						}
						break;
					case XMLStreamConstants.END_ELEMENT:
						if(xmlParser.getLocalName().equals(ELEMENT_RULE_BLOCK)) {
							Block block = null;
							if(innerBlock==null) {
								block = new Block(quantor, className, innerBlock);
							} else {
								block = new Block(quantor, className, declaration);
							}
							return block;
						}
						break;
				}
				xmlParser.next();
			}
		} catch(XMLStreamException e) {
		}
		return null;
	}
}
