package processor;

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
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtArrayTypeReference;

/**
 * Created by spirals on 09/03/16.
 */
public class PerturbationProcessor extends AbstractProcessor<CtExpression> {

    @Override
    public void process(CtExpression ctExpression) {
        ctExpression.replace(UtilPerturbation.createStaticCallOfPerturbationFunction(getFactory(),
                "p" + UtilPerturbation.function(ctExpression), getFactory().Core().clone(ctExpression)));
    }

    @Override
    public boolean isToBeProcessed(CtExpression candidate) {

        if (UtilPerturbation.checkIsNotInPerturbatorPackage(candidate))
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
        if (candidate.getParent() != null) {
            if (candidate.getParent() instanceof CtInvocation) {
                if (((CtInvocation) (candidate.getParent())).getTarget() != null &&
                        ((CtInvocation) (candidate.getParent())).getTarget().equals(candidate))
                    return false;
            }

            //Unperturbable case because of java
            if (candidate.getParent() instanceof CtCase || candidate.getParent().getParent() instanceof CtCase ||
                    (candidate.getParent() instanceof CtField && ((CtField) candidate.getParent()).getModifiers().contains(ModifierKind.FINAL)) ||
                    candidate.getParent() instanceof CtWhile && (candidate instanceof CtLiteral && ((CtLiteral) candidate).getValue().equals(true)) ||
                    candidate.getParent() instanceof CtUnaryOperator &&
                            ((CtUnaryOperator) candidate.getParent()).getKind() != UnaryOperatorKind.NEG &&
                            ((CtUnaryOperator) candidate.getParent()).getKind() != UnaryOperatorKind.NOT &&
                            ((CtUnaryOperator) candidate.getParent()).getKind() != UnaryOperatorKind.COMPL &&
                            ((CtUnaryOperator) candidate.getParent()).getKind() != UnaryOperatorKind.POS)
                return false;

            if (candidate.getParent() instanceof CtNewArray)
                addCast(candidate, (CtNewArray) candidate.getParent());

            if (candidate.getParent() instanceof CtBinaryOperator) {
                CtBinaryOperator parent = (CtBinaryOperator) candidate.getParent();
                return checkBrotherCtBinaryOpererator(candidate, parent, (CtClass) getFactory().Class().get("perturbator.Perturbator"));
            }

        }

        return UtilPerturbation.perturbableTypes.contains(candidate.getType().getSimpleName());
    }

    private static boolean checkBrotherCtBinaryOpererator(CtExpression candidate, CtBinaryOperator parent, CtClass p) {
        CtExpression brother = parent.getLeftHandOperand().equals(candidate) ? parent.getRightHandOperand() : parent.getLeftHandOperand();
        if (brother.getType() == null) {
            if (brother instanceof CtInvocation &&
                    ((CtInvocation) brother).getTarget().equals(p))
                return true;
            else
                return UtilPerturbation.perturbableTypes.contains(candidate.getType().getSimpleName());
        }
        return UtilPerturbation.perturbableTypes.contains(brother.getType().getSimpleName()) && UtilPerturbation.perturbableTypes.contains(candidate.getType().getSimpleName());
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
