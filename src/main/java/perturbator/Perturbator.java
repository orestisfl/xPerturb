package perturbator;


import java.util.ArrayList;
import java.util.List;

public class Perturbator {

    private static boolean oneTime = false;

    private static boolean firstTime = true;

    private static List<Integer> l = new ArrayList<>();

    private static AbstractPerturbator pertubator = new RndPerturbator();

    /*
        Methods setting
     */

    public static void setOneTime(boolean setOneTime) {
        oneTime = setOneTime;
    }

    public static void setPertubator(AbstractPerturbator p) {
        pertubator = p;
    }

    public static void add(Integer i) {
        l.add(i);
    }

    public static boolean remove(Integer i) {
        return l.remove((Object)i);
    }

    public static void clear() {
        l.clear();
    }

    private static boolean perturbation(int location) {
        if (firstTime && (l.contains(-1)|| l.contains(location))) {
            firstTime = !oneTime;
            return true;
        } else
            return false;
    }

    /*
        Methods perturbation
     */
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
