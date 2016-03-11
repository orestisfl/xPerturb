package processor;

import perturbator.UtilPerturbation;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtTypeReference;

/**
 * Created by spirals on 09/03/16.
 */
public class VariableCaster extends AbstractProcessor<CtVariable> {

    private CtTypeReference getPrimitiveFromObject(CtTypeReference ref) {
        switch (ref.getSimpleName()) {
            case "Boolean" : return getFactory().Type().BOOLEAN_PRIMITIVE;
            case "Long" : return getFactory().Type().LONG_PRIMITIVE;
            case "Integer" : return getFactory().Type().INTEGER_PRIMITIVE;
            case "Character" : return getFactory().Type().CHARACTER_PRIMITIVE;
            case "Byte" : return getFactory().Type().BYTE_PRIMITIVE;
            case "Double" : return getFactory().Type().DOUBLE_PRIMITIVE;
            case "Float" : return getFactory().Type().FLOAT_PRIMITIVE;
            default: return getFactory().Type().SHORT_PRIMITIVE;
        }
    }

    @Override
    public void process(CtVariable ctVariable) {
        if (ctVariable.getType().isPrimitive())
            ctVariable.getDefaultExpression().addTypeCast(ctVariable.getType());
        else
            ctVariable.getDefaultExpression().addTypeCast(getPrimitiveFromObject(ctVariable.getType()));
    }

    @Override
    public boolean isToBeProcessed(CtVariable candidate) {
        if (UtilPerturbation.checkIsNotInPerturbatorPackage(candidate))
            return false;

        if (candidate.getDefaultExpression() == null ||
                candidate.getDefaultExpression().getType() == null ||
                !UtilPerturbation.perturbableTypes.contains(candidate.getDefaultExpression().getType().getSimpleName()))
            return false;

        return !candidate.getType().equals(candidate.getDefaultExpression().getType()) && candidate.getDefaultExpression().getTypeCasts().isEmpty();
    }
}
