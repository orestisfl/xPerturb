package perturbation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by spirals on 08/03/16.
 */
public class PerturbationLocation {

    private final String locationType;

    private final int locationIndex;

    private final String locationInCode;

    public String replacement;

    public int numberOfSuccess = 0;

    public int numberOfFailure = 0;

    public int numberOfCall = 0;

    public int numberOfActivation = 0;

    public void reset() {
        this.numberOfFailure = 0;
        this.numberOfActivation = 0;
        this.numberOfCall = 0;
        this.numberOfSuccess = 0;
        this.replacement = null;
    }

    public int getLocationIndex(){
        return this.locationIndex;
    }

    public String getLocationInCode() {
        return this.locationInCode;
    }

    public String getType() {
        return this.locationType;
    }

    private PerturbationLocation() {
        this.locationInCode = "";
        this.locationIndex = -1;
        this.locationType = "";
    }

    public PerturbationLocation(String location, int index, String type) {
        this.locationInCode = location;
        this.locationIndex = index;
        this.locationType = type;
    }

    @Override
    public String toString() {
        return locationIndex+"\t"+locationInCode+"\t"+locationType+"\t"+replacement;
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
