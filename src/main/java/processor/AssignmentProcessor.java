package processor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtAssignment;

public class AssignmentProcessor extends AbstractProcessor<CtAssignment> {

    public void process(CtAssignment assignment) {
        assignment.getAssignment().addTypeCast(assignment.getAssigned().getType());
    }

    @Override
    public boolean isToBeProcessed(CtAssignment candidate) {

        if (!candidate.getType().isPrimitive()) {
            return false;
        }

        if (!candidate.getAssigned().getType().getSimpleName().equals(candidate.getAssignment().getType().getSimpleName()) &&
        candidate.getAssignment().getTypeCasts().isEmpty()) {
            return true;
        }

        return false;
    }
}
