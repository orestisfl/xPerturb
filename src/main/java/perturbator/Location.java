package perturbator;

/**
 * Created by spirals on 08/03/16.
 */
public class Location {

    public final int locationIndex;

    public final String locationInCode;

    private Location() {
        locationInCode = "";
        locationIndex = -1;
    }

    public Location(String location, int index) {
        locationInCode = location;
        locationIndex = index;

    }

}
