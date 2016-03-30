package perturbation.enactor;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.filter.NameFilter;
import util.Util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URLClassLoader;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by spirals on 29/03/16.
 */
public class TestEnactor {

    private static Launcher launcher = null;

    private static URLClassLoader classLoaderWithoutOldFile;
    private static CtClass simpleResWithPerturbation;
    private static Class<?> classPerturbator;
    private static Object objectPerturbator;
    private static Method addLocationToPerturb;
    private static Method removeLocationToPerturb;
    private static Method setEnactor;

    private static Class<?> classUnderTest;
    private static Object objectUnderTest;
    private static Method booleanMethodOfClassUnderTest;


    private static void initialisation() throws Exception {
        launcher = Util.createSpoonWithPerturbationProcessors();

        launcher.addInputResource("src/test/resources/SimpleRes.java");

        launcher.run();

        simpleResWithPerturbation = (CtClass) launcher.getFactory().Package().getRootPackage().getElements(new NameFilter("SimpleRes")).get(0);

        Util.addPathToClassPath(launcher.getModelBuilder().getBinaryOutputDirectory().toURL());
        classLoaderWithoutOldFile = Util.removeOldFileFromClassPath((URLClassLoader) ClassLoader.getSystemClassLoader());

        //PerturbationEngine
        classPerturbator = classLoaderWithoutOldFile.loadClass("perturbation.PerturbationEngine");
        objectPerturbator = classPerturbator.newInstance();
        addLocationToPerturb = classPerturbator.getMethod("addLocationToPerturb", classLoaderWithoutOldFile.loadClass("perturbation.location.PerturbationLocation"));
        removeLocationToPerturb = classPerturbator.getMethod("removeLocationToPerturb", classLoaderWithoutOldFile.loadClass("perturbation.location.PerturbationLocation"));
        setEnactor = classPerturbator.getMethod("setEnactor", classLoaderWithoutOldFile.loadClass("perturbation.enactor.Enactor"));

        classUnderTest = classLoaderWithoutOldFile.loadClass(simpleResWithPerturbation.getQualifiedName());
        objectUnderTest = classUnderTest.newInstance();
        booleanMethodOfClassUnderTest = classUnderTest.getMethod("_pBoolean");

    }


    @Test
    public void testLocationEnactor() throws Exception {

        //test the configuration of the LocationEnactorImpl which is enact the perturbtion if the PerturbationLocationImpl is in his list.

        if (launcher == null)
            initialisation();

        //Setting Enactor Location
        setEnactor.invoke(objectPerturbator, classLoaderWithoutOldFile.loadClass("perturbation.enactor.LocationEnactorImpl").newInstance());

        //shoudln't be perturb because the list of PerturbationLocationImpl is empty
        assertEquals(true, (Boolean)booleanMethodOfClassUnderTest.invoke(objectUnderTest));

        addLocationToPerturb.invoke(objectPerturbator, classUnderTest.getFields()[0].get(null));

        //now it is perturbed
        assertEquals(false, (Boolean)booleanMethodOfClassUnderTest.invoke(objectUnderTest));

        removeLocationToPerturb.invoke(objectPerturbator, classUnderTest.getFields()[0].get(null));
    }

    @Test
    public void testAlwaysEnactor() throws Exception {
        if (launcher == null)
            initialisation();

        //test the always Enactor which always enact perturbation
        setEnactor.invoke(objectPerturbator, classLoaderWithoutOldFile.loadClass("perturbation.enactor.AlwaysEnactorImpl").newInstance());

        //perturb while no PerturbationLocationImpl has been added to the list : 2 perturbations in a row : !!(true) == true
        assertEquals(true,(Boolean)booleanMethodOfClassUnderTest.invoke(objectUnderTest));

        addLocationToPerturb.invoke(objectPerturbator, classUnderTest.getFields()[0].get(null));

        assertEquals(true,(Boolean)booleanMethodOfClassUnderTest.invoke(objectUnderTest));
    }

    @Test
    public void testRandomLocationEnactor() throws Exception {
        if (launcher == null)
            initialisation();

        //test the Random Enactor which means that is enact the perturbation with a probability epsilon

        //Setting Enactor NTime with Location as decorated Enactor
        Constructor constructorOfRandomEnactor = classLoaderWithoutOldFile.loadClass("perturbation.enactor.RandomEnactorImpl").getConstructor(
                classLoaderWithoutOldFile.loadClass("perturbation.enactor.Enactor"), float.class
        );

        Object OneForTwoPerturbationEnactor = constructorOfRandomEnactor.newInstance(
                classLoaderWithoutOldFile.loadClass("perturbation.enactor.LocationEnactorImpl").newInstance(), 0.5f
        );

        setEnactor.invoke(objectPerturbator, OneForTwoPerturbationEnactor);

        //The perturbation will occurs 1 time on two (50%)
        addLocationToPerturb.invoke(objectPerturbator, classUnderTest.getFields()[0].get(null));

        int cptPerturb = 0;
        int cptNotPerturb = 0;

        int numberOfOccurences = 10000;
        double e = 0.05;
        double zScore = 1.960;//95% confidence
        double nZero = (Math.pow(zScore, 2)  * 0.25) / (Math.pow(e, 2));

        int numberOfOccurrencesToBeAnalyzed = (int)Math.round(nZero / (1+(nZero-1)/numberOfOccurences));

        for (int i = 0 ; i < numberOfOccurrencesToBeAnalyzed ; i++) {
            if ((Boolean)booleanMethodOfClassUnderTest.invoke(objectUnderTest))
                cptNotPerturb++;
            else
                cptPerturb++;
        }

        assertTrue(numberOfOccurences*e >= Math.abs(cptNotPerturb - cptPerturb));

        removeLocationToPerturb.invoke(objectPerturbator, classUnderTest.getFields()[0].get(null));
    }

    @Test
    public void testNTimeLocationEnactor() throws Exception {

        if (launcher == null)
            initialisation();

        //test the NTime Enactor which means that is enact n time the perturbation at the given location

        //Setting Enactor NTime with Location as decorated Enactor
        Constructor constructorOfNTimeEnactor = classLoaderWithoutOldFile.loadClass("perturbation.enactor.NTimeEnactorImpl").getConstructor(int.class);

        Object FiveTimeEnactorWithLocationEnactorDecorated = constructorOfNTimeEnactor.newInstance(5);

        setEnactor.invoke(objectPerturbator, FiveTimeEnactorWithLocationEnactorDecorated);

        //The perturbation will occurs 5 times
        addLocationToPerturb.invoke(objectPerturbator, classUnderTest.getFields()[0].get(null));

        assertEquals(false, (Boolean)booleanMethodOfClassUnderTest.invoke(objectUnderTest));
        assertEquals(false, (Boolean)booleanMethodOfClassUnderTest.invoke(objectUnderTest));
        assertEquals(false, (Boolean)booleanMethodOfClassUnderTest.invoke(objectUnderTest));
        assertEquals(false, (Boolean)booleanMethodOfClassUnderTest.invoke(objectUnderTest));
        assertEquals(false, (Boolean)booleanMethodOfClassUnderTest.invoke(objectUnderTest));

        //after that, no more perturbation
        assertEquals(true, (Boolean)booleanMethodOfClassUnderTest.invoke(objectUnderTest));

        removeLocationToPerturb.invoke(objectPerturbator, classUnderTest.getFields()[0].get(null));
    }
}
