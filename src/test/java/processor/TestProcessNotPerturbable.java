package processor;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import util.Util;

import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;

/**
 * Created by spirals on 08/03/16.
 */
public class TestProcessNotPerturbable {

    @Test
    public void testNoPerturbationField() throws Exception {

        Launcher launcher = Util.createSpoonWithPerturbationProcessors();

        launcher.addInputResource("src/test/resources/NotPerturbableRes.java");
        launcher.run();

        CtClass c = (CtClass) launcher.getFactory().Package().getRootPackage().getElements(new NameFilter("NotPerturbableRes")).get(0);

        List<CtField> aFields = c.getFields();
        assertFalse(aFields.get(0).getReference().getDeclaration().getAssignment() instanceof CtInvocation);
    }

    @Test
    public void testNoPerturbationSwitch() throws Exception {

        Launcher launcher = Util.createSpoonWithPerturbationProcessors();

        launcher.addInputResource("src/test/resources/NotPerturbableRes.java");
        launcher.run();

        CtClass c = (CtClass) launcher.getFactory().Package().getRootPackage().getElements(new NameFilter("NotPerturbableRes")).get(0);

        List<CtSwitch> aSwitch = c.getElements(new TypeFilter<>(CtSwitch.class));
        List<CtCase> aCases = aSwitch.get(0).getCases();
        assertEquals(3, aCases.size());
        for (CtCase aCase : aCases) {
            assertFalse(aCase.getCaseExpression() instanceof CtInvocation);
        }
    }

    @Test
    public void testNoPerturbationUnary() throws Exception {

        Launcher launcher = Util.createSpoonWithPerturbationProcessors();

        launcher.addInputResource("src/test/resources/NotPerturbableRes.java");
        launcher.run();

        CtClass c = (CtClass) launcher.getFactory().Package().getRootPackage().getElements(new NameFilter("NotPerturbableRes")).get(0);

        List<CtUnaryOperator> unaryOperatorList = c.getElements(new TypeFilter<>(CtUnaryOperator.class));
        for (CtUnaryOperator aUnaryOperator : unaryOperatorList) {
            assertFalse(aUnaryOperator.getParent() instanceof CtInvocation);
        }
    }

    @Test
    public void testNoPerturbationWhileTrue() throws Exception {

        Launcher launcher = Util.createSpoonWithPerturbationProcessors();

        launcher.addInputResource("src/test/resources/NotPerturbableRes.java");
        launcher.run();

        CtClass c = (CtClass) launcher.getFactory().Package().getRootPackage().getElements(new NameFilter("NotPerturbableRes")).get(0);

        List<CtWhile> whiles = c.getElements(new TypeFilter<>(CtWhile.class));
        assertFalse(whiles.get(0).getLoopingExpression().getParent() instanceof CtInvocation);
    }

}
