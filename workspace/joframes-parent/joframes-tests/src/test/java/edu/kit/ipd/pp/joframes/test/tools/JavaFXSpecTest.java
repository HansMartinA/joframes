package edu.kit.ipd.pp.joframes.test.tools;

import edu.kit.ipd.pp.joframes.test.tools.AnalysisApplicator.JoanaProfiles;
import edu.kit.ipd.pp.joframes.test.tools.AnalysisApplicator.SupportedFrameworks;
import edu.kit.joana.api.lattice.BuiltinLattices;
import org.junit.Test;

/**
 * Class that tests the JavaFX test cases.
 *
 * @author Martin Armbruster
 */
public class JavaFXSpecTest extends BasicTest {
	/**
	 * Stores the current main class.
	 */
	private String mainClass;

	/**
	 * Tests the JavaFX test case which violates confidentiality by showing a secret in a text field.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testConf1() throws Exception {
		mainClass = "Ledu/kit/ipd/pp/joframes/jfx/conf/ConfApplication";
		anaApp.addSource("edu.kit.ipd.pp.joframes.jfx.conf.ConfEventHandler.secret", BuiltinLattices.STD_SECLEVEL_HIGH);
		anaApp.addSink("javafx.scene.control.TextInputControl.setText(Ljava/lang/String;)V->p1",
				BuiltinLattices.STD_SECLEVEL_LOW);
		super.makeAndPrintResults("jfx-conf1.jar", "jfx-conf1.jar", JoanaProfiles.MODERATE, 1, false, false);
	}

	@Override
	String getMainClass() {
		return mainClass;
	}

	@Override
	SupportedFrameworks getFramework() {
		return SupportedFrameworks.JAVAFX;
	}

	@Override
	void annotateDefaultSource() {
	}

	@Override
	void annotateDefaultSink() {
	}
}
