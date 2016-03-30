package perturbation;


import perturbation.enactor.Enactor;
import perturbation.enactor.LocationEnactorImpl;
import perturbation.location.PerturbationLocation;
import perturbation.log.Logger;

public class PerturbationEngine {

    private static Enactor enactor = new LocationEnactorImpl();

    /*
        Setting method
     */
    public static void setEnactor(Enactor a) {
        enactor = a;
    }

    public static void addLocationToPerturb(PerturbationLocation location) {
        location.setEnaction(true);
    }

    public static void removeLocationToPerturb(PerturbationLocation location) {
        location.setEnaction(false);
    }

    /*
       PerturbationEngine Methods
     */
    public static boolean pboolean(PerturbationLocation perturbationLocation, boolean value) {
        Logger.numberOfCalls++;
        if (enactor.shouldBeActivated(perturbationLocation)) {
            Logger.numberOfEnaction++;
            boolean perturbation = perturbationLocation.getPerturbator().pboolean(value);
            return perturbation;
        } else
            return value;
    }

    public static byte pbyte(PerturbationLocation perturbationLocation, byte value) {
        Logger.numberOfCalls++;
        if (enactor.shouldBeActivated(perturbationLocation)) {
            Logger.numberOfEnaction++;
            byte perturbation = perturbationLocation.getPerturbator().pbyte(value);
            return perturbation;
        } else
            return value;
    }

    public static short pshort(PerturbationLocation perturbationLocation, short value) {
        Logger.numberOfCalls++;
        if (enactor.shouldBeActivated(perturbationLocation)) {
            Logger.numberOfEnaction++;
            short perturbation = perturbationLocation.getPerturbator().pshort(value);
            return perturbation;
        } else
            return value;
    }

    public static int pint(PerturbationLocation perturbationLocation, int value) {
        Logger.numberOfCalls++;
        if (enactor.shouldBeActivated(perturbationLocation)) {
            Logger.numberOfEnaction++;
            int perturbation = perturbationLocation.getPerturbator().pint(value);
            return perturbation;
        } else
            return value;
    }

    public static long plong(PerturbationLocation perturbationLocation, long value) {
        Logger.numberOfCalls++;
        if (enactor.shouldBeActivated(perturbationLocation)) {
            Logger.numberOfEnaction++;
            long perturbation = perturbationLocation.getPerturbator().plong(value);
            return perturbation;
        } else
            return value;
    }

    public static char pchar(PerturbationLocation perturbationLocation, char value) {
        Logger.numberOfCalls++;
        if (enactor.shouldBeActivated(perturbationLocation)) {
            Logger.numberOfEnaction++;
            char perturbation = perturbationLocation.getPerturbator().pchar(value);
            return perturbation;
        } else
            return value;
    }

    public static float pfloat(PerturbationLocation perturbationLocation, float value) {
        Logger.numberOfCalls++;
        if (enactor.shouldBeActivated(perturbationLocation)) {
            Logger.numberOfEnaction++;
            float perturbation = perturbationLocation.getPerturbator().pfloat(value);
            return perturbation;
        } else
            return value;
    }

    public static double pdouble(PerturbationLocation perturbationLocation, double value) {
        Logger.numberOfCalls++;
        if (enactor.shouldBeActivated(perturbationLocation)) {
            Logger.numberOfEnaction++;
            double perturbation = perturbationLocation.getPerturbator().pdouble(value);
            return perturbation;
        } else
            return value;
    }

}
