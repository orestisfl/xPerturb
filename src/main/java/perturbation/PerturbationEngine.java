package perturbation;


import perturbation.enactor.Enactor;
import perturbation.enactor.LocationEnactor;
import perturbation.perturbator.InvPerturbatorImpl;

public class PerturbationEngine {

    private static perturbation.perturbator.Perturbator pertubator = new InvPerturbatorImpl();

    private static Enactor enactor = new LocationEnactor();

    /*
        Setting method
     */
    public static void setPerturbator(perturbation.perturbator.Perturbator p) {
        pertubator = p;
    }

    public static void setEnactor(Enactor a) {
        enactor = a;
    }

    public static void add(PerturbationLocation location) {
        enactor.addLocation(location);
    }

    public static boolean remove(PerturbationLocation location) {
        return enactor.removeLocation(location);
    }

    public static void reset() {
        enactor.reset();
    }

    public static int numberOfPerturbationSetOn() {
        return enactor.numberOfPerturbationOn();
    }

    /*
       PerturbationEngine Methods
     */
    public static boolean pboolean(PerturbationLocation perturbationLocation, boolean value) {
        perturbationLocation.numberOfCall++;
        if (enactor.shouldBeActivated(perturbationLocation)) {
            boolean perturbation = pertubator.pboolean(value);
            perturbationLocation.numberOfActivation++;
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static byte pbyte(PerturbationLocation perturbationLocation, byte value) {
        perturbationLocation.numberOfCall++;
        if (enactor.shouldBeActivated(perturbationLocation)) {
            byte perturbation = pertubator.pbyte(value);
            perturbationLocation.numberOfActivation++;
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static short pshort(PerturbationLocation perturbationLocation, short value) {
        perturbationLocation.numberOfCall++;
        if (enactor.shouldBeActivated(perturbationLocation)) {
            short perturbation = pertubator.pshort(value);
            perturbationLocation.numberOfActivation++;
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static int pint(PerturbationLocation perturbationLocation, int value) {
        perturbationLocation.numberOfCall++;
        if (enactor.shouldBeActivated(perturbationLocation)) {
            int perturbation = pertubator.pint(value);
            perturbationLocation.numberOfActivation++;
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static long plong(PerturbationLocation perturbationLocation, long value) {
        perturbationLocation.numberOfCall++;
        if (enactor.shouldBeActivated(perturbationLocation)) {
            long perturbation = pertubator.plong(value);
            perturbationLocation.numberOfActivation++;
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static char pchar(PerturbationLocation perturbationLocation, char value) {
        perturbationLocation.numberOfCall++;
        if (enactor.shouldBeActivated(perturbationLocation)) {
            char perturbation = pertubator.pchar(value);
            perturbationLocation.numberOfActivation++;
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static float pfloat(PerturbationLocation perturbationLocation, float value) {
        perturbationLocation.numberOfCall++;
        if (enactor.shouldBeActivated(perturbationLocation)) {
            float perturbation = pertubator.pfloat(value);
            perturbationLocation.numberOfActivation++;
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static double pdouble(PerturbationLocation perturbationLocation, double value) {
        perturbationLocation.numberOfCall++;
        if (enactor.shouldBeActivated(perturbationLocation)) {
            double perturbation = pertubator.pdouble(value);
            perturbationLocation.numberOfActivation++;
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static String print() {
        return pertubator.toString()+"/"+ enactor.toString();
    }

}
