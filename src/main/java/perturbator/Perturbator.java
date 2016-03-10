package perturbator;


import java.util.ArrayList;
import java.util.List;

public class Perturbator {

    private static boolean oneTime = false;

    private static boolean firstTime = true;

    private static List<Integer> l = new ArrayList<>();

    private static AbstractPerturbator pertubator = new RndPerturbator();

    private static int number = 1;

    private static List<Location> locations = new ArrayList<Location>();

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

    public static String getLocation(int location) {
        for (Location l : locations) {
            if (l.location == location)
                return l.position;
        }
        return "location of perturbation not found";
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
    public static boolean pboolean(Location location, boolean value) {
        locations.add(location);
        return (perturbation(location.location)?pertubator.pboolean(value):value);
    }

    public static byte pbyte(Location location, byte value) {
        locations.add(location);
        return (perturbation(location.location)?pertubator.pbyte(value):value);
    }

    public static short pshort(Location location, short value) {
        locations.add(location);
        return (perturbation(location.location)?pertubator.pshort(value):value);
    }

    public static int pint(Location location, int value) {
        return (perturbation(location.location)?pertubator.pint(value):value);
    }

    public static long plong(Location location, long value) {
        locations.add(location);
        return (perturbation(location.location)?pertubator.plong(value):value);
    }

    public static char pchar(Location location, char value) {
        locations.add(location);
        return (perturbation(location.location)?pertubator.pchar(value):value);
    }

    public static float pfloat(Location location, float value) {
        locations.add(location);
        return (perturbation(location.location)?pertubator.pfloat(value):value);
    }

    public static double pdouble(Location location, double value) {
        locations.add(location);
        return (perturbation(location.location)?pertubator.pdouble(value):value);
    }

}
