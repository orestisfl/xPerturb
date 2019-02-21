package processor;

import org.junit.Test;
import spoon.Launcher;

import static org.junit.Assert.assertNotEquals;

/**
 * Created by spirals on 19/04/16.
 */
public class TestRenameProcessor {

    @Test
    public void testRename() throws Exception {

        Launcher launcher = new Launcher();
        launcher.addProcessor(new RenameProcessor());
        launcher.addInputResource("src/test/resources/SimpleRes.java");
        launcher.run();

        assertNotEquals(null, launcher.getFactory().Class().get("SimpleResInstr"));

    }
}
