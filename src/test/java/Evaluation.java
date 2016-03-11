import org.junit.Test;
import spoon.Launcher;
import util.Util;

/**
 * Created by spirals on 03/03/16.
 */
public class Evaluation {

    @Test
    public void processSample() {
        Launcher l = Util.createSpoonWithPerturbationProcessors();

		l.addInputResource("resources/Test.java");

        l.run();
    }

}
