package perturbation;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.filter.NameFilter;
import util.Util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by spirals on 22/03/16.
 */
public class TestPerturbationLiteralsVariable {

    @Test
    public void testPerturbation() throws Exception {

        Launcher launcher = Util.createSpoonWithPerturbationProcessors();

        launcher.addInputResource("src/test/resources/SimpleRes.java");

        launcher.run();

        CtClass simpleResWithPerturbation = (CtClass) launcher.getFactory().Package().getRootPackage().getElements(new NameFilter("SimpleRes")).get(0);

        //The pertubation works?
        Util.addPathToClassPath(launcher.getModelBuilder().getBinaryOutputDirectory().toURL());
        URLClassLoader classLoaderWithoutOldFile = Util.removeOldFileFromClassPath((URLClassLoader) ClassLoader.getSystemClassLoader());

        //PerturbationEngine
        Class<?> classPerturbator = classLoaderWithoutOldFile.loadClass("perturbation.PerturbationEngine");
        Object objectPerturbator = classPerturbator.newInstance();
        Method addLocationToPerturb = classPerturbator.getMethod("add",  classLoaderWithoutOldFile.loadClass("perturbation.PerturbationLocation"));
        Method clearLocationToPerturb = classPerturbator.getMethod("reset");

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

        for (int f = 0; f < fields.length; f++) {
            if (fields[f].getName().startsWith("__L")) {
                Object instanceField = fields[f].get(objectUnderTest);
                addLocationToPerturb.invoke(objectPerturbator, instanceField);//Activated the right location
                perturbation = false;
                assertEquals(1, classPerturbator.getMethod("numberOfPerturbationSetOn").invoke(objectPerturbator));
                for (int m = 0; m < methods.length; m++) {
                    if (methods[m].getName().startsWith("_p")) {
                        if (!methods[m].invoke(objectUnderTest).equals(returnWithoutPerturbation.get(methods[m]))) {
                            if (perturbation)
                                assertTrue(false);//One and only one perturbation is activated
                            else
                                perturbation = true;
                        } else
                            assertEquals(returnWithoutPerturbation.get(methods[m]), methods[m].invoke(objectUnderTest));//Others are the same
                    }
                }
                assertTrue(perturbation);//One perturbation is activated
                clearLocationToPerturb.invoke(objectPerturbator);//clean location of perturbation
            }
        }
    }

}
