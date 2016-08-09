package processor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.reference.CtTypeReference;

public class AssignmentProcessor extends AbstractProcessor<CtAssignment> {

	public void process(CtAssignment assignment) {
		assignment.getAssignment().addTypeCast(assignment.getAssigned().getType());
	}

	@Override
	public boolean isToBeProcessed(CtAssignment candidate) {

		if (UtilPerturbation.isInPerturbatationPackage(candidate))
			return false;

		if (candidate.getType() == null)
			return false;

		if (!candidate.getType().isPrimitive()) {
			return false;
		}

		CtTypeReference typeAssigned = candidate.getAssigned().getType();
		CtTypeReference typeAssignment = candidate.getAssignment().getType();
		if (typeAssigned != null
				&& typeAssignment != null
				&& !typeAssigned.getSimpleName().equals(typeAssignment.getSimpleName())
				&& candidate.getAssignment().getTypeCasts().isEmpty()) {
			return true;
		}

		return false;
	}
}
