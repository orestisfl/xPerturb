package processor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.reference.CtTypeReference;

/**
 * Created by spirals on 03/03/16.
 */
public class LocalVariableProcessor extends AbstractProcessor<CtLocalVariable> {

    @Override
    public void process(CtLocalVariable ctLocalVariable) {
        CtTypeReference ref;
        if (!ctLocalVariable.getType().isPrimitive())
            ref = getPrimitiveFromObject(ctLocalVariable.getType());
        else
            ref = ctLocalVariable.getType();
        ctLocalVariable.getAssignment().addTypeCast(ref);
    }

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
    public boolean isToBeProcessed(CtLocalVariable candidate) {

        if (candidate.getAssignment() == null)
            return false;

        if (candidate.getType() == null)
            return false;

        if (candidate.getAssignment().getType().isPrimitive() && candidate.getAssignment().getTypeCasts().isEmpty())
            return true;

        if (!candidate.getType().isPrimitive() ) {
            return false;
        }

        if (!candidate.getType().getSimpleName().equals(candidate.getAssignment().getType().getSimpleName()) &&
                !candidate.getAssignment().getTypeCasts().contains(candidate.getType())) {
            return true;
        }

        return false;
    }
}
