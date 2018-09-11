package edu.kit.ipd.pp.joframes.shrike;

import com.ibm.wala.shrikeBT.Instruction;
import com.ibm.wala.shrikeBT.MethodData;
import com.ibm.wala.shrikeBT.MethodEditor;
import com.ibm.wala.shrikeBT.info.LocalAllocator;
import java.util.ArrayList;

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
	 * Stores a list of all added instructions that will be inserted at the end, before the return instruction.
	 */
	private ArrayList<Instruction> endInstructions;
	/**
	 * Stores all labels associated to the endInstructions. If a label equals -1, then no label was allocated and
	 * emitted for the instruction.
	 */
	private ArrayList<Integer> endLabels;
	/**
	 * Stores all instructions that will be inserted after the last return instruction.
	 */
	private ArrayList<Instruction> afterReturnInstructions;

	/**
	 * Creates a new instance.
	 *
	 * @param data the method data to wrap.
	 */
	MethodWrapper(final MethodData data) {
		editor = new MethodEditor(data);
		endInstructions = new ArrayList<>();
		endLabels = new ArrayList<>();
		afterReturnInstructions = new ArrayList<>();
	}

	/**
	 * Returns the name of the method.
	 *
	 * @return the method name.
	 */
	public String getMethodName() {
		return editor.getData().getName();
	}

	/**
	 * Returns the signature of the method.
	 *
	 * @return the method signature.
	 */
	public String getMethodSignature() {
		return editor.getData().getSignature();
	}

	/**
	 * Allocates a new local variable.
	 *
	 * @param type type of the local variable.
	 * @return index of the local variable.
	 */
	public int allocateLocalVariable(final String type) {
		return LocalAllocator.allocate(editor.getData(), type);
	}

	/**
	 * Allocates a new label for an instruction.
	 *
	 * @return the allocated label.
	 */
	public int allocateLabel() {
		return editor.allocateLabel();
	}

	/**
	 * Adds an instruction that will be inserted at the end.
	 *
	 * @param ins the instruction.
	 */
	public void addInstructionAtEnd(final Instruction ins) {
		addInstructionAtEnd(-1, ins);
	}

	/**
	 * Adds an instruction that will be inserted at the end.
	 *
	 * @param label label associated to the instruction.
	 * @param ins the instruction.
	 */
	public void addInstructionAtEnd(final int label, final Instruction ins) {
		endLabels.add(label);
		endInstructions.add(ins);
	}

	/**
	 * Adds an instruction that will be inserted at the end of the method after the last return instruction.
	 *
	 * @param ins the instruction.
	 */
	public void addInstructionAtLast(final Instruction ins) {
		afterReturnInstructions.add(ins);
	}

	/**
	 * Instruments the method with the added instructions.
	 */
	public void instrumentMethod() {
		editor.beginPass();
		int lastInstructionIndex = editor.getInstructions().length - 1;
		editor.replaceWith(lastInstructionIndex, new MethodEditor.Patch() {
			@Override
			public void emitTo(final MethodEditor.Output w) {
			}
		});
		editor.insertAfter(lastInstructionIndex, new MethodEditor.Patch() {
			@Override
			public void emitTo(final MethodEditor.Output w) {
				for (int i = 0; i < endLabels.size(); i++) {
					Integer label = endLabels.get(i);
					if (label != -1) {
						w.emitLabel(label);
					}
					Instruction instruction = endInstructions.get(i);
					w.emit(instruction);
				}
				w.emit((Instruction) editor.getInstructions()[lastInstructionIndex]);
				for (Instruction ins : afterReturnInstructions) {
					w.emit(ins);
				}
			}
		});
		editor.applyPatches();
		editor.endPass();
		endLabels.clear();
		endInstructions.clear();
	}

	/**
	 * Visits all instructions of the method, excluding added instructions that are not instrumented yet. The visitor
	 * allows the addition of further instructions that are added directly after the visit. Local variables and labels
	 * have to be allocated before a visit.
	 *
	 * @param visitor visitor for the instructions.
	 */
	public void visitInstructions(final MethodEditor.Visitor visitor) {
		editor.beginPass();
		editor.visitInstructions(visitor);
		editor.applyPatches();
		editor.endPass();
	}

	/**
	 * Returns the wrapped method data.
	 *
	 * @return the wrapped method.
	 */
	MethodData getMethodData() {
		return editor.getData();
	}
}
