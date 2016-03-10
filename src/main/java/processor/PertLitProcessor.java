package processor;

import perturbator.UtilPerturbation;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtArrayTypeReference;

public class PertLitProcessor extends AbstractProcessor<CtLiteral>{
	
	public void process(CtLiteral lit) {
		lit.replace(UtilPerturbation.createStaticCall(getFactory(), "p"+ UtilPerturbation.function(lit),  getFactory().Core().clone(lit)));
	}

	private static void addCast(CtLiteral candidate, CtNewArray parent) {
		if (parent.getDimensionExpressions().size() == 0) {
			if (parent.getType() instanceof CtArrayTypeReference) {
				CtArrayTypeReference ref = (CtArrayTypeReference) (parent.getType());
				if (ref.getComponentType() != candidate.getType() && candidate.getTypeCasts().isEmpty()) {
					candidate.addTypeCast(ref.getComponentType());
				}
			}
		}
	}

	@Override
	public boolean isToBeProcessed(CtLiteral candidate) {

		if (UtilPerturbation.checkClass(candidate))
			return false;

		if(candidate.getType() == null || candidate.getParent() instanceof CtCase ||
				(candidate.getParent() instanceof CtUnaryOperator && candidate.getParent().getParent() instanceof CtCase)) {
			return false;
		}

		if (!candidate.getType().isPrimitive()) {
			return false;
		}

		if (candidate.getParent() instanceof CtWhile && (Boolean) (candidate.getValue()) == true) {
			return false;
		}

		if (candidate.getParent() instanceof CtField) {
			if (((CtField) candidate.getParent()).getModifiers().contains(ModifierKind.FINAL))
				return false;
		}

		if(candidate.getParent() instanceof CtLocalVariable) {
			if (UtilPerturbation.types.contains(((CtLocalVariable) candidate.getParent()).getType().getSimpleName().toLowerCase()))
				return true;
		}

		if (candidate.getParent() instanceof  CtNewArray)
			addCast(candidate, (CtNewArray) candidate.getParent());



		if (candidate.getParent() instanceof CtConditional && candidate.getParent().getParent() instanceof CtNewArray) {
			if (candidate != ((CtConditional) candidate.getParent()).getCondition())
				addCast(candidate, (CtNewArray) candidate.getParent().getParent());
		}

		return UtilPerturbation.types.contains(candidate.getType().getSimpleName().toLowerCase());
	}

}
