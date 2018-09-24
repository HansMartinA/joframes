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
		anaApp.addSink("javax.swing.text.JTextComponent.setText(Ljava/lang/String;)V->p1",
				BuiltinLattices.STD_SECLEVEL_LOW);
		makeAndPrintResults("sw-conf1.jar", "sw-conf1.jar", JoanaProfiles.MODERATE, 1, false, false);
	}

	/**
	 * Tests the Swing test case with password input which violates confidentiality.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testPassword() throws Exception {
		mainClass = "Ledu/kit/ipd/pp/joframes/test/swing/password/MainPW";
		anaApp.addSource("javax.swing.JPasswordField.getText()Ljava/lang/String;->exit",
				BuiltinLattices.STD_SECLEVEL_HIGH);
		anaApp.addSource(
				"edu.kit.ipd.pp.joframes.test.swing.password.PasswordInput.keyReleased(Ljava/awt/event/KeyEvent;)V->p1",
				BuiltinLattices.STD_SECLEVEL_HIGH);
		anaApp.addSink("javax.swing.text.JTextComponent.setText(Ljava/lang/String;)V->p1",
				BuiltinLattices.STD_SECLEVEL_LOW);
		anaApp.addSink("java.io.PrintStream.println(Ljava/lang/String;)V->p1", BuiltinLattices.STD_SECLEVEL_LOW);
		makeAndPrintResults("sw-conf2.jar", "sw-conf2.jar", JoanaProfiles.FAST, 2, false, false);
	}

	/**
	 * Tests the Swing test case which has no information flow.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testNoIF() throws Exception {
		mainClass = "Ledu/kit/ipd/pp/joframes/test/swing/no_ifc/MainNo";
		anaApp.addSource("javax.swing.text.JTextComponent.getText()Ljava/lang/String;->exit",
				BuiltinLattices.STD_SECLEVEL_HIGH);
		anaApp.addSink("java.io.PrintStream.println(Ljava/lang/String;)V->p1", BuiltinLattices.STD_SECLEVEL_LOW);
		makeAndPrintResults("sw-noif.jar", "sw-noif.jar", JoanaProfiles.MODERATE, 0, false, false);
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
