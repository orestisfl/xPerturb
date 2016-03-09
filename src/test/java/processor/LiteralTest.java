package processor;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtInvocationImpl;
import util.Util;

import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by spirals on 07/03/16.
 */
public class LiteralTest {

    /**
     * Test of perturbation of Literals
     */
    private static CtClass c;
    private static CtClass p;
    private static Launcher launcher;

    @BeforeClass
    public static void setUp() {
        launcher = Util.createSpoon();

        launcher.addProcessor(new PertLitProcessor());

        launcher.addInputResource("src/test/resources/SimpleRes.java");
        launcher.run();

        c = (CtClass) launcher.getFactory().Package().getRootPackage().getElements(new NameFilter("SimpleRes")).get(0);
        p = (CtClass) launcher.getFactory().Package().getRootPackage().getElements(new NameFilter("Perturbator")).get(0);

//        System.out.println(c);
    }

    /**
     * Test if we introduce perturbation well
     */
    @Test
    public void testSpoon() {
        Set<CtMethod> methods = c.getAllMethods();

        for (CtMethod m : methods) {
            List<CtLiteral> elems = m.getElements(new TypeFilter(CtLiteral.class));
            for (CtLiteral elem : elems) {
                //parent is invokation
                assertTrue(elem.getParent() instanceof CtInvocation);
                //this invokation come from pertubator
                assertTrue(((CtInvocationImpl) elem.getParent()).getExecutable().getDeclaringType().equals(p.getReference()));
            }
        }
    }

    /**
     * Test perturbation for each type perturbed
     */
    @Test
    public void testPerturbation() throws Exception{
        //The pertubation works?
        Util.addPathToClassPath(launcher.getModelBuilder().getBinaryOutputDirectory().toURL());
        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<?> pClass = sysloader.loadClass(p.getQualifiedName());

        //We activate all pertubation with -1 in the list of locations
        Field f = pClass.getDeclaredField("l");
        f.setAccessible(true);
        List l = (List) f.get(null);
        l.add(-1);

        //Laoding the class LiteralRessource
        Class<?> aClass = sysloader.loadClass(c.getQualifiedName());
        Object o = aClass.newInstance();

        //Testing all output's method
        assertTrue(((char)(Util.execMethod("Char", aClass, o)) != '0'));
        assertTrue(((char)(Util.execMethod("character", aClass, o)) != '0'));
        assertTrue(((char)(Util.execMethod("charactercstr", aClass, o)) != '0'));

        assertFalse((boolean)(Util.execMethod("Boolean", aClass, o)));
        assertFalse((boolean)(Util.execMethod("bool", aClass, o)));
        assertFalse((boolean)(Util.execMethod("boolcstr", aClass, o)));

        assertTrue(((int)(Util.execMethod("Int", aClass, o)) != 0));
        assertTrue(((int)(Util.execMethod("integer", aClass, o)) != 0));
        assertTrue(((int)(Util.execMethod("integercstr", aClass, o)) != 0));

        assertTrue(((byte)(Util.execMethod("Byte", aClass, o)) != 0));
        assertTrue(((byte)(Util.execMethod("byteo", aClass, o)) != 0));
        assertTrue(((byte)(Util.execMethod("byteocstr", aClass, o)) != 0));

        assertTrue(((short)(Util.execMethod("Short", aClass, o)) != 0));
        assertTrue(((short)(Util.execMethod("shorto", aClass, o)) != 0));
        assertTrue(((short)(Util.execMethod("shortocstr", aClass, o)) != 0));

        assertTrue(((long)(Util.execMethod("Long", aClass, o)) != 0));
        assertTrue(((long)(Util.execMethod("longo", aClass, o)) != 0));
        assertTrue(((long)(Util.execMethod("longocstr", aClass, o)) != 0));

        assertTrue(((float)(Util.execMethod("Float", aClass, o)) != 0));
        assertTrue(((float)(Util.execMethod("floato", aClass, o)) != 0));
        assertTrue(((float)(Util.execMethod("floatocstr", aClass, o)) != 0));

        assertTrue(((double)(Util.execMethod("Double", aClass, o)) != 0));
        assertTrue(((double)(Util.execMethod("doubleo", aClass, o)) != 0));
        assertTrue(((double)(Util.execMethod("doubleocstr", aClass, o)) != 0));

        //remove -1 from location
        l.clear();
    }

    /**
     * Test on specific location
     */
    @Test
    public void testLocation() throws Exception {
        //The pertubation works?
        Util.addPathToClassPath(launcher.getModelBuilder().getBinaryOutputDirectory().toURL());
        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<?> pClass = sysloader.loadClass(p.getQualifiedName());

        //We activate all pertubation with -1 in the list of locations
        Field f = pClass.getDeclaredField("l");
        f.setAccessible(true);
        List l = (List) f.get(null);
        //put 1 in the list of location
        l.add(1);

        //Loadding the class LiteralRessource
        Class<?> aClass = sysloader.loadClass(c.getQualifiedName());
        Object o = aClass.newInstance();

        //Location of the pertubation 1
        assertFalse((boolean)(Util.execMethod("Boolean", aClass, o)));

        //Test if the others aren't pertubed
        //Testing all output's method
        assertFalse(((char)(Util.execMethod("Char", aClass, o)) != '0'));
        assertFalse(((char)(Util.execMethod("character", aClass, o)) != '0'));
        assertFalse(((char)(Util.execMethod("charactercstr", aClass, o)) != '0'));

        assertTrue((boolean)(Util.execMethod("bool", aClass, o)));
        assertTrue((boolean)(Util.execMethod("boolcstr", aClass, o)));

        assertFalse(((int)(Util.execMethod("Int", aClass, o)) != 0));
        assertFalse(((int)(Util.execMethod("integer", aClass, o)) != 0));
        assertFalse(((int)(Util.execMethod("integercstr", aClass, o)) != 0));

        assertFalse(((byte)(Util.execMethod("Byte", aClass, o)) != 0));
        assertFalse(((byte)(Util.execMethod("byteo", aClass, o)) != 0));
        assertFalse(((byte)(Util.execMethod("byteocstr", aClass, o)) != 0));

        assertFalse(((short)(Util.execMethod("Short", aClass, o)) != 0));
        assertFalse(((short)(Util.execMethod("shorto", aClass, o)) != 0));
        assertFalse(((short)(Util.execMethod("shortocstr", aClass, o)) != 0));

        assertFalse(((long)(Util.execMethod("Long", aClass, o)) != 0));
        assertFalse(((long)(Util.execMethod("longo", aClass, o)) != 0));
        assertFalse(((long)(Util.execMethod("longocstr", aClass, o)) != 0));

        assertFalse(((float)(Util.execMethod("Float", aClass, o)) != 0));
        assertFalse(((float)(Util.execMethod("floato", aClass, o)) != 0));
        assertFalse(((float)(Util.execMethod("floatocstr", aClass, o)) != 0));

        assertFalse(((double)(Util.execMethod("Double", aClass, o)) != 0));
        assertFalse(((double)(Util.execMethod("doubleo", aClass, o)) != 0));
        assertFalse(((double)(Util.execMethod("doubleocstr", aClass, o)) != 0));

        l.clear();
    }

    @AfterClass
    public static void close(){
        PertProcessor.reset();
    }


}
