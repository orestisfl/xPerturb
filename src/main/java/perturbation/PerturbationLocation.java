package perturbation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by spirals on 08/03/16.
 */
public class PerturbationLocation {

    private final int locationIndex;

    private final String locationInCode;

    public String replacement;

    public int numberOfSuccess = 0;

    public int numberOfFailure = 0;

    public int numberOfCall = 0;

    public int numberOfActivation = 0;

    public int getLocationIndex(){
        return locationIndex;
    }

    public String getLocationInCode() {
        return locationInCode;
    }

    private PerturbationLocation() {
        locationInCode = "";
        locationIndex = -1;
    }

    public PerturbationLocation(String location, int index) {
        locationInCode = location;
        locationIndex = index;
    }

    @Override
    public String toString() {
        return locationIndex+"\t"+locationInCode+"\t"+replacement;
    }

    public static List<List<PerturbationLocation>> buildSubList(Class clazz, int k) {
        List<PerturbationLocation> locations = PerturbationLocation.getLocationFromClass(clazz);
        List<List<PerturbationLocation>> subLists = new ArrayList<List<PerturbationLocation>>();
        LinkedList<String> indices = comb(k, locations.size());
        for (String indice : indices) {
            List<PerturbationLocation> currentSubList = new ArrayList<PerturbationLocation>();
            String [] loc = indice.split(" ");
            for (int i = 0 ; i < k ; i++) {
                currentSubList.add(locations.get(Integer.parseInt(loc[i])));
            }
            subLists.add(currentSubList);
        }
        return subLists;
    }

    private static String bitprint(int u) {
        String s = "";
        for (int n = 0; u > 0; ++n, u >>= 1)
            if ((u & 1) > 0) s += n + " ";
        return s;
    }

    private static int bitcount(int u) {
        int n;
        for (n = 0; u > 0; ++n, u &= (u - 1));
        return n;
    }

    private static LinkedList<String> comb(int c, int n) {
        LinkedList<String> s = new LinkedList<String>();
        for (int u = 0; u < 1 << n; u++)
            if (bitcount(u) == c) s.push(bitprint(u));
        Collections.sort(s);
        return s;
    }

    public static List<PerturbationLocation> getLocationFromClass(Class clazz) {

        Field[] fields = clazz.getFields();

        List<PerturbationLocation> locations = new ArrayList<PerturbationLocation>();

        for (int i = 0 ; i < fields.length ; i++) {
            if (fields[i].getName().startsWith("__L"))
                try {
                    locations.add((PerturbationLocation) fields[i].get(null));
                } catch (IllegalAccessException e) {//won't occurs any way
                    e.printStackTrace();
                }
        }

        return locations;
    }

    @Override
    public boolean equals(Object that) {
        return that instanceof PerturbationLocation && (perturbation.PerturbationLocation.this.locationIndex) == ((PerturbationLocation)that).locationIndex;
    }

}
