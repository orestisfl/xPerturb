package processor;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import perturbator.UtilPerturbation;
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
public class NotPerturbableTest {

    private static CtClass c;
    private static CtClass p;
    private static Launcher launcher;

    @BeforeClass
    public static void setUp() {
        launcher = Util.createSpoon();

        launcher.addProcessor(new LocalVariableProcessor());
        launcher.addProcessor(new FieldProcessor());
        launcher.addProcessor(new AssignmentProcessor());
        launcher.addProcessor(new PertBinOpProcessor());
        launcher.addProcessor(new PertLitProcessor());
        launcher.addProcessor(new PertVarProcessor());

        launcher.addInputResource("src/main/java/perturbator/Perturbator.java");
        launcher.addInputResource("src/test/resources/NotPerturbableRes.java");
        launcher.run();

        c = (CtClass) launcher.getFactory().Package().getRootPackage().getElements(new NameFilter("NotPerturbableRes")).get(0);
        p = (CtClass) launcher.getFactory().Package().getRootPackage().getElements(new NameFilter("Perturbator")).get(0);

//        System.out.println(c);
    }

    @Test
    public void testNoPertubationField() throws Exception {
        List<CtField> aFields = c.getFields();
        assertEquals(1, aFields.size());
        assertFalse(aFields.get(0).getReference().getDeclaration().getAssignment() instanceof CtInvocation);
    }

    @Test
    public void testNoPerturbationSwitch() throws Exception {
        List<CtSwitch> aSwitch = c.getElements(new TypeFilter<>(CtSwitch.class));
        List<CtCase> aCases = aSwitch.get(0).getCases();
        assertEquals(3, aCases.size());
        for (CtCase aCase : aCases) {
            assertFalse(aCase.getCaseExpression() instanceof CtInvocation);
        }
    }

    @Test
    public void testNoPerturbationUnary() throws Exception {
        List<CtUnaryOperator> unaryOperatorList = c.getElements(new TypeFilter<>(CtUnaryOperator.class));
        for (CtUnaryOperator aUnaryOperator : unaryOperatorList) {
            assertFalse(aUnaryOperator.getParent() instanceof CtInvocation);
        }
    }

    @Test
    public void testNoPertubationWhileTrue() throws Exception {
        List<CtWhile> whiles = c.getElements(new TypeFilter<>(CtWhile.class));
        assertFalse(whiles.get(0).getLoopingExpression().getParent() instanceof CtInvocation);
    }

    @AfterClass
    public static void close(){
        UtilPerturbation.reset();
    }
}
