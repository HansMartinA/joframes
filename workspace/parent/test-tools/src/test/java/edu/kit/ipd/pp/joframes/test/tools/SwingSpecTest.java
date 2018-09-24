package edu.kit.ipd.pp.joframes.test.tools;

import edu.kit.ipd.pp.joframes.test.tools.AnalysisApplicator.JoanaProfiles;
import edu.kit.ipd.pp.joframes.test.tools.AnalysisApplicator.SupportedFrameworks;
import edu.kit.joana.api.lattice.BuiltinLattices;
import org.junit.Test;

/**
 * A test class for Swing.
 *
 * @author Martin Armbruster
 */
public class SwingSpecTest extends BasicTest {
	/**
	 * Stores the current class containing the main class.
	 */
	private String mainClass;

	/**
	 * Tests the simple Swing test case which violates confidentiality.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testConf() throws Exception {
		mainClass = "Ledu/kit/ipd/pp/joframes/test/swing/conf/Main";
		anaApp.addSource("edu.kit.ipd.pp.joframes.test.swing.conf.ConfActionListener.secret",
				BuiltinLattices.STD_SECLEVEL_HIGH);
		anaApp.addSink("javax.swing.JTextField.setText(Ljava/lang/String;)V->p1", BuiltinLattices.STD_SECLEVEL_LOW);
		makeAndPrintResults("sw-conf1.jar", "sw-conf1.jar", JoanaProfiles.HIGH_PRECISION, 1, false, false);
	}

	@Override
	SupportedFrameworks getFramework() {
		return SupportedFrameworks.SWING;
	}

	@Override
	String getMainClass() {
		return mainClass;
	}

	@Override
	void annotateDefaultSource() {
	}

	@Override
	void annotateDefaultSink() {
	}
}
