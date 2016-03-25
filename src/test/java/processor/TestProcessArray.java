package processor;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import util.Util;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by spirals on 23/03/16.
 */
public class TestProcessArray {

    @Test
    public void testArray() throws Exception {

        Launcher launcher = Util.createSpoonWithPerturbationProcessors();

        launcher.addInputResource("src/test/resources/ArrayRes.java");

        launcher.run();

        CtClass arrayResWithPerturbation = (CtClass) launcher.getFactory().Package().getRootPackage().getElements(new NameFilter("ArrayRes")).get(0);

        CtClass perturbator = (CtClass) launcher.getFactory().Package().getRootPackage().getElements(new NameFilter("Perturbation")).get(0);

        //All of the invokation should be perturbation calls or the initialiazer or the super() call
        List<CtInvocation> invokationsOfPerturbations = arrayResWithPerturbation.getElements(new TypeFilter<>(CtInvocation.class));

        for (CtInvocation perturbation : invokationsOfPerturbations) {
            if (perturbation.getExecutable().getDeclaringType().equals(arrayResWithPerturbation.getReference()))
                assertEquals("initPerturbationLocation0", perturbation.getExecutable().getSimpleName());//init location
            else if (perturbation.isImplicit())
                assertEquals("<init>", invokationsOfPerturbations.get(10).getExecutable().getSimpleName());//implicit call of super() from java.lang.Object
            else
                assertTrue(perturbation.getExecutable().getDeclaringType().equals(perturbator.getReference()));//else invokation of perturbation
        }


    }
}
