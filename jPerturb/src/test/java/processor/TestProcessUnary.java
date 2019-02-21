package processor;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtInvocationImpl;
import util.Util;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by bdanglot on 26/05/16.
 */
public class TestProcessUnary {

    @Test
    public void testPerturbationUnary() throws Exception {

        Launcher launcher = Util.createSpoonWithPerturbationProcessors();

        launcher.addInputResource("src/test/resources/Unary.java");
        launcher.run();

        CtClass c = launcher.getFactory().Package().getRootPackage().getElements(new NamedElementFilter<>(CtClass.class, "Unary")).get(0);
        CtClass perturbator = launcher.getFactory().Package().getRootPackage().getElements(new NamedElementFilter<>(CtClass.class, "PerturbationEngine")).get(0);

        List<CtUnaryOperator> unaryOperatorList = c.getElements(new TypeFilter<>(CtUnaryOperator.class));
        for (CtUnaryOperator aUnaryOperator : unaryOperatorList) {
            assertTrue(aUnaryOperator.getParent() instanceof CtInvocation);
            assertTrue(((CtInvocationImpl) aUnaryOperator.getParent()).getExecutable().getDeclaringType().equals(perturbator.getReference()));
        }
    }

}
