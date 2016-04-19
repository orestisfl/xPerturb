package processor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;

/**
 * Created by spirals on 19/04/16.
 */
public class RenameProcessor extends AbstractProcessor<CtClass> {

    @Override
    public void process(CtClass ctClass) {
        ctClass.setSimpleName(ctClass.getSimpleName()+"Instr");
    }

    @Override
    public boolean isToBeProcessed(CtClass candidate) {
        return !(UtilPerturbation.checkIsNotInPerturbatorPackage(candidate));
    }
}
