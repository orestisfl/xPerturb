package processor;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import util.Util;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by spirals on 24/03/16.
 */
public class TestProcessEnum {


    @Test
    public void testNotPerturbableEnum() throws Exception {

        Launcher launcher = Util.createSpoonWithPerturbationProcessors();

        launcher.addInputResource("src/test/resources/EnumRes.java");

        launcher.run();

        //Not a single invokation has been introduce
        CtClass enumWithoutPerturbation = (CtClass) launcher.getFactory().Package().getRootPackage().getElements(new NameFilter("EnumRes")).get(0);

        //filtering the super() call
        List<CtInvocation> invocationList = enumWithoutPerturbation.getElements(new TypeFilter<CtInvocation>(CtInvocation.class){
            @Override
            public boolean matches(CtInvocation element) {
                return !element.getExecutable().getSimpleName().equals("<init>");
            }
        });

        assertTrue(invocationList.isEmpty());

    }
}
