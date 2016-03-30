package perturbation;


import perturbation.enactor.Enactor;
import perturbation.enactor.LocationEnactorImpl;
import perturbation.location.PerturbationLocation;
import perturbation.location.PerturbationLocationImpl;

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
    public static boolean pboolean(PerturbationLocationImpl perturbationLocationImpl, boolean value) {
        perturbationLocationImpl.numberOfCall++;
        if (enactor.shouldBeActivated(perturbationLocationImpl)) {
            boolean perturbation = perturbationLocationImpl.getPerturbator().pboolean(value);
            perturbationLocationImpl.numberOfActivation++;
            perturbationLocationImpl.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static byte pbyte(PerturbationLocationImpl perturbationLocationImpl, byte value) {
        perturbationLocationImpl.numberOfCall++;
        if (enactor.shouldBeActivated(perturbationLocationImpl)) {
            byte perturbation = perturbationLocationImpl.getPerturbator().pbyte(value);
            perturbationLocationImpl.numberOfActivation++;
            perturbationLocationImpl.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static short pshort(PerturbationLocationImpl perturbationLocationImpl, short value) {
        perturbationLocationImpl.numberOfCall++;
        if (enactor.shouldBeActivated(perturbationLocationImpl)) {
            short perturbation = perturbationLocationImpl.getPerturbator().pshort(value);
            perturbationLocationImpl.numberOfActivation++;
            perturbationLocationImpl.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static int pint(PerturbationLocationImpl perturbationLocationImpl, int value) {
        perturbationLocationImpl.numberOfCall++;
        if (enactor.shouldBeActivated(perturbationLocationImpl)) {
            int perturbation = perturbationLocationImpl.getPerturbator().pint(value);
            perturbationLocationImpl.numberOfActivation++;
            perturbationLocationImpl.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static long plong(PerturbationLocationImpl perturbationLocationImpl, long value) {
        perturbationLocationImpl.numberOfCall++;
        if (enactor.shouldBeActivated(perturbationLocationImpl)) {
            long perturbation = perturbationLocationImpl.getPerturbator().plong(value);
            perturbationLocationImpl.numberOfActivation++;
            perturbationLocationImpl.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static char pchar(PerturbationLocationImpl perturbationLocationImpl, char value) {
        perturbationLocationImpl.numberOfCall++;
        if (enactor.shouldBeActivated(perturbationLocationImpl)) {
            char perturbation = perturbationLocationImpl.getPerturbator().pchar(value);
            perturbationLocationImpl.numberOfActivation++;
            perturbationLocationImpl.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static float pfloat(PerturbationLocationImpl perturbationLocationImpl, float value) {
        perturbationLocationImpl.numberOfCall++;
        if (enactor.shouldBeActivated(perturbationLocationImpl)) {
            float perturbation = perturbationLocationImpl.getPerturbator().pfloat(value);
            perturbationLocationImpl.numberOfActivation++;
            perturbationLocationImpl.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static double pdouble(PerturbationLocationImpl perturbationLocationImpl, double value) {
        perturbationLocationImpl.numberOfCall++;
        if (enactor.shouldBeActivated(perturbationLocationImpl)) {
            double perturbation = perturbationLocationImpl.getPerturbator().pdouble(value);
            perturbationLocationImpl.numberOfActivation++;
            perturbationLocationImpl.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

}
