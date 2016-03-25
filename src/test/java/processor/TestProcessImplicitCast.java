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

import java.net.URLClassLoader;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * Created by spirals on 08/03/16.
 */
public class TestProcessImplicitCast {

    @Test
    public void testCast() throws Exception {

        Launcher launcher = Util.createSpoonWithPerturbationProcessors();

        launcher.addInputResource("src/test/resources/CastRes.java");

        launcher.run();

        CtClass c = (CtClass) launcher.getFactory().Package().getRootPackage().getElements(new NameFilter("CastRes")).get(0);

        CtClass p = (CtClass) launcher.getFactory().Package().getRootPackage().getElements(new NameFilter("Perturbation")).get(0);


        Set<CtMethod> methods = c.getAllMethods();
        for (CtMethod m : methods) {
            List<CtLiteral> elems = m.getElements(new TypeFilter(CtLiteral.class));
            for (CtLiteral elem : elems) {
                if (elem.getParent() instanceof CtConstructorCall && ((CtConstructorCall) elem.getParent()).getExecutable().getType().getSimpleName().equals("PerturbationLocation"))
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

}
