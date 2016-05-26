package processor;

import org.junit.Test;
import perturbation.PerturbationEngine;
import spoon.Launcher;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtInvocationImpl;
import spoon.support.reflect.reference.CtTypeReferenceImpl;
import util.Util;

import java.math.BigInteger;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by bdanglot on 26/05/16.
 */
public class TestBigInteger {



    @Test
    public void testBigInteger() throws Exception {

        Launcher launcher = Util.createSpoonWithPerturbationProcessors();

        launcher.addInputResource("src/test/resources/BigIntegerRes.java");

        launcher.run();

        CtClass c = (CtClass) launcher.getFactory().Package().getRootPackage().getElements(new NameFilter("BigIntegerRes")).get(0);
        CtClass perturbator = (CtClass) launcher.getFactory().Package().getRootPackage().getElements(new NameFilter("PerturbationEngine")).get(0);

        CtTypeReference type = launcher.getFactory().Type().createReference(launcher.getFactory().Class().get(BigInteger.class));

        List<CtVariableRead> expressionList = c.getElements(new TypeFilter<>(CtVariableRead.class));
        for (CtVariableRead expression : expressionList) {
            if (type.equals(expression.getType())) {
                assertTrue(expression.getParent() instanceof CtInvocation);
                assertTrue(((CtInvocationImpl) expression.getParent()).getExecutable().getDeclaringType().equals(perturbator.getReference()));
            }
        }
    }

}
