package perturbation.activator;

import perturbation.AbstractActivator;
import perturbation.PerturbationLocation;

/**
 * Created by spirals on 23/03/16.
 */
public class LocationActivator extends AbstractActivator {

    @Override
    public boolean shouldBeActivated(PerturbationLocation location) {
        return this.locations.contains(location);
    }

    public String toString() {
        return "location";
    }

}
