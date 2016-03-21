package perturbator;


import java.util.ArrayList;
import java.util.List;

public class Perturbator {

    private static boolean perturbed = false;

    private static boolean oneTime = false;

    private static boolean firstTime = true;

    private static List<Integer> locationsToPerturb = new ArrayList<Integer>();

    private static PerturbatorInterface pertubator = new RndPerturbatorImpl();

    /*
        Setting method
     */
    public static void setOneTime(boolean setOneTime) {
        oneTime = setOneTime;
    }

    public static void setPertubator(PerturbatorInterface p) {
        pertubator = p;
    }

    public static void add(Integer i) {
        locationsToPerturb.add(i);
    }

    public static boolean remove(Integer i) {
        return locationsToPerturb.remove(i);
    }

    public static void reset() {
        locationsToPerturb.clear();
        perturbed = false;
        firstTime = true;
    }

    public static int numberOfPerturbationSetOn() {
        return locationsToPerturb.size();
    }


    /*
       Perturbation Methods
     */
    private static boolean perturbation(PerturbationLocation perturbationLocation) {
        if (firstTime && (locationsToPerturb.contains(-1)|| locationsToPerturb.contains(perturbationLocation.getLocationIndex()))) {
            firstTime = !(oneTime);
            perturbed = true;
            return true;
        } else
            return false;
    }

    public static boolean pboolean(PerturbationLocation perturbationLocation, boolean value) {
        if (perturbation(perturbationLocation)) {
            boolean perturbation = pertubator.pboolean(value);
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static byte pbyte(PerturbationLocation perturbationLocation, byte value) {
        if (perturbation(perturbationLocation)) {
            byte perturbation = pertubator.pbyte(value);
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static short pshort(PerturbationLocation perturbationLocation, short value) {
        if (perturbation(perturbationLocation)) {
            short perturbation = pertubator.pshort(value);
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static int pint(PerturbationLocation perturbationLocation, int value) {
        if (perturbation(perturbationLocation)) {
            int perturbation = pertubator.pint(value);
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static long plong(PerturbationLocation perturbationLocation, long value) {
        if (perturbation(perturbationLocation)) {
            long perturbation = pertubator.plong(value);
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static char pchar(PerturbationLocation perturbationLocation, char value) {
        if (perturbation(perturbationLocation)) {
            char perturbation = pertubator.pchar(value);
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static float pfloat(PerturbationLocation perturbationLocation, float value) {
        if (perturbation(perturbationLocation)) {
            float perturbation = pertubator.pfloat(value);
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

    public static double pdouble(PerturbationLocation perturbationLocation, double value) {
        if (perturbation(perturbationLocation)) {
            double perturbation = pertubator.pdouble(value);
            perturbationLocation.replacement = value + "->" + perturbation;
            return perturbation;
        } else
            return value;
    }

}
