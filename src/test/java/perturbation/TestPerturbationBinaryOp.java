package perturbation;

import org.junit.Test;
import spoon.Launcher;
import util.Util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLClassLoader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by spirals on 22/03/16.
 */
public class TestPerturbationBinaryOp {

    @Test
    public void testPerturbationOnBinaryOperator() throws Exception {

        Launcher launcher = Util.createSpoonWithPerturbationProcessors();

        launcher.addInputResource("src/test/resources/BinOpRes.java");
        launcher.run();

        //The pertubation works?
        Util.addPathToClassPath(launcher.getModelBuilder().getBinaryOutputDirectory().toURL());
        URLClassLoader classLoaderWithoutOldFile = Util.removeOldFileFromClassPath((URLClassLoader) ClassLoader.getSystemClassLoader());

        Class<?> classPerturbator = classLoaderWithoutOldFile.loadClass("perturbation.PerturbationEngine");
        Object objectPerturbator = classPerturbator.newInstance();
        Method addLocationToPerturb = classPerturbator.getMethod("add",  classLoaderWithoutOldFile.loadClass("perturbation.PerturbationLocation"));
        Method clearLocationToPerturb = classPerturbator.getMethod("reset");

        Class<?> classUnderTest = classLoaderWithoutOldFile.loadClass("BinOpRes");
        Object objectUnderTest = classUnderTest.newInstance();

        Object returnWithoutPerturbOr = classUnderTest.getMethod("and", boolean.class, boolean.class).invoke(objectUnderTest, true, true);
        Object returnWithoutPerturbPlus = classUnderTest.getMethod("plus", int.class, int.class).invoke(objectUnderTest, 1, 1);

        assertEquals(true, returnWithoutPerturbOr);
        assertEquals(2 , returnWithoutPerturbPlus);

        Field[] fieldsOfBinaryOp = classUnderTest.getFields();

        for (int i = 0 ; i < fieldsOfBinaryOp.length ; i++) {
            if (fieldsOfBinaryOp[i].getName().startsWith("__L")) {
                Class instanceField = fieldsOfBinaryOp[i].get(objectUnderTest).getClass();
                String locationInCode = ((String)instanceField.getMethod("getLocationInCode").invoke(fieldsOfBinaryOp[i].get(objectUnderTest)));
                if (locationInCode.endsWith(":4")) {
                    addLocationToPerturb.invoke(objectPerturbator, fieldsOfBinaryOp[i].get(objectUnderTest));
                    assertNotEquals(true, classUnderTest.getMethod("and", boolean.class, boolean.class).invoke(objectUnderTest, true, true));
                } else if (locationInCode.endsWith(":12")) {
                    addLocationToPerturb.invoke(objectPerturbator, fieldsOfBinaryOp[i].get(objectUnderTest));
                    assertNotEquals(2, classUnderTest.getMethod("plus", int.class, int.class).invoke(objectUnderTest, 1, 1));
                }
                clearLocationToPerturb.invoke(objectPerturbator);
            }
        }




    }
}
