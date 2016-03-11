package perturbator;


import java.util.ArrayList;
import java.util.List;

public class Perturbator {

    private static boolean oneTime = false;

    private static boolean firstTime = true;

    private static List<Integer> locationsToPerturb = new ArrayList<>();

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
    private static boolean perturbation(int location) {
        if (firstTime && (locationsToPerturb.contains(-1)|| locationsToPerturb.contains(location))) {
            firstTime = !oneTime;
            return true;
        } else
            return false;
    }

    public static boolean pboolean(int location, boolean value) {
        return (perturbation(location)?pertubator.pboolean(value):value);
    }

    public static byte pbyte(int location, byte value) {
        return (perturbation(location)?pertubator.pbyte(value):value);
    }

    public static short pshort(int location, short value) {
        return (perturbation(location)?pertubator.pshort(value):value);
    }

    public static int pint(int location, int value) {
        return (perturbation(location)?pertubator.pint(value):value);
    }

    public static long plong(int location, long value) {
        return (perturbation(location)?pertubator.plong(value):value);
    }

    public static char pchar(int location, char value) {
        return (perturbation(location)?pertubator.pchar(value):value);
    }

    public static float pfloat(int location, float value) {
        return (perturbation(location)?pertubator.pfloat(value):value);
    }

    public static double pdouble(int location, double value) {
        return (perturbation(location)?pertubator.pdouble(value):value);
    }

}
