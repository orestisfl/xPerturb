package perturbator;


import java.util.ArrayList;
import java.util.List;

public class Perturbator {

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

    public static void clear() {
        locationsToPerturb.clear();
    }

    public static int numberOfPerturbationSetOn() {
        return locationsToPerturb.size();
    }


    /*
       Perturbation Methods
     */
    private static boolean perturbation(PerturbationLocation perturbationLocation) {
        if (firstTime && (locationsToPerturb.contains(-1)|| locationsToPerturb.contains(perturbationLocation.locationIndex))) {
            firstTime = !oneTime;
            return true;
        } else
            return false;
    }

    public static boolean pboolean(PerturbationLocation perturbationLocation, boolean value) {
        return (perturbation(perturbationLocation)?pertubator.pboolean(value):value);
    }

    public static byte pbyte(PerturbationLocation perturbationLocation, byte value) {
        return (perturbation(perturbationLocation)?pertubator.pbyte(value):value);
    }

    public static short pshort(PerturbationLocation perturbationLocation, short value) {
        return (perturbation(perturbationLocation)?pertubator.pshort(value):value);
    }

    public static int pint(PerturbationLocation perturbationLocation, int value) {
        return (perturbation(perturbationLocation)?pertubator.pint(value):value);
    }

    public static long plong(PerturbationLocation perturbationLocation, long value) {
        return (perturbation(perturbationLocation)?pertubator.plong(value):value);
    }

    public static char pchar(PerturbationLocation perturbationLocation, char value) {
        return (perturbation(perturbationLocation)?pertubator.pchar(value):value);
    }

    public static float pfloat(PerturbationLocation perturbationLocation, float value) {
        return (perturbation(perturbationLocation)?pertubator.pfloat(value):value);
    }

    public static double pdouble(PerturbationLocation perturbationLocation, double value) {
        return (perturbation(perturbationLocation)?pertubator.pdouble(value):value);
    }

}
