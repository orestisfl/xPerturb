package processor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.support.reflect.code.CtVariableReadImpl;

/**
 * Created by bdanglot on 27/05/16.
 */
public class UnaryOpProcessor extends AbstractProcessor<CtUnaryOperator> {

    @Override
    public void process(CtUnaryOperator ctUnaryOperator) {
        String name = ((CtVariableReadImpl) (ctUnaryOperator).getOperand()).getVariable().getSimpleName();
        String codeSnippet = name + " = " + name;
        switch (ctUnaryOperator.getKind()) {
            case POSTDEC:
                codeSnippet += "-";
                break;
            case POSTINC:
                codeSnippet += "+";
                break;
            case PREINC:
                codeSnippet += "+";
                break;
            case PREDEC:
                codeSnippet += "-";
                break;
            default:
                System.exit(-1);
                break;
        }
        codeSnippet += " 1";
        ctUnaryOperator.replace(getFactory().Code().createCodeSnippetExpression(codeSnippet));
    }

    @Override
    public boolean isToBeProcessed(CtUnaryOperator candidate) {
        return candidate.getKind() == UnaryOperatorKind.POSTDEC ||
                candidate.getKind() == UnaryOperatorKind.POSTINC ||
                candidate.getKind() == UnaryOperatorKind.PREINC ||
                candidate.getKind() == UnaryOperatorKind.PREDEC;
    }

}
