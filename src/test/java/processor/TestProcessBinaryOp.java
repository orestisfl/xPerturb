package processor;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtInvocationImpl;
import util.Util;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by spirals on 08/03/16.
 */
public class TestProcessBinaryOp {

    @Test
    public void testIntroductionOfPerturbation() throws Exception {

        Launcher launcher = Util.createSpoonWithPerturbationProcessors();

        launcher.addInputResource("src/test/resources/BinOpRes.java");
        launcher.run();

        CtClass binOpWithPerturbation = (CtClass) launcher.getFactory().Package().getRootPackage().getElements(new NameFilter("BinOpRes")).get(0);
        CtClass perturbator = (CtClass) launcher.getFactory().Package().getRootPackage().getElements(new NameFilter("PerturbationEngine")).get(0);

        List<CtBinaryOperator> binaryOperators = binOpWithPerturbation.getElements(new TypeFilter<>(CtBinaryOperator.class));

        for (CtBinaryOperator aBinaryOperator : binaryOperators) {
            //parent is invokation
            assertTrue(aBinaryOperator.getParent() instanceof CtInvocation);
            //this invokation come from pertubator
            assertTrue(((CtInvocationImpl) aBinaryOperator.getParent()).getExecutable().getDeclaringType().equals(perturbator.getReference()));
        }
    }

}
