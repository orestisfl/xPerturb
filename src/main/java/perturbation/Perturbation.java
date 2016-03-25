package perturbation;


import perturbation.enactor.AbstractEnactor;
import perturbation.enactor.NTimeLocationEnactor;
import perturbation.perturbator.InvPerturbatorImpl;

public class Perturbation {

    private static perturbation.perturbator.Perturbator pertubator = new InvPerturbatorImpl();

    private static AbstractEnactor enactor = new NTimeLocationEnactor(1);

    /*
        Setting method
     */
    public static void setPerturbator(perturbation.perturbator.Perturbator p) {
        pertubator = p;
    }

    public static void setEnactor(AbstractEnactor a) {
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
       Perturbation Methods
     */


    public static boolean pboolean(PerturbationLocation perturbationLocation, boolean value) {
        if (enactor.shouldBeActivated(perturbationLocation)) {
            boolean perturbation = pertubator.pboolean(value);
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static byte pbyte(PerturbationLocation perturbationLocation, byte value) {
        if (enactor.shouldBeActivated(perturbationLocation)) {
            byte perturbation = pertubator.pbyte(value);
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static short pshort(PerturbationLocation perturbationLocation, short value) {
        if (enactor.shouldBeActivated(perturbationLocation)) {
            short perturbation = pertubator.pshort(value);
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static int pint(PerturbationLocation perturbationLocation, int value) {
        if (enactor.shouldBeActivated(perturbationLocation)) {
            int perturbation = pertubator.pint(value);
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static long plong(PerturbationLocation perturbationLocation, long value) {
        if (enactor.shouldBeActivated(perturbationLocation)) {
            long perturbation = pertubator.plong(value);
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static char pchar(PerturbationLocation perturbationLocation, char value) {
        if (enactor.shouldBeActivated(perturbationLocation)) {
            char perturbation = pertubator.pchar(value);
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static float pfloat(PerturbationLocation perturbationLocation, float value) {
        if (enactor.shouldBeActivated(perturbationLocation)) {
            float perturbation = pertubator.pfloat(value);
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static double pdouble(PerturbationLocation perturbationLocation, double value) {
        if (enactor.shouldBeActivated(perturbationLocation)) {
            double perturbation = pertubator.pdouble(value);
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static String print() {
        return pertubator.toString()+"/"+ enactor.toString();
    }

}
