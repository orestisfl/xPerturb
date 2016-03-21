package processor;

import org.junit.Assert;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.support.reflect.code.CtInvocationImpl;
import util.Util;

import static org.junit.Assert.assertEquals;

/**
 * Created by spirals on 21/03/16.
 */
public class InnerClassTess {

    @Test
    public void testNoAnonymousBlockInNotStaticInnerClass() throws Exception {

        Launcher launcherSpoon = Util.createSpoonWithPerturbationProcessors();

        launcherSpoon.addInputResource("src/test/resources/AbstractRes.java");

        launcherSpoon.run();

        CtClass abstractPerturbed = (CtClass) launcherSpoon.getFactory().Package().getRootPackage().getElements(new NameFilter("AbstractRes")).get(0);

        CtClass perturbator = (CtClass) launcherSpoon.getFactory().Package().getRootPackage().getElements(new NameFilter("Perturbator")).get(0);

        CtClass innerClassNotStatic = (CtClass) abstractPerturbed.getNestedType("notStaticInnerClass");

        assertEquals(1, innerClassNotStatic.getFields().size());

        CtField fieldOfInnerClassNotStatic = innerClassNotStatic.getField("value");

        Assert.assertTrue(fieldOfInnerClassNotStatic.getDefaultExpression() instanceof CtInvocation);

        CtInvocation invokationPerturbation = ((CtInvocationImpl) fieldOfInnerClassNotStatic.getDefaultExpression());
        Assert.assertEquals(perturbator.getReference(),invokationPerturbation.getExecutable().getDeclaringType());
        Assert.assertTrue(innerClassNotStatic.getAnonymousExecutables().isEmpty());
        Assert.assertEquals(0, ((CtLiteral)invokationPerturbation.getArguments().get(1)).getValue());

        String nameOfPerturbationLocationInNotStaticInnerClass = invokationPerturbation.getArguments().get(0).toString();
        CtField perturbationLocation = abstractPerturbed.getField(nameOfPerturbationLocationInNotStaticInnerClass);
        Assert.assertTrue(perturbationLocation != null);

    }

    @Test
    public void testStaticInnerClass() throws Exception {

        Launcher launcherSpoon = Util.createSpoonWithPerturbationProcessors();

        launcherSpoon.addInputResource("src/test/resources/AbstractRes.java");

        launcherSpoon.run();

        CtClass abstractPerturbed = (CtClass) launcherSpoon.getFactory().Package().getRootPackage().getElements(new NameFilter("AbstractRes")).get(0);

        CtClass perturbator = (CtClass) launcherSpoon.getFactory().Package().getRootPackage().getElements(new NameFilter("Perturbator")).get(0);

        CtClass innerClassStatic = (CtClass) abstractPerturbed.getNestedType("staticInnerClass");

        Assert.assertEquals(2, innerClassStatic.getFields().size());
        Assert.assertEquals(1, innerClassStatic.getAnonymousExecutables().size());

        CtField fieldOfInnerClassStatic = innerClassStatic.getField("value");
        Assert.assertTrue(fieldOfInnerClassStatic.getDefaultExpression() instanceof CtInvocation);

        CtInvocation invokationPerturbation = ((CtInvocationImpl) fieldOfInnerClassStatic.getDefaultExpression());
        Assert.assertEquals(perturbator.getReference(), invokationPerturbation.getExecutable().getDeclaringType());
        Assert.assertEquals(0, ((CtLiteral)invokationPerturbation.getArguments().get(1)).getValue());

        String nameOfPerturbationLocationInNotStaticInnerClass = invokationPerturbation.getArguments().get(0).toString();
        CtField perturbationLocation = innerClassStatic.getField(nameOfPerturbationLocationInNotStaticInnerClass);
        Assert.assertTrue(perturbationLocation != null);

    }
}
