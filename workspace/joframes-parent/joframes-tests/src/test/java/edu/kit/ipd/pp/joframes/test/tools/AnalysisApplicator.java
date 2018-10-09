package edu.kit.ipd.pp.joframes.test.tools;

import com.ibm.wala.util.NullProgressMonitor;
import edu.kit.ipd.pp.joframes.api.APIConstants;
import edu.kit.ipd.pp.joframes.api.Pipeline;
import edu.kit.ipd.pp.joframes.shrike.InstrumenterWrapper;
import edu.kit.joana.api.IFCAnalysis;
import edu.kit.joana.api.sdg.SDGConfig;
import edu.kit.joana.api.sdg.SDGProgram;
import edu.kit.joana.api.sdg.SDGProgramPart;
import edu.kit.joana.ifc.sdg.core.SecurityNode;
import edu.kit.joana.ifc.sdg.core.violations.IViolation;
import edu.kit.joana.ifc.sdg.graph.SDGSerializer;
import edu.kit.joana.ifc.sdg.mhpoptimization.MHPType;
import edu.kit.joana.ifc.sdg.util.JavaMethodSignature;
import edu.kit.joana.util.Stubs;
import edu.kit.joana.wala.core.SDGBuilder.ExceptionAnalysis;
import edu.kit.joana.wala.core.SDGBuilder.PointsToPrecision;
import gnu.trove.map.TObjectIntMap;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import static org.junit.Assert.fail;

/**
 * This class applies the analysis and instrumentation of JoFrames and the analysis of Joana to a given
 * framework and application.
 *
 * @author Martin Armbruster
 */
class AnalysisApplicator {
	/**
	 * A set of supported frameworks for test cases.
	 *
	 * @author Martin Armbruster
	 */
	enum SupportedFrameworks {
		/**
		 * Servlet API specification.
		 */
		SERVLET,
		/**
		 * Swing.
		 */
		SWING,
		/**
		 * JavaFX.
		 */
		JAVAFX,
	}

	/**
	 * Settings profiles for the Joana analysis.
	 *
	 * @author Martin Armbruster
	 */
	enum JoanaProfiles {
		/**
		 * Settings that offer an analysis with highest precision.
		 */
		HIGH_PRECISION,
		/**
		 * Settings that offer a trade-off between performance for larger applications and precision for the analysis.
		 */
		MODERATE,
		/**
		 * Settings that offer a fast analysis.
		 */
		FAST,
		/**
		 * Settings that offer a faster analysis than the fast settings.
		 */
		FASTEST,
	}
	/**
	 * Stores the sources with their security level for annotation.
	 */
	private HashMap<String, String> sources;
	/**
	 * Stores the sinks witht their security level for annotation.
	 */
	private HashMap<String, String> sinks;

	/**
	 * Creates a new instance.
	 */
	AnalysisApplicator() {
		sources = new HashMap<>();
		sinks = new HashMap<>();
	}

	/**
	 * Adds a source for annotation.
	 *
	 * @param source the source.
	 * @param level the security level of the source.
	 */
	void addSource(final String source, final String level) {
		sources.put(source, level);
	}

	/**
	 * Adds a sink for annotation.
	 *
	 * @param sink the sink.
	 * @param level the security level of the sink.
	 */
	void addSink(final String sink, final String level) {
		sinks.put(sink, level);
	}

