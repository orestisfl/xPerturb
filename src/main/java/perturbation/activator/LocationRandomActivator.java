package perturbation.activator;

import perturbation.PerturbationLocation;

/**
 * Created by spirals on 23/03/16.
 */
public class LocationRandomActivator extends RandomActivator {

    public LocationRandomActivator(float epsilon) {
        super(epsilon);
    }

    public LocationRandomActivator(float epsilon, int seed) {
        super(epsilon, seed);
    }

    @Override
    public boolean shouldBeActivated(PerturbationLocation location) {
        return locations.contains(location) && super.shouldBeActivated(location);
    }

    @Override
    public String toString() {
        return "location_"+super.toString();
    }
}
