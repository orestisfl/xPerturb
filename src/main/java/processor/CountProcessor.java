package processor;


import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;

/**
 * Created by bdanglot on 12/05/16.
 */
public class CountProcessor extends AbstractProcessor<CtClass> {

    private static int countLine = 0;
    private static int countClass = 0;

    @Override
    public void process(CtClass ctClass) {

        countLine += ctClass.getPosition().getLine();

        countClass++;

    }

    @Override
    public void processingDone() {
        super.processingDone();
        System.out.println(countLine + " Lines");
        System.out.println(countClass + " classes");
        countClass = 0;
        countLine = 0;
    }
}
