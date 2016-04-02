package perturbation;

import perturbation.location.PerturbationLocation;
import perturbation.log.Logger;
import perturbation.log.LoggerImpl;

public class PerturbationEngine {

    public static Logger logger = new LoggerImpl();

    /*
       PerturbationEngine Methods
     */
    public static boolean pboolean(PerturbationLocation perturbationLocation, boolean value) {
        logger.logCall(perturbationLocation);
        if (perturbationLocation.getEnactor().shouldBeActivated()) {
            logger.logEnaction(perturbationLocation);
            boolean perturbation = perturbationLocation.getPerturbator().pboolean(value);
            return perturbation;
        } else
            return value;
    }

    public static byte pbyte(PerturbationLocation perturbationLocation, byte value) {
        logger.logCall(perturbationLocation);
        if (perturbationLocation.getEnactor().shouldBeActivated()) {
            logger.logEnaction(perturbationLocation);
            byte perturbation = perturbationLocation.getPerturbator().pbyte(value);
            return perturbation;
        } else
            return value;
    }

    public static short pshort(PerturbationLocation perturbationLocation, short value) {
        logger.logCall(perturbationLocation);
        if (perturbationLocation.getEnactor().shouldBeActivated()) {
            logger.logEnaction(perturbationLocation);
            short perturbation = perturbationLocation.getPerturbator().pshort(value);
            return perturbation;
        } else
            return value;
    }

    public static int pint(PerturbationLocation perturbationLocation, int value) {
        logger.logCall(perturbationLocation);
        if (perturbationLocation.getEnactor().shouldBeActivated()) {
            logger.logEnaction(perturbationLocation);
            int perturbation = perturbationLocation.getPerturbator().pint(value);
            return perturbation;
        } else
            return value;
    }

    public static long plong(PerturbationLocation perturbationLocation, long value) {
        logger.logCall(perturbationLocation);
        if (perturbationLocation.getEnactor().shouldBeActivated()) {
            logger.logEnaction(perturbationLocation);
            long perturbation = perturbationLocation.getPerturbator().plong(value);
            return perturbation;
        } else
            return value;
    }

    public static char pchar(PerturbationLocation perturbationLocation, char value) {
        logger.logCall(perturbationLocation);
        if (perturbationLocation.getEnactor().shouldBeActivated()) {
            logger.logEnaction(perturbationLocation);
            char perturbation = perturbationLocation.getPerturbator().pchar(value);
            return perturbation;
        } else
            return value;
    }

    public static float pfloat(PerturbationLocation perturbationLocation, float value) {
        logger.logCall(perturbationLocation);
        if (perturbationLocation.getEnactor().shouldBeActivated()) {
            logger.logEnaction(perturbationLocation);
            float perturbation = perturbationLocation.getPerturbator().pfloat(value);
            return perturbation;
        } else
            return value;
    }

    public static double pdouble(PerturbationLocation perturbationLocation, double value) {
        logger.logCall(perturbationLocation);
        if (perturbationLocation.getEnactor().shouldBeActivated()) {
            logger.logEnaction(perturbationLocation);
            double perturbation = perturbationLocation.getPerturbator().pdouble(value);
            return perturbation;
        } else
            return value;
    }

}
