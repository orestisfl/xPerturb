package processor;

import perturbator.UtilPerturbation;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBinaryOperator;

public class PertBinOpProcessor extends AbstractProcessor<CtBinaryOperator>{

	public void process(CtBinaryOperator op) {
		op.replace(UtilPerturbation.createStaticCall(getFactory(), "p"+ UtilPerturbation.function(op),  getFactory().Core().clone(op)));
	}

	@Override
	public boolean isToBeProcessed(CtBinaryOperator candidate) {
		if (UtilPerturbation.checkClass(candidate))
			return false;
		return UtilPerturbation.types.contains(candidate.getType().getSimpleName());
	}
}