	/**
	 * Applies the analysis.
	 *
	 * @param framework the framework the application belongs to.
	 * @param applicationJars paths to the jar files containing the application classes.
	 * @param mainClass name of the main class of the application.
	 * @param output name of the output jar file.
	 * @param joProfile profile for Joana.
	 * @returns the results of the analysis.
	 * @throws Exception if something goes wrong.
	 */
	AAResults applyAnalysis(final SupportedFrameworks framework, final String[] applicationJars, final String mainClass,
			final String output, final JoanaProfiles joProfile) throws Exception {
		long analysisStart = System.currentTimeMillis();
		AAResults result = new AAResults();
		InstrumenterWrapper instr = new InstrumenterWrapper();
		instr.setExclusionRegex(APIConstants.DEFAULT_EXCLUSION_REGEX);
		for (String appJar : applicationJars) {
			instr.addInputJar(appJar);
		}
		result.insCountBefore = instr.countInstructions();
		instr = new InstrumenterWrapper();
		instr.setExclusionRegex(APIConstants.DEFAULT_EXCLUSION_REGEX);
		String[] frameworkJars = null;
		String specPath = new File("").getAbsoluteFile().getParentFile().getAbsolutePath() + File.separator
				+ "joframes-api" + File.separator + "src" + File.separator + "main" + File.separator + "resources"
				+ File.separator;
		if (framework == SupportedFrameworks.SERVLET) {
			frameworkJars = new String[] {
					"target" + File.separator + "tomcat-servlet-api-9.0.12.jar",
					"target" + File.separator + "tomcat-annotations-api-9.0.12.jar",
					"target" + File.separator + "tomcat-api-9.0.12.jar",
					"target" + File.separator + "tomcat-catalina-9.0.12.jar",
					"target" + File.separator + "tomcat-coyote-9.0.12.jar",
					"target" + File.separator + "tomcat-jni-9.0.12.jar",
					"target" + File.separator + "tomcat-juli-9.0.12.jar",
					"target" + File.separator + "tomcat-util-9.0.12.jar",
					"target" + File.separator + "tomcat-util-scan-9.0.12.jar"};
			for (String j : frameworkJars) {
				instr.addInputJar(j);
			}
			specPath += "Servlets.xml";
		} else if (framework == SupportedFrameworks.SWING) {
			specPath += "Swing.xml";
		} else if (framework == SupportedFrameworks.JAVAFX) {
			frameworkJars = new String[] {
					System.getProperty("java.home") + File.separator + "lib" + File.separator + "ext" + File.separator
					+ "jfxrt.jar"};
			instr.addInputJar(frameworkJars[0]);
			specPath += "JavaFX.xml";
		}
		result.insFramework = instr.countInstructions();
		Pipeline p = new Pipeline(specPath, frameworkJars, applicationJars);
		p.setOutput("target" + File.separator + output);
		p.setMainClass(mainClass);
		long startTime = System.currentTimeMillis();
		p.process();
		result.timeProcessing = System.currentTimeMillis() - startTime;
		instr = new InstrumenterWrapper();
		instr.addInputJar(p.getOutput());
		result.insCountAfter = instr.countInstructions();
		System.out.println("Instructions: " + result.getFrameworkInstructionCount() + " (F), "
				+ result.getApplicationInstructionCount() + " (A), " + result.getAdditionalInstructionsCount());
		System.out.println("Time for JoFrames: " + result.getProcessingTime() + "ms");
		JavaMethodSignature entryMethod = JavaMethodSignature.mainMethodOfClass(
				"edu.kit.ipd.pp.joframes.api.external.ArtificialClass");
		SDGConfig sdgConfig = new SDGConfig(p.getOutput(), entryMethod.toBCString(), Stubs.JRE_17);
		sdgConfig.setComputeInterferences(true);
		sdgConfig.setExclusions(APIConstants.DEFAULT_EXCLUSION_REGEX);
		if (joProfile == JoanaProfiles.HIGH_PRECISION) {
			sdgConfig.setMhpType(MHPType.PRECISE);
			sdgConfig.setPointsToPrecision(PointsToPrecision.N1_OBJECT_SENSITIVE);
			sdgConfig.setExceptionAnalysis(ExceptionAnalysis.INTERPROC);
		} else if (joProfile == JoanaProfiles.MODERATE) {
			sdgConfig.setMhpType(MHPType.SIMPLE);
			sdgConfig.setPointsToPrecision(PointsToPrecision.INSTANCE_BASED);
			sdgConfig.setExceptionAnalysis(ExceptionAnalysis.INTRAPROC);
		} else if (joProfile == JoanaProfiles.FAST) {
			sdgConfig.setMhpType(MHPType.SIMPLE);
			sdgConfig.setPointsToPrecision(PointsToPrecision.TYPE_BASED);
			sdgConfig.setExceptionAnalysis(ExceptionAnalysis.ALL_NO_ANALYSIS);
		} else if (joProfile == JoanaProfiles.FASTEST) {
			sdgConfig.setComputeInterferences(false);
			sdgConfig.setComputeSummaryEdges(false);
			sdgConfig.setPointsToPrecision(PointsToPrecision.TYPE_BASED);
			sdgConfig.setExceptionAnalysis(ExceptionAnalysis.IGNORE_ALL);
		}
		SDGProgram sdg = SDGProgram.createSDGProgram(sdgConfig, System.out, new NullProgressMonitor());
		// SDGSerializer.toPDGFormat(sdg.getSDG(), new FileOutputStream("SDG.pdg"));
		IFCAnalysis ifcAna = new IFCAnalysis(sdg);
		for (Entry<String, String> entry : sources.entrySet()) {
			SDGProgramPart part = sdg.getPart(entry.getKey());
			if (part == null) {
				System.out.println("Source annotation " + entry.getKey() + " not found.");
				fail("Source annotation not found.");
			}
			ifcAna.addSourceAnnotation(part, entry.getValue());
		}
		for (Entry<String, String> entry : sinks.entrySet()) {
			SDGProgramPart part = sdg.getPart(entry.getKey());
			if (part == null) {
				System.out.println("Sink annotation " + entry.getKey() + " not found.");
				fail("Sink annotation not found.");
			}
			ifcAna.addSinkAnnotation(part, entry.getValue());
		}
		startTime = System.currentTimeMillis();
		Collection<? extends IViolation<SecurityNode>> ifcResult = ifcAna.doIFC();
		result.timeJoana = System.currentTimeMillis() - startTime;
		TObjectIntMap<IViolation<SDGProgramPart>> sortedIFCResult = ifcAna.groupByPPPart(ifcResult);
		result.violations = sortedIFCResult;
		result.time = System.currentTimeMillis() - analysisStart;
		System.out.println("Time for Joana: " + result.getTimeOfJoana() + "ms");
		System.out.println("Overall time: " + result.getOverallTime() + "ms");
		return result;
	}

