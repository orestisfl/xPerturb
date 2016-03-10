package processor;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import perturbator.UtilPerturbation;
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

import java.net.URLClassLoader;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * Created by spirals on 08/03/16.
 */
public class ImplicitCastTest {

    /**
     * Test if processor replace implicit cast by explicit
     */
    private static CtClass c;
    private static CtClass p;
    private static Launcher launcher;

    @BeforeClass
    public static void setUp() {
        launcher = Util.createSpoon();

        launcher.addProcessor(new AssignmentProcessor());
        launcher.addProcessor(new LocalVariableProcessor());
        launcher.addProcessor(new FieldProcessor());

        launcher.addProcessor(new PertLitProcessor());

        launcher.addInputResource("src/main/java/perturbator/Perturbator.java");
        launcher.addInputResource("src/test/resources/CastRes.java");
        launcher.run();

        c = (CtClass) launcher.getFactory().Package().getRootPackage().getElements(new NameFilter("CastRes")).get(0);

        p = (CtClass) launcher.getFactory().Package().getRootPackage().getElements(new NameFilter("Perturbator")).get(0);

//        System.out.println(c);
    }

    @Test
    public void testAddPertubation() throws Exception {
        Set<CtMethod> methods = c.getAllMethods();
        for (CtMethod m : methods) {
            List<CtLiteral> elems = m.getElements(new TypeFilter(CtLiteral.class));
            for (CtLiteral elem : elems) {
                if (elem.getParent() instanceof CtConstructorCall && ((CtConstructorCall) elem.getParent()).getExecutable().getType().getSimpleName().equals("Location"))
                    continue;// we skip lit introduce by the perturbation
                //parent is invokation
                assertTrue(elem.getParent() instanceof CtInvocation);
                //this invokation come from pertubator
                assertTrue(((CtInvocationImpl) elem.getParent()).getExecutable().getDeclaringType().equals(p.getReference()));
            }
        }
        //We assume if we can't instanciate the class, something went wrong
        Util.addPathToClassPath(launcher.getModelBuilder().getBinaryOutputDirectory().toURL());
        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<?> CastResClass = sysloader.loadClass(c.getQualifiedName());
        Object CastResInstance = CastResClass.newInstance();
    }

    @AfterClass
    public static void close(){
        UtilPerturbation.reset();
    }

}
