package edu.kit.ipd.pp.joframes.test.tools;

import com.ibm.wala.util.NullProgressMonitor;
import edu.kit.ipd.pp.joframes.api.Pipeline;
import edu.kit.ipd.pp.joframes.shrike.InstrumenterWrapper;
import edu.kit.joana.api.IFCAnalysis;
import edu.kit.joana.api.sdg.SDGConfig;
import edu.kit.joana.api.sdg.SDGProgram;
import edu.kit.joana.api.sdg.SDGProgramPart;
import edu.kit.joana.ifc.sdg.core.SecurityNode;
import edu.kit.joana.ifc.sdg.core.violations.IViolation;
import edu.kit.joana.ifc.sdg.mhpoptimization.MHPType;
import edu.kit.joana.ifc.sdg.util.JavaMethodSignature;
import edu.kit.joana.util.Stubs;
import edu.kit.joana.wala.core.SDGBuilder.ExceptionAnalysis;
import edu.kit.joana.wala.core.SDGBuilder.PointsToPrecision;
import gnu.trove.map.TObjectIntMap;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * This class applies the analysis and instrumentation of the [Framework Project] and the analysis of Joana to a given
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
		String[] frameworkJars = null;
		String specPath = new File("").getAbsoluteFile().getParentFile().getAbsolutePath() + File.separator + "api"
				+ File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator;
		if (framework == SupportedFrameworks.SERVLET) {
			String servletAPI = "target" + File.separator + "javax.servlet-api-4.0.1.jar";
			instr.addInputJar(servletAPI);
			frameworkJars = new String[] {servletAPI};
			specPath += "Servlets.xml";
		} else if (framework == SupportedFrameworks.SWING) {
			specPath += "Swing.xml";
		} else if (framework == SupportedFrameworks.JAVAFX) {
			specPath += "JavaFX.xml";
		}
		for (String appJar : applicationJars) {
			instr.addInputJar(appJar);
		}
		result.insCountBefore = instr.countInstructions();
		Pipeline p = new Pipeline(specPath, frameworkJars, applicationJars);
		p.setOutput("target" + File.separator + output);
		p.setMainClass(mainClass);
		long startTime = System.currentTimeMillis();
		p.process();
		result.timeProcessing = System.currentTimeMillis() - startTime;
		instr = new InstrumenterWrapper();
		instr.addInputJar(p.getOutput());
		result.insCountAfter = instr.countInstructions();
		JavaMethodSignature entryMethod = JavaMethodSignature.mainMethodOfClass(
				"edu.kit.ipd.pp.joframes.api.external.ArtificialClass");
		SDGConfig sdgConfig = new SDGConfig(p.getOutput(), entryMethod.toBCString(), Stubs.JRE_17);
		sdgConfig.setComputeInterferences(true);
		if (joProfile == JoanaProfiles.HIGH_PRECISION) {
			sdgConfig.setMhpType(MHPType.PRECISE);
			sdgConfig.setPointsToPrecision(PointsToPrecision.N1_OBJECT_SENSITIVE);
			sdgConfig.setExceptionAnalysis(ExceptionAnalysis.INTERPROC);
		} else if (joProfile == JoanaProfiles.MODERATE) {
			sdgConfig.setMhpType(MHPType.SIMPLE);
			sdgConfig.setPointsToPrecision(PointsToPrecision.INSTANCE_BASED);
			sdgConfig.setExceptionAnalysis(ExceptionAnalysis.INTRAPROC);
		}
		SDGProgram sdg = SDGProgram.createSDGProgram(sdgConfig, System.out, new NullProgressMonitor());
		IFCAnalysis ifcAna = new IFCAnalysis(sdg);
		for (Entry<String, String> entry : sources.entrySet()) {
			ifcAna.addSourceAnnotation(sdg.getPart(entry.getKey()), entry.getValue());
		}
		for (Entry<String, String> entry : sinks.entrySet()) {
			ifcAna.addSinkAnnotation(sdg.getPart(entry.getKey()), entry.getValue());
		}
		startTime = System.currentTimeMillis();
		Collection<? extends IViolation<SecurityNode>> ifcResult = ifcAna.doIFC();
		result.timeJoana = System.currentTimeMillis() - startTime;
		TObjectIntMap<IViolation<SDGProgramPart>> sortedIFCResult = ifcAna.groupByPPPart(ifcResult);
		result.violations = sortedIFCResult;
		result.time = System.currentTimeMillis() - analysisStart;
		return result;
	}

	/**
	 * Class for storing the results of the AnalysisApplicator.
	 *
	 * @author Martin Armbruster
	 */
	class AAResults {
		/**
		 * Stores the instruction count before the analysis and instrumentation of the application.
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
		 * Returns the instruction count of the framework and application without the instrumented code.
		 *
		 * @return the instruction count.
		 */
		long getFrameworkAndApplicationInstructionCount() {
			return insCountBefore;
		}

		/**
		 * Returns the count of additional instrumented instructions.
		 *
		 * @return the instruction count.
		 */
		long getAdditionalInstructionsCount() {
			return insCountAfter - insCountBefore;
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
		 * Returns the time needed for the analysis and instrumentation of the [Framework Project].
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
