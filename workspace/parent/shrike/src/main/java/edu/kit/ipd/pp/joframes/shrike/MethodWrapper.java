package edu.kit.ipd.pp.joframes.shrike;

import com.ibm.wala.shrikeBT.MethodData;
import com.ibm.wala.shrikeBT.MethodEditor;

/**
 * Wraps a method and gives the possibility to instrument it.
 *
 * @author Martin Armbruster
 */
public class MethodWrapper {
	/**
	 * Stores the editor for the method.
	 */
	private MethodEditor editor;

	/**
	 * Creates a new instance.
	 *
	 * @param data the method data to wrap.
	 */
	MethodWrapper(final MethodData data) {
		editor = new MethodEditor(data);
	}
}
