package processor;

import perturbator.UtilPerturbation;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.ModifierKind;

public class PertVarProcessor extends AbstractProcessor<CtVariableRead>{

	@Override
	public void process(CtVariableRead var) {
		var.replace(UtilPerturbation.createStaticCall(getFactory(), "p"+ UtilPerturbation.function(var),  getFactory().Core().clone(var)));
	}

	@Override
	public boolean isToBeProcessed(CtVariableRead candidate) {

		if (UtilPerturbation.checkClass(candidate))
			return false;

		if (candidate.getParent() instanceof CtUnaryOperator && ((CtUnaryOperator) candidate.getParent()).getKind() != UnaryOperatorKind.NEG
				&& ((CtUnaryOperator) candidate.getParent()).getKind() != UnaryOperatorKind.NOT &&
				((CtUnaryOperator) candidate.getParent()).getKind() != UnaryOperatorKind.COMPL &&
				((CtUnaryOperator) candidate.getParent()).getKind() != UnaryOperatorKind.POS)
			return false;

		if (candidate.getType() == null)
			return false;

		if (candidate.getParent() instanceof CtCase)
			return false;

		if (candidate.getParent() instanceof CtField) {
			if (((CtField) candidate.getParent()).getModifiers().contains(ModifierKind.FINAL))
				return false;
		}

		if (candidate.getParent() instanceof CtInvocation && ! (((CtInvocation)candidate.getParent()).getArguments().contains(candidate)))
			return false;

		if (candidate instanceof CtFieldWrite)
			return false;

		if (candidate.getParent() instanceof CtBinaryOperator) {
			if (((CtBinaryOperator)candidate.getParent()).getLeftHandOperand().getType() != null &&
					!((CtBinaryOperator)candidate.getParent()).getLeftHandOperand().getType().isPrimitive() ||
					((CtBinaryOperator)candidate.getParent()).getRightHandOperand().getType() != null &&
							!((CtBinaryOperator)candidate.getParent()).getRightHandOperand().getType().isPrimitive())
				return false;
		}

		return UtilPerturbation.types.contains(candidate.getType().getSimpleName());
	}
}
