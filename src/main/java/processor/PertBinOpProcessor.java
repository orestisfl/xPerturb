package processor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBinaryOperator;

public class PertBinOpProcessor extends AbstractProcessor<CtBinaryOperator>{

	public void process(CtBinaryOperator op) {
		op.replace(PertProcessor.createStaticCall(getFactory(), "p"+PertProcessor.function(op),  getFactory().Core().clone(op)));
	}

	@Override
	public boolean isToBeProcessed(CtBinaryOperator candidate) {
		return PertProcessor.types.contains(candidate.getType().getSimpleName());
	}
}
