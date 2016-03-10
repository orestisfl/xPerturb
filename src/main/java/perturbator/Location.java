package perturbator;

/**
 * Created by spirals on 08/03/16.
 */
public class Location {

    public final int location;

    public final String position;

    public Location() {
        this.location = -1;
        this.position  = "";
    }

    public Location(int location, String sourcePosition) {
        this.location = location;
        this.position = sourcePosition;
    }



}
