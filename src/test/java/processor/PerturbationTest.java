package processor;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtInvocationImpl;
import util.Util;

import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by spirals on 07/03/16.
 */
public class PerturbationTest {

    private static Launcher launcher = null;
    private static CtClass perturbator = null;
    private static CtClass simpleResWithPerturbation = null;

    private static void intialisationTest() {
        launcher = Util.createSpoonWithPerturbationProcessors();

        launcher.addInputResource("src/test/resources/SimpleRes.java");
        launcher.run();

        simpleResWithPerturbation = (CtClass) launcher.getFactory().Package().getRootPackage().getElements(new NameFilter("SimpleRes")).get(0);
        perturbator = (CtClass) launcher.getFactory().Package().getRootPackage().getElements(new NameFilter("Perturbator")).get(0);
    }

    @Test
    public void testIntroductionOfPerturbation() {
        if (launcher == null)
            intialisationTest();

        Set<CtMethod> methods = simpleResWithPerturbation.getAllMethods();

        for (CtMethod m : methods) {
            List<CtLiteral> elems = m.getElements(new TypeFilter(CtLiteral.class));
            for (CtLiteral elem : elems) {
                if (elem.getParent() instanceof CtConstructorCall && ((CtConstructorCall) elem.getParent()).getExecutable().getType().getSimpleName().equals("Location"))
                    continue;// we skip lit introduce by the perturbation
                //parent is invokation
                assertTrue(elem.getParent() instanceof CtInvocation);
                //this invokation come from perturbator
                assertTrue(((CtInvocationImpl) elem.getParent()).getExecutable().getDeclaringType().equals(perturbator.getReference()));
            }
        }
    }

    @Test
    public void testPerturbaion() throws Exception {
        if (launcher == null)
            intialisationTest();

//        Launcher launcher = Util.createSpoonWithPerturbationProcessors();
//
//        launcher.addInputResource("src/test/resources/SimpleRes.java");
//        launcher.run();
//
//        CtClass simpleResWithPerturbation = (CtClass) launcher.getFactory().Package().getRootPackage().getElements(new NameFilter("SimpleRes")).get(0);
//        CtClass perturbator = (CtClass) launcher.getFactory().Package().getRootPackage().getElements(new NameFilter("Perturbator")).get(0);

        //The pertubation works?
        Util.addPathToClassPath(launcher.getModelBuilder().getBinaryOutputDirectory().toURL());
        URLClassLoader classLoaderWithoutOldFile = Util.removeOldFileFromClassPath((URLClassLoader) ClassLoader.getSystemClassLoader());

        //Perturbator
        Class<?> classPerturbator = classLoaderWithoutOldFile.loadClass("perturbator.Perturbator");
        Object objectPerturbator = classPerturbator.newInstance();
        Method addLocationToPerturb = classPerturbator.getMethod("add", Integer.class);
        Method clearLocationToPerturb = classPerturbator.getMethod("clear");

        //Locations
        Class<?> classLocation = classLoaderWithoutOldFile.loadClass("perturbator.Location");
        Object objectLocation = classLocation.newInstance();
        Method getLocation = classLocation.getMethod("getLocation", int.class);
        int numberOfLocation = (Integer)classLocation.getMethod("numberOfLocation").invoke(objectLocation);

        System.out.println("number of Location perturbable : " + numberOfLocation);

        //SimpleRes Class Under Test
        Class<?> classUnderTest = classLoaderWithoutOldFile.loadClass(simpleResWithPerturbation.getQualifiedName());
        Object objectUnderTest = classUnderTest.newInstance();

        assertEquals(0, classPerturbator.getMethod("numberOfPerturbationSetOn").invoke(objectPerturbator));

        Map<String,Object> returnWithoutPerturbation = new HashMap<>();
        for (int i = 0 ; i < numberOfLocation ; i++) {
            String methodName = ((String) getLocation.invoke(objectLocation, i)).split(":")[1];
            returnWithoutPerturbation.put(methodName,classUnderTest.getMethod(methodName).invoke(objectUnderTest));
        }

        for (int i = 0 ; i < numberOfLocation ; i++) {
            String methodNameUnderPerturbation = ((String) getLocation.invoke(objectLocation, i)).split(":")[1];
            addLocationToPerturb.invoke(objectPerturbator, i);

            //Perturbation is done here
            assertNotEquals(returnWithoutPerturbation.get(methodNameUnderPerturbation),
                    classUnderTest.getMethod(methodNameUnderPerturbation).invoke(objectUnderTest));

            //But not for all other method
            for (int j = 0 ; j < numberOfLocation ; j++) {
                String methodNameNotPerturbed =  ((String) getLocation.invoke(objectLocation, j)).split(":")[1];
                if (!methodNameUnderPerturbation.equals(methodNameNotPerturbed))
                    assertEquals(returnWithoutPerturbation.get(methodNameNotPerturbed),
                            classUnderTest.getMethod(methodNameNotPerturbed).invoke(objectUnderTest));
            }

            clearLocationToPerturb.invoke(objectPerturbator);

        }
    }

}
