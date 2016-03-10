package processor;

import perturbator.UtilPerturbation;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtArrayWrite;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.code.CtWhile;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtArrayTypeReference;

/**
 * Created by spirals on 09/03/16.
 */
public class PerturbationProcessor extends AbstractProcessor<CtExpression> {

    @Override
    public void process(CtExpression ctExpression) {
        ctExpression.replace(UtilPerturbation.createStaticCall(getFactory(), "p"+UtilPerturbation.function(ctExpression),  getFactory().Core().clone(ctExpression)));
    }

    @Override
    public boolean isToBeProcessed(CtExpression candidate) {

//        if (getFactory().Class().get("perturbator.Perturbator").getElements(new TypeFilter<>(CtExpression.class)).contains(candidate))
        if (UtilPerturbation.checkClass(candidate))//Using a smelly method to replace the condition just above
            return false;

        if (candidate.getType() == null)
            return false;

        if (candidate instanceof CtUnaryOperator)
            return false;

        //Left-Hand of an assignment can not be perturbed
        if (candidate instanceof CtVariableWrite || candidate instanceof CtAssignment
                || candidate instanceof CtFieldWrite || candidate instanceof CtArrayWrite)
            return false;

        //An object on which we call a method can not be perturb
        if (candidate.getParent() instanceof CtInvocation &&
                ((CtInvocation)(candidate.getParent())).getTarget().equals(candidate))
            return false;

        //Unperturbable case because of java
        if (candidate.getParent() instanceof CtCase || candidate.getParent().getParent() instanceof CtCase ||
                (candidate.getParent() instanceof CtField && ((CtField) candidate.getParent()).getModifiers().contains(ModifierKind.FINAL)) ||
                candidate.getParent() instanceof CtWhile && ((CtLiteral)candidate).getValue().equals(true) ||
                candidate.getParent() instanceof CtUnaryOperator &&
                        ((CtUnaryOperator) candidate.getParent()).getKind() != UnaryOperatorKind.NEG &&
                        ((CtUnaryOperator) candidate.getParent()).getKind() != UnaryOperatorKind.NOT &&
                        ((CtUnaryOperator) candidate.getParent()).getKind() != UnaryOperatorKind.COMPL &&
                        ((CtUnaryOperator) candidate.getParent()).getKind() != UnaryOperatorKind.POS)
            return false;

        if (candidate.getParent() instanceof CtNewArray)
            addCast(candidate, (CtNewArray) candidate.getParent());

        //
        if (candidate.getParent() instanceof CtBinaryOperator) {
            if (((CtBinaryOperator) candidate.getParent()).getLeftHandOperand() instanceof CtInvocation ||
            ((CtBinaryOperator) candidate.getParent()).getLeftHandOperand() instanceof CtInvocation)
                return false;
            if (!((CtBinaryOperator) candidate.getParent()).getLeftHandOperand().getType().equals(
                    ((CtBinaryOperator) candidate.getParent()).getRightHandOperand().getType()
            ))
                return false;
        }

        return UtilPerturbation.types.contains(candidate.getType().getSimpleName());
    }

    private static void addCast(CtExpression candidate, CtNewArray parent) {
        if (parent.getDimensionExpressions().size() == 0) {
            if (parent.getType() instanceof CtArrayTypeReference) {
                CtArrayTypeReference ref = (CtArrayTypeReference) (parent.getType());
                if (ref.getComponentType() != candidate.getType() && candidate.getTypeCasts().isEmpty()) {
                    candidate.addTypeCast(ref.getComponentType());
                }
            }
        }
    }
}
