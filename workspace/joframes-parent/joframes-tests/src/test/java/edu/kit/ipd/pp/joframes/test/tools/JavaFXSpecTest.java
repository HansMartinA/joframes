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
		mainClass = "Ledu/kit/ipd/pp/joframes/test/jfx/conf/ConfApplication";
		anaApp.addSource("edu.kit.ipd.pp.joframes.test.jfx.conf.ConfEventHandler.secret",
				BuiltinLattices.STD_SECLEVEL_HIGH);
		anaApp.addSink("javafx.scene.control.TextInputControl.setText(Ljava/lang/String;)V->p1",
				BuiltinLattices.STD_SECLEVEL_LOW);
		makeAndPrintResults("jfx-conf1.jar", "jfx-conf1.jar", JoanaProfiles.FASTEST, 1, false, false);
	}

	/**
	 * Tests the JavaFX test case with a password input and its output.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testPassword() throws Exception {
		mainClass = "Ledu/kit/ipd/pp/joframes/test/jfx/password/PasswordApplication";
		anaApp.addSource("javafx.scene.control.TextInputControl.getText()Ljava/lang/String;->exit",
				BuiltinLattices.STD_SECLEVEL_HIGH);
		anaApp.addSink("java.io.PrintStream.println(Ljava/lang/String;)V->p1", BuiltinLattices.STD_SECLEVEL_LOW);
		makeAndPrintResults("jfx-password.jar", "jfx-password.jar", JoanaProfiles.FASTEST, 1, false, false);
	}

	/**
	 * Tests the JavaFX test case with a user name and password input and no information flow.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testExtendedPassword() throws Exception {
		mainClass = "Ledu/kit/ipd/pp/joframes/test/jfx/extended_password/ExtendedPasswordApplication";
		anaApp.addSource("edu.kit.ipd.pp.joframes.test.jfx.extended_password.ExtendedPasswordApplication.userName",
				BuiltinLattices.STD_SECLEVEL_LOW);
		anaApp.addSource("edu.kit.ipd.pp.joframes.test.jfx.extended_password.ExtendedPasswordApplication.password",
				BuiltinLattices.STD_SECLEVEL_HIGH);
		anaApp.addSink("java.io.Writer.append(Ljava/lang/CharSequence;)Ljava/io/Writer;->p1",
				BuiltinLattices.STD_SECLEVEL_LOW);
		makeAndPrintResults("jfx-extended-password.jar", "jfx-extended-password.jar", JoanaProfiles.FASTEST, 0,
				false, false);
	}

	/**
	 * Tests the JavaFX test case including a Worker.
	 *
	 * @throws Exception if something goes wrong.
	 */
	@Test
	public void testWorker() throws Exception {
		mainClass = "Ledu/kit/ipd/pp/joframes/test/jfx/worker/WorkerApplication";
		anaApp.addSource("edu.kit.ipd.pp.joframes.test.jfx.worker.WorkerWorker.secret",
				BuiltinLattices.STD_SECLEVEL_HIGH);
		anaApp.addSink("javafx.scene.control.TextInputControl.setText(Ljava/lang/String;)V->p1",
				BuiltinLattices.STD_SECLEVEL_LOW);
		makeAndPrintResults("jfx-worker.jar", "jfx-worker.jar", JoanaProfiles.FASTEST, 1, false, false);
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
