package processor;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtInvocationImpl;
import util.Util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by spirals on 07/03/16.
 */
public class PerturbationTest {

    @Test
    public void testIntroductionOfPerturbation() {
        Launcher launcher = Util.createSpoonWithPerturbationProcessors();

        launcher.addInputResource("src/test/resources/SimpleRes.java");

        launcher.run();

        CtClass simpleResWithPerturbation = (CtClass) launcher.getFactory().Package().getRootPackage().getElements(new NameFilter("SimpleRes")).get(0);

        CtClass perturbator = (CtClass) launcher.getFactory().Package().getRootPackage().getElements(new NameFilter("Perturbator")).get(0);

        Set<CtMethod> methods = simpleResWithPerturbation.getAllMethods();

        for (CtMethod m : methods) {
            List<CtLiteral> elems = m.getElements(new TypeFilter(CtLiteral.class));
            for (CtLiteral elem : elems) {
                if (elem.getParent() instanceof CtConstructorCall && ((CtConstructorCall) elem.getParent()).getExecutable().getType().getSimpleName().equals("PerturbationLocation"))
                    continue;// we skip lit introduce by the perturbation
                //parent is invokation
                assertTrue(elem.getParent() instanceof CtInvocation);
                //this invokation come from perturbator
                assertTrue(((CtInvocationImpl) elem.getParent()).getExecutable().getDeclaringType().equals(perturbator.getReference()));
            }
            List<CtReturn> returns = m.getElements(new TypeFilter<>(CtReturn.class));
            for (CtReturn ret : returns) {
                assertTrue(ret.getReturnedExpression() instanceof CtInvocation);
                assertTrue(((CtInvocationImpl) ret.getReturnedExpression()).getExecutable().getDeclaringType().equals(perturbator.getReference()));
            }
        }
    }

    @Test
    public void testPerturbation() throws Exception {

        Launcher launcher = Util.createSpoonWithPerturbationProcessors();

        launcher.addInputResource("src/test/resources/SimpleRes.java");

        launcher.run();

        CtClass simpleResWithPerturbation = (CtClass) launcher.getFactory().Package().getRootPackage().getElements(new NameFilter("SimpleRes")).get(0);

        CtClass perturbator = (CtClass) launcher.getFactory().Package().getRootPackage().getElements(new NameFilter("Perturbator")).get(0);

        //The pertubation works?
        Util.addPathToClassPath(launcher.getModelBuilder().getBinaryOutputDirectory().toURL());
        URLClassLoader classLoaderWithoutOldFile = Util.removeOldFileFromClassPath((URLClassLoader) ClassLoader.getSystemClassLoader());

        //Perturbator
        Class<?> classPerturbator = classLoaderWithoutOldFile.loadClass("perturbator.Perturbator");
        int nbPerturbation = (int) classPerturbator.getField("nbPerturbation").get(null);
        Object objectPerturbator = classPerturbator.newInstance();
        Method addLocationToPerturb = classPerturbator.getMethod("add", Integer.class);
        Method clearLocationToPerturb = classPerturbator.getMethod("clear");

        assertEquals(0, classPerturbator.getMethod("numberOfPerturbationSetOn").invoke(objectPerturbator));

        Class<?> classUnderTest = classLoaderWithoutOldFile.loadClass(simpleResWithPerturbation.getQualifiedName());
        Object objectUnderTest = classUnderTest.newInstance();

        Map<Method, Object> returnWithoutPerturbation = new HashMap<>();
        Method[] methods = classUnderTest.getMethods();

        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().startsWith("_p"))
                returnWithoutPerturbation.put(methods[i], methods[i].invoke(objectUnderTest));
        }

        boolean perturbation;

        Field[] fields = classUnderTest.getFields();//Getting all field Location in the class under Test
        Class<?> classLocation = classLoaderWithoutOldFile.loadClass("perturbator.PerturbationLocation");
        Method getLocationIndex = classLocation.getMethod("getLocationIndex");

        for (int f = 0; f < fields.length; f++) {
            if (fields[f].getName().startsWith("__L")) {
                Object instanceField = fields[f].get(objectUnderTest);
                Integer i = (Integer)  getLocationIndex.invoke(instanceField);
                addLocationToPerturb.invoke(objectPerturbator, i);//Activated the right location
                perturbation = false;
                for (int m = 0; m < methods.length; m++) {
                    if (methods[m].getName().startsWith("_p")) {
                        if (!methods[m].invoke(objectUnderTest).equals(returnWithoutPerturbation.get(methods[m]))) {
                            if (perturbation)
                                assertTrue(false);//One and only one perturbation is activated
                            else
                                perturbation = true;
                        } else
                            assertEquals(returnWithoutPerturbation.get(methods[m]), methods[m].invoke(objectUnderTest));//Others are the same w/o
                    }
                }
                assertTrue(perturbation);//One perturbation is activated
                clearLocationToPerturb.invoke(objectPerturbator);//clean location of perturbation
            }
        }
    }
}
