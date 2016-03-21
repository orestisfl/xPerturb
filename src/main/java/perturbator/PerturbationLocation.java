package perturbator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by spirals on 08/03/16.
 */
public class PerturbationLocation {

    private final int locationIndex;

    private final String locationInCode;

    public int getLocationIndex(){
        return locationIndex;
    }

    public String getLocationInCode() {
        return locationInCode;
    }

    public String replacement;

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

    public static List<List<PerturbationLocation>> getSubListsFromClass(Class clazz, int size) {
        List<List<PerturbationLocation>> subLists = new ArrayList<List<PerturbationLocation>>();
        buildSubList(subLists, null, getLocationFromClass(clazz), size);
        return subLists;
    }

    private static void buildSubList( List<List<PerturbationLocation>> subLists, List<PerturbationLocation> current, List<PerturbationLocation> locations, int size) {

        if (current == null)
            current = new ArrayList<PerturbationLocation>();

        for (int i = 0 ; i < locations.size() - size ; i++) {
            PerturbationLocation value = locations.get(i);
            current.add(value);
            locations.remove(value);
            if (current.size() == size) {
                List<PerturbationLocation> subList = new ArrayList<PerturbationLocation>();
                subList.addAll(current);
                subLists.add(subList);
            }
            List<PerturbationLocation> newLocations = new ArrayList<PerturbationLocation>();
            newLocations.addAll(locations);
            buildSubList(subLists, current, newLocations, size);
            current.remove(value);
        }
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
        return that instanceof PerturbationLocation && (perturbator.PerturbationLocation.this.locationIndex) == ((PerturbationLocation)that).locationIndex;
    }

}
