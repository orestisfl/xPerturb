package perturbator;

/**
 * Created by spirals on 08/03/16.
 */
public class Location {

    private static final String[] locations = new String[] {};

    public static String getLocation(int i) {
        if (i >= locations.length)
            return "location not found";
        else
            return locations[i];
    }

    public static int numberOfLocation() {
        return locations.length;
    }

}