	/**
	 * Class for storing the results of the AnalysisApplicator.
	 *
	 * @author Martin Armbruster
	 */
	class AAResults {
		/**
		 * Stores the instruction count of the framework.
		 */
		private long insFramework;
		/**
		 * Stores the instruction count of the application before the analysis and instrumentation of the application.
		 */
		private long insCountBefore;
		/**
		 * Stores the instruction count after the analysis and instrumentation of the application.
		 */
		private long insCountAfter;
		/**
		 * Stores the overall time the AnalysisApplicator needed.
		 */
		private long time;
		/**
		 * Stores the time needed for the analysis and instrumentation of the application.
		 */
		private long timeProcessing;
		/**
		 * Stores the time of the IFC analysis of Joana.
		 */
		private long timeJoana;
		/**
		 * Stores the violations Joana found.
		 */
		private TObjectIntMap<IViolation<SDGProgramPart>> violations;

		/**
		 * Returns the instruction count of the framework.
		 *
		 * @return the instruction count.
		 */
		long getFrameworkInstructionCount() {
			return insFramework;
		}

		/**
		 * Returns the instruction count of the application without the instrumented code.
		 *
		 * @return the instruction count.
		 */
		long getApplicationInstructionCount() {
			return insCountBefore;
		}

		/**
		 * Returns the count of additional instrumented instructions.
		 *
		 * @return the instruction count.
		 */
		long getAdditionalInstructionsCount() {
			return insCountAfter - insCountBefore - insFramework;
		}

		/**
		 * Returns the overall time the AnalysisApplicator needed.
		 *
		 * @return the overall time.
		 */
		long getOverallTime() {
			return time;
		}

		/**
		 * Returns the time needed for the analysis and instrumentation of JoFrames.
		 *
		 * @return the needed time.
		 */
		long getProcessingTime() {
			return timeProcessing;
		}

		/**
		 * Returns the time needed for the analysis of Joana.
		 *
		 * @return the needed time.
		 */
		long getTimeOfJoana() {
			return timeJoana;
		}

		/**
		 * Returns a collection of all found violations.
		 *
		 * @return the collection.
		 */
		TObjectIntMap<IViolation<SDGProgramPart>> getViolations() {
			return violations;
		}
	}
}
