package processor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtArrayWrite;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.code.CtWhile;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;

/**
 * Created by spirals on 09/03/16.
 */
public class PerturbationProcessor<T extends CtExpression> extends AbstractProcessor<T> {

    @Override
    public void process(CtExpression ctExpression) {

        CtExpression expr = getFactory().Core().clone(ctExpression);

        expr.setParent(ctExpression.getParent());

        CtTypeReference originalTypeReference = ctExpression.getTypeCasts().isEmpty()?ctExpression.getType():(CtTypeReference) ctExpression.getTypeCasts().get(0);

        String perturbedTypeReference = getTypeReferenceFromTypeOfOriginalExpression(originalTypeReference);

        ctExpression.replace(UtilPerturbation.createStaticCallOfPerturbationFunction(getFactory(), perturbedTypeReference, originalTypeReference, expr));

    }

    public String getTypeReferenceFromTypeOfOriginalExpression(CtTypeReference originalType) {
        switch (originalType.getSimpleName().toLowerCase()) {
            case "boolean": return "Boolean";
            default: return "Numerical";
        }
    }

    @Override
    public boolean isToBeProcessed(CtExpression candidate) {

        if (UtilPerturbation.checkIsNotInPerturbatorPackage(candidate)) {
            return false;
        }

        if (candidate.getType() == null) {
            return false;
        }

        if (candidate instanceof CtUnaryOperator) {
            return false;
        }

        //Left-Hand of an assignment can not be perturbed
        if (candidate instanceof CtVariableWrite || candidate instanceof CtAssignment
                || candidate instanceof CtFieldWrite || candidate instanceof CtArrayWrite) {
            return false;
        }

        if (candidate.getParent(CtConstructor.class) != null) {
            if (candidate.getParent(CtConstructor.class).getParent() instanceof CtEnum)
                return false;
        }

        CtType perturbatorClass = getFactory().Type().get(UtilPerturbation.QUALIFIED_NAME_PERTURBATOR);
        CtTypeReference perturbatorReference = perturbatorClass.getReference();

        //An object on which we call a method can not be perturb
        if (candidate.isParentInitialized()) {

            if (candidate.getParent(new TypeFilter<CtField>(CtField.class)) != null) {
                if (candidate.getParent(new TypeFilter<CtField>(CtField.class)).getModifiers().contains(ModifierKind.FINAL))
                return false;
            }

            if (candidate.getParent(new TypeFilter<CtConstructor>(CtConstructor.class)) != null) {
                return false;
            }

            CtElement candidateParent = candidate.getParent();

            //@TODO
            if (candidate instanceof CtInvocation) {
                if (candidateParent instanceof CtBlock)
                    return false;
                if (candidateParent instanceof CtIf && ((CtIf) candidateParent).getCondition() != candidate)
                    return false;
                if (candidateParent instanceof CtFor && ( ((CtFor) candidateParent).getExpression() != candidate ||
                        ((CtFor) candidateParent).getForInit() != candidate ||
                        ((CtFor) candidateParent).getForUpdate() != candidate))
                    return false;

            }

            if (candidateParent instanceof CtInvocation) {
                CtExpression target = ((CtInvocation) candidateParent).getTarget();
                if (target != null && target.equals(candidate) ||
                        ((CtInvocation) candidateParent).getExecutable().getDeclaringType().equals(perturbatorReference)) {
                    return false;
                }
            }

            //Unperturbable case because of java
            if (candidateParent instanceof CtCase || candidateParent.getParent() instanceof CtCase ||
                    (candidateParent instanceof CtField && ((CtField) candidateParent).hasModifier(ModifierKind.FINAL)) ||
                    candidateParent instanceof CtWhile && (candidate instanceof CtLiteral && ((CtLiteral) candidate).getValue().equals(true))) {
                return false;
            }

            if (candidateParent instanceof CtUnaryOperator) {
                UnaryOperatorKind parentKind = ((CtUnaryOperator) candidateParent).getKind();
                if (parentKind != UnaryOperatorKind.NEG && parentKind != UnaryOperatorKind.NOT &&
                        parentKind != UnaryOperatorKind.COMPL && parentKind != UnaryOperatorKind.POS)
                    return false;
            }

            if (candidateParent instanceof CtConstructorCall) {
                if (candidateParent.isParentInitialized() && candidateParent.getParent() instanceof CtEnumValue)
                    return false;
            }

            if (candidateParent instanceof CtNewArray) {
                addCast(candidate, (CtNewArray) candidateParent);
            }

            if (candidateParent instanceof CtBinaryOperator) {
                return checkBrotherCtBinaryOpererator(candidate, (CtBinaryOperator) candidateParent, (CtClass) perturbatorClass);
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
                    if (UtilPerturbation.perturbableTypes.contains(ref.getComponentType().getSimpleName().toLowerCase()))
                        candidate.addTypeCast(ref.getComponentType());
                }
            }
        }
    }

    @Override
    public void processingDone() {
        super.processingDone();
        UtilPerturbation.addAllFieldsAndMethods(getFactory());
    }
}
