package perturbator;

/**
 * Created by spirals on 08/03/16.
 */
public class PerturbationLocation {

    public final int locationIndex;

    public final String locationInCode;

    private PerturbationLocation() {
        locationInCode = "";
        locationIndex = -1;
    }

    public PerturbationLocation(String location, int index) {
        locationInCode = location;
        locationIndex = index;

    }

}
