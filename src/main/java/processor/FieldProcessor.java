package processor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtField;

/**
 * Created by spirals on 03/03/16.
 */
public class FieldProcessor extends AbstractProcessor<CtField> {

    public void process(CtField field) {
        field.getDefaultExpression().addTypeCast(field.getType());
    }

    @Override
    public boolean isToBeProcessed(CtField candidate) {

        if (!candidate.getType().isPrimitive()) {
            return false;
        }

        if (!(candidate.getDefaultExpression() != null && candidate.getDefaultExpression().getType() != null)) {
            return false;
        }

        if (!candidate.getType().getSimpleName().equals(candidate.getDefaultExpression().getType().getSimpleName()) &&
                candidate.getDefaultExpression().getTypeCasts().isEmpty()) {
            return true;
        }

        return false;
    }
}