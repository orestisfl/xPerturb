package processor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;


/**
 * Created by spirals on 19/04/16.
 */
public class RenameProcessor extends AbstractProcessor<CtClass> {

    @Override
    public void process(CtClass ctClass) {
        String newName = ctClass.getSimpleName() + "Instr";
        List<CtTypeReference<?>> references = ctClass.getElements(new TypeFilter<CtTypeReference<?>>(CtTypeReference.class) {
            @Override
            public boolean matches(CtTypeReference<?> element) {
                return super.matches(element) && element.getQualifiedName().equals(ctClass.getQualifiedName());
            }
        });

        for (CtTypeReference<?> reference : references) {
            reference.setSimpleName(newName);
        }

        ctClass.setSimpleName(newName);
    }

    @Override
    public boolean isToBeProcessed(CtClass candidate) {
        return !(UtilPerturbation.isInPerturbatationPackage(candidate)) && candidate.isTopLevel();
    }
}
