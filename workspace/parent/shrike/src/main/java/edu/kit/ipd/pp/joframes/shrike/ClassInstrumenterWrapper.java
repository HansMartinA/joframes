package edu.kit.ipd.pp.joframes.shrike;

import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;

/**
 * Wraps a ClassInstrumenter and, therefore, a class.
 *
 * @author Martin Armbruster
 */
public class ClassInstrumenterWrapper {
	/**
	 * Stores the wrapped ClassInstrumenter.
	 */
	private ClassInstrumenter clInstr;

	/**
	 * Creates a new instance.
	 *
	 * @param clInstrWrap class instrumenter that will be wrapped.
	 */
	ClassInstrumenterWrapper(final ClassInstrumenter clInstrWrap) {
		clInstr = clInstrWrap;
	}
}
