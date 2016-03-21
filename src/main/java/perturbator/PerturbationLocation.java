package perturbator;

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

}
