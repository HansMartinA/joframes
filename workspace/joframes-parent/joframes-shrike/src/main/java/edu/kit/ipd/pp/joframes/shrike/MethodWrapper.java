package edu.kit.ipd.pp.joframes.shrike;

import com.ibm.wala.shrikeBT.ExceptionHandler;
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
	 * Stores all labels associated to the endInstructions. If a label equals -1, then no label was allocated and
	 * emitted for the instruction.
	 */
	private ArrayList<Integer> endLabels;
	/**
	 * Stores a list of all added instructions that will be inserted at the end, before the return instruction.
	 */
	private ArrayList<Instruction> endInstructions;
	/**
	 * Stores a list of all ExceptionHandlers for the endInstructions.
	 */
	private ArrayList<ExceptionHandler[]> endExHandlers;
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
		endExHandlers = new ArrayList<>();
		afterReturnInstructions = new ArrayList<>();
		editor.beginPass();
	}

	/**
	 * Returns the class type to which this method belongs.
	 *
	 * @return the class type.
	 */
	public String getClassType() {
		return editor.getData().getClassType();
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
	 * Adds a label.
	 *
	 * @param label the label.
	 */
	public void addLabelAtEnd(final int label) {
		addInstructionAtEnd(label, null, null);
	}

	/**
	 * Adds instructions that will be inserted at the end.
	 *
	 * @param instructions array of all instructions.
	 */
	public void addInstructionsAtEnd(final Instruction[] instructions) {
		for (Instruction ins : instructions) {
			addInstructionAtEnd(ins);
		}
	}

	/**
	 * Adds an instruction that will be inserted at the end.
	 *
	 * @param ins the instruction.
	 */
	public void addInstructionAtEnd(final Instruction ins) {
		addInstructionAtEnd(-1, ins, null);
	}

	/**
	 * Adds an instruction that will be inserted at the end.
	 *
	 * @param ins the instruction.
	 * @param handlers exception handlers for the instruction.
	 */
	public void addInstructionAtEnd(final Instruction ins, final ExceptionHandler[] handlers) {
		addInstructionAtEnd(-1, ins, handlers);
	}

	/**
	 * Adds an instruction that will be inserted at the end.
	 *
	 * @param label label associated to the instruction.
	 * @param ins the instruction.
	 */
	public void addInstructionAtEnd(final int label, final Instruction ins) {
		addInstructionAtEnd(label, ins, null);
	}

	/**
	 * Adds an instruction that will be inserted at the end.
	 *
	 * @param label label associated to the instruction.
	 * @param ins the instruction.
	 * @param handlers exception handlers for the instruction. Can be null.
	 */
	public void addInstructionAtEnd(final int label, final Instruction ins, final ExceptionHandler[] handlers) {
		endLabels.add(label);
		endInstructions.add(ins);
		endExHandlers.add(handlers);
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
		int lastInstructionIndex = editor.getInstructions().length - 1;
		Instruction lastInstruction = (Instruction) editor.getInstructions()[lastInstructionIndex];
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
					ExceptionHandler[] handlers = endExHandlers.get(i);
					Instruction instruction = endInstructions.get(i);
					if (instruction == null) {
						continue;
					}
					if (handlers == null) {
						w.emit(instruction);
					} else {
						w.emit(instruction, handlers);
					}
				}
				w.emit(lastInstruction);
				for (Instruction ins : afterReturnInstructions) {
					w.emit(ins);
				}
			}
		});
		editor.applyPatches();
		editor.endPass();
		endLabels.clear();
		endInstructions.clear();
		endExHandlers.clear();
		afterReturnInstructions.clear();
		editor.beginPass();
	}

	/**
	 * Visits all instructions of the method, excluding added instructions that are not instrumented yet. The visitor
	 * allows the addition of further instructions that are added directly after the visit. Local variables and labels
	 * have to be allocated before a visit.
	 *
	 * @param visitor visitor for the instructions.
	 */
	public void visitInstructions(final MethodEditor.Visitor visitor) {
		editor.visitInstructions(visitor);
		editor.applyPatches();
		editor.endPass();
		editor.beginPass();
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
