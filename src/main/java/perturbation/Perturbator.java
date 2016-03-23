package perturbation;


import perturbation.activator.LocationActivator;
import perturbation.perturbator.RndPerturbatorImpl;

public class Perturbator {

    private static PerturbatorInterface pertubator = new RndPerturbatorImpl();

    private static AbstractActivator activator = new LocationActivator();

    /*
        Setting method
     */
    public static void setPertubator(PerturbatorInterface p) {
        pertubator = p;
    }

    public static void setActivator(AbstractActivator a) {
        activator= a;
    }

    public static void add(PerturbationLocation location) {
        activator.addLocation(location);
    }

    public static boolean remove(PerturbationLocation location) {
        return activator.removeLocation(location);
    }

    public static void reset() {
        activator.reset();
    }

    public static int numberOfPerturbationSetOn() {
        return activator.numberOfPerturbationOn();
    }

    /*
       Perturbation Methods
     */

    public static boolean pboolean(PerturbationLocation perturbationLocation, boolean value) {
        if (activator.shouldBeActivated(perturbationLocation)) {
            boolean perturbation = pertubator.pboolean(value);
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static byte pbyte(PerturbationLocation perturbationLocation, byte value) {
        if (activator.shouldBeActivated(perturbationLocation)) {
            byte perturbation = pertubator.pbyte(value);
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static short pshort(PerturbationLocation perturbationLocation, short value) {
        if (activator.shouldBeActivated(perturbationLocation)) {
            short perturbation = pertubator.pshort(value);
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static int pint(PerturbationLocation perturbationLocation, int value) {
        if (activator.shouldBeActivated(perturbationLocation)) {
            int perturbation = pertubator.pint(value);
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static long plong(PerturbationLocation perturbationLocation, long value) {
        if (activator.shouldBeActivated(perturbationLocation)) {
            long perturbation = pertubator.plong(value);
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static char pchar(PerturbationLocation perturbationLocation, char value) {
        if (activator.shouldBeActivated(perturbationLocation)) {
            char perturbation = pertubator.pchar(value);
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static float pfloat(PerturbationLocation perturbationLocation, float value) {
        if (activator.shouldBeActivated(perturbationLocation)) {
            float perturbation = pertubator.pfloat(value);
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static double pdouble(PerturbationLocation perturbationLocation, double value) {
        if (activator.shouldBeActivated(perturbationLocation)) {
            double perturbation = pertubator.pdouble(value);
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static String print() {
        return pertubator.toString()+"/"+activator.toString();
    }

}
