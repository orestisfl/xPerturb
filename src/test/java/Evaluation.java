import org.junit.Test;
import processor.AssignmentProcessor;
import processor.FieldProcessor;
import processor.LocalVariableProcessor;
import processor.PertBinOpProcessor;
import processor.PertLitProcessor;
import processor.PertVarProcessor;
import spoon.Launcher;

/**
 * Created by spirals on 03/03/16.
 */
public class Evaluation {

    @Test
    public void processSample() {
        Launcher l = createSpoon();

		l.addInputResource("resources/Test.java");

        l.run();
    }

    private Launcher createSpoon() {
        PertLitProcessor lit = new PertLitProcessor();
        PertVarProcessor var = new PertVarProcessor();
        PertBinOpProcessor op = new PertBinOpProcessor();
        AssignmentProcessor assignmentProcessor = new AssignmentProcessor();
        LocalVariableProcessor lvar = new LocalVariableProcessor();
        FieldProcessor fieldProcessor = new FieldProcessor();

        Launcher l = new Launcher();

        l.addProcessor(lvar);
        l.addProcessor(fieldProcessor);
        l.addProcessor(assignmentProcessor);

        l.addProcessor(var);
        l.addProcessor(lit);
        l.addProcessor(op);

        l.getEnvironment().setAutoImports(true);

        l.getEnvironment().setShouldCompile(true);
        l.setSourceOutputDirectory("spooned");
        l.setBinaryOutputDirectory("spooned/bin");
        return l;
    }
}
