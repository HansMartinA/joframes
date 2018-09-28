package edu.kit.ipd.pp.joframes.shrike;

import com.ibm.wala.shrikeBT.ArrayLoadInstruction;
import com.ibm.wala.shrikeBT.BinaryOpInstruction;
import com.ibm.wala.shrikeBT.ConditionalBranchInstruction;
import com.ibm.wala.shrikeBT.ConstantInstruction;
import com.ibm.wala.shrikeBT.Constants;
import com.ibm.wala.shrikeBT.DupInstruction;
import com.ibm.wala.shrikeBT.GotoInstruction;
import com.ibm.wala.shrikeBT.IBinaryOpInstruction;
import com.ibm.wala.shrikeBT.IConditionalBranchInstruction;
import com.ibm.wala.shrikeBT.IInvokeInstruction;
import com.ibm.wala.shrikeBT.Instruction;
import com.ibm.wala.shrikeBT.InvokeInstruction;
import com.ibm.wala.shrikeBT.LoadInstruction;
import com.ibm.wala.shrikeBT.PopInstruction;
import com.ibm.wala.shrikeBT.StoreInstruction;

/**
 * Factory class for instructions.
 *
 * @author Martin Armbruster
 */
public final class InstructionFactory {
	/**
	 * Private constructor to avoid instantiation.
	 */
	private InstructionFactory() {
	}

	/**
	 * Creates an invocation instruction for an object initialization method.
	 *
	 * @param className name of the class.
	 * @param methodSignature signature of the method.
	 * @return the instruction.
	 */
	public static Instruction makeInit(final String className, final String methodSignature) {
		return InvokeInstruction.make(methodSignature, className, "<init>", IInvokeInstruction.Dispatch.SPECIAL);
	}

	/**
	 * Creates instructions that increment an integer by one.
	 *
	 * @param index index of the local variable in which the integer is stored.
	 * @return the instructions.
	 */
	public static Instruction[] makeIncrement(final int index) {
		return new Instruction[] {
				LoadInstruction.make(Constants.TYPE_int, index),
				ConstantInstruction.make(1),
				BinaryOpInstruction.make(Constants.TYPE_int, IBinaryOpInstruction.Operator.ADD),
				StoreInstruction.make(Constants.TYPE_int, index),
		};
	}

	/**
	 * Creates instructions to load an array element.
	 *
	 * @param arrayType type of the array.
	 * @param arrayIndex index of the local variable in which the array reference is stored.
	 * @param elementIndex index of the local variable in which the element index to load is stored.
	 * @return the instructions.
	 */
	public static Instruction[] makeArrayElementLoad(final String arrayType, final int arrayIndex,
			final int elementIndex) {
		return new Instruction[] {
				LoadInstruction.make(arrayType, arrayIndex),
				LoadInstruction.make(Constants.TYPE_int, elementIndex),
				ArrayLoadInstruction.make(arrayType),
		};
	}

	/**
	 * Creates instructions that compare two integers.
	 *
	 * @param firstIndex index of the local variable in which the first integer is stored.
	 * @param secondIndex index of the local variable in which the second integer is stored.
	 * @param op compare operation.
	 * @param label label to which is branched if the comparison evaluates to true.
	 * @return the instructions.
	 */
	public static Instruction[] makeCompareIntegers(final int firstIndex, final int secondIndex,
			final IConditionalBranchInstruction.Operator op, final int label) {
		return new Instruction[] {
				LoadInstruction.make(Constants.TYPE_int, firstIndex),
				LoadInstruction.make(Constants.TYPE_int, secondIndex),
				ConditionalBranchInstruction.make(Constants.TYPE_int, op, label),
		};
	}

	/**
	 * Creates a DupInstruction that duplicates the word on top of the stack.
	 *
	 * @return the instruction.
	 */
	public static Instruction makeDup() {
		return DupInstruction.make(0);
	}

	/**
	 * Creates a PopInstruction that pops the word on top of the stack off from the stack.
	 *
	 * @return the instruction.
	 */
	public static Instruction makePop() {
		return PopInstruction.make(1);
	}

	/**
	 * Creates a GotoInstruction.
	 *
	 * @param label label to which the execution branches.
	 * @return the created instruction.
	 */
	public static Instruction makeGoto(final int label) {
		return GotoInstruction.make(label);
	}
}
