package processor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtTypeAccessImpl;


/**
 * Created by spirals on 19/04/16.
 */
public class RenameProcessor extends AbstractProcessor<CtClass> {

    @Override
    public void process(CtClass ctClass) {
        String oldName = ctClass.getSimpleName();
        ctClass.setSimpleName(ctClass.getSimpleName()+"Instr");
        CtTypeAccess typeInstrumented = getFactory().Code().createTypeAccess(ctClass.getReference());
        CtThisAccess thisInstrumented = getFactory().Code().createThisAccess(ctClass.getReference());
        for (Object field :  ctClass.getElements(new TypeFilter(CtFieldAccess.class))) {
            if (((CtFieldAccess) field).getVariable().isStatic() &&
                    ((CtTypeAccessImpl)((CtFieldAccess) field).getTarget()).getAccessedType().getSimpleName().equals(oldName))
                ((CtFieldAccess)field).setTarget(typeInstrumented);
            if (((CtFieldAccess) field).getTarget() instanceof CtThisAccess)
                ((CtFieldAccess) field).setTarget(thisInstrumented);
        }

        for (Object method : ctClass.getElements(new TypeFilter(CtInvocation.class))) {
            if (((CtInvocation)method).getExecutable().getDeclaringType() == null)
                continue;
            if (((CtInvocation) method).getExecutable().isStatic() && ((CtInvocation) method).getExecutable().getDeclaringType().getSimpleName().equals(oldName))
                ((CtInvocation) method).setTarget(typeInstrumented);
            if (((CtInvocation) method).getTarget() instanceof CtThisAccess)
                   ((CtInvocation) method).setTarget(thisInstrumented);
        }

    }

    @Override
    public boolean isToBeProcessed(CtClass candidate) {
        return !(UtilPerturbation.checkIsNotInPerturbatorPackage(candidate));
    }
}
