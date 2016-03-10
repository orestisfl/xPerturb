package processor;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import perturbator.UtilPerturbation;
import spoon.Launcher;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtInvocationImpl;
import util.Util;

import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by spirals on 08/03/16.
 */
public class BinaryOpTest {

    private static CtClass c;
    private static CtClass p;
    private static Launcher launcher;

    @BeforeClass
    public static void setUp() {
        launcher = Util.createSpoon();

        launcher.addProcessor(new PertBinOpProcessor());

        launcher.addInputResource("src/main/java/perturbator/Perturbator.java");
        launcher.addInputResource("src/test/resources/BinOpRes.java");
        launcher.run();

        c = (CtClass) launcher.getFactory().Package().getRootPackage().getElements(new NameFilter("BinOpRes")).get(0);
        p = (CtClass) launcher.getFactory().Package().getRootPackage().getElements(new NameFilter("Perturbator")).get(0);

//        System.out.println(c);
    }

    @Test
    public void testBinOp() throws Exception {
        List<CtBinaryOperator> binaryOperators = c.getElements(new TypeFilter<>(CtBinaryOperator.class));
        for (CtBinaryOperator aBinaryOperator : binaryOperators) {
            //parent is invokation
            assertTrue(aBinaryOperator.getParent() instanceof CtInvocation);
            //this invokation come from pertubator
            assertTrue(((CtInvocationImpl) aBinaryOperator.getParent()).getExecutable().getDeclaringType().equals(p.getReference()));
        }
    }

    @Test
    public void testBinOpReturn() throws Exception {
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

        assertEquals(false, aClass.getMethod("or", boolean.class, boolean.class).invoke(o,true,false));
        assertEquals(false, aClass.getMethod("or", boolean.class, boolean.class).invoke(o,false,true));
        assertEquals(false, aClass.getMethod("or", boolean.class, boolean.class).invoke(o,true,true));
        assertEquals(true, aClass.getMethod("or", boolean.class, boolean.class).invoke(o,false,false));

        assertEquals(true, aClass.getMethod("and", boolean.class, boolean.class).invoke(o,false,false));
        assertEquals(true, aClass.getMethod("and", boolean.class, boolean.class).invoke(o,true,false));
        assertEquals(true, aClass.getMethod("and", boolean.class, boolean.class).invoke(o,false,true));
        assertEquals(false, aClass.getMethod("and", boolean.class, boolean.class).invoke(o,true,true));

        assertFalse(2 == (Integer)aClass.getMethod("plus", int.class, int.class).invoke(o,1,1));
        assertFalse(0 == (Integer)aClass.getMethod("minus", int.class, int.class).invoke(o,0,0));
        assertFalse(1 == (Integer)aClass.getMethod("multiply", int.class, int.class).invoke(o,1,1));
        assertFalse(1 == (Integer)aClass.getMethod("divide", int.class, int.class).invoke(o,1,1));
        assertFalse(0 == (Integer)aClass.getMethod("modulo", int.class, int.class).invoke(o,1,1));

        l.clear();
    }

    @AfterClass
    public static void close(){
        UtilPerturbation.reset();
    }
}
