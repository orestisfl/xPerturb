package perturbation;

import perturbation.location.PerturbationLocation;
import perturbation.log.Logger;
import perturbation.log.LoggerImpl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerturbationEngine {

    public static Map<String, Logger> loggers = new HashMap<String, Logger>();

    /*
       PerturbationEngine Methods
     */
    public static boolean pboolean(PerturbationLocation perturbationLocation, boolean value) {
        if (perturbationLocation.getEnactor().shouldBeActivated()) {
            notifyEnactionOn(perturbationLocation);
            boolean perturbation = perturbationLocation.getPerturbator().pboolean(value);
            return perturbation;
        }  else {
            notifyCallOn(perturbationLocation);
            return value;
        }
    }

    public static byte pbyte(PerturbationLocation perturbationLocation, byte value) {
        if (perturbationLocation.getEnactor().shouldBeActivated()) {
            notifyEnactionOn(perturbationLocation);
            byte perturbation = perturbationLocation.getPerturbator().pbyte(value);
            return perturbation;
        }  else {
            notifyCallOn(perturbationLocation);
            return value;
        }
    }

    public static short pshort(PerturbationLocation perturbationLocation, short value) {
        if (perturbationLocation.getEnactor().shouldBeActivated()) {
            notifyEnactionOn(perturbationLocation);
            short perturbation = perturbationLocation.getPerturbator().pshort(value);
            return perturbation;
        }  else {
            notifyCallOn(perturbationLocation);
            return value;
        }
    }

    public static int pint(PerturbationLocation perturbationLocation, int value) {
        if (perturbationLocation.getEnactor().shouldBeActivated()) {
            notifyEnactionOn(perturbationLocation);
            int perturbation = perturbationLocation.getPerturbator().pint(value);
            return perturbation;
        }  else {
            notifyCallOn(perturbationLocation);
            return value;
        }
    }

    public static long plong(PerturbationLocation perturbationLocation, long value) {
        if (perturbationLocation.getEnactor().shouldBeActivated()) {
            notifyEnactionOn(perturbationLocation);
            long perturbation = perturbationLocation.getPerturbator().plong(value);
            return perturbation;
        }  else {
            notifyCallOn(perturbationLocation);
            return value;
        }
    }

    public static char pchar(PerturbationLocation perturbationLocation, char value) {
        if (perturbationLocation.getEnactor().shouldBeActivated()) {
            notifyEnactionOn(perturbationLocation);
            char perturbation = perturbationLocation.getPerturbator().pchar(value);
            return perturbation;
        }  else {
            notifyCallOn(perturbationLocation);
            return value;
        }
    }

    public static float pfloat(PerturbationLocation perturbationLocation, float value) {
        if (perturbationLocation.getEnactor().shouldBeActivated()) {
            notifyEnactionOn(perturbationLocation);
            float perturbation = perturbationLocation.getPerturbator().pfloat(value);
            return perturbation;
        } else {
            notifyCallOn(perturbationLocation);
            return value;
        }
    }

    public static double pdouble(PerturbationLocation perturbationLocation, double value) {
        if (perturbationLocation.getEnactor().shouldBeActivated()) {
            notifyEnactionOn(perturbationLocation);
            double perturbation = perturbationLocation.getPerturbator().pdouble(value);
            return perturbation;
        } else {
            notifyCallOn(perturbationLocation);
            return value;
        }
    }

    public static BigInteger pBigInteger(PerturbationLocation perturbationLocation, BigInteger value) {
        if (perturbationLocation.getEnactor().shouldBeActivated()) {
            notifyEnactionOn(perturbationLocation);
            BigInteger perturbation = perturbationLocation.getPerturbator().pBigInteger(value);
            return perturbation;
        }  else {
            notifyCallOn(perturbationLocation);
            return value;
        }
    }

    /*
     * Logging Management
     */
    private static void notifyCallOn(PerturbationLocation location) {
        for (Logger logger : loggers.values()) {
            logger.logCall(location);
        }
    }

    private static void notifyEnactionOn(PerturbationLocation location) {
        for (Logger logger : loggers.values()) {
            logger.logCall(location);
            logger.logEnaction(location);
        }
    }

}
