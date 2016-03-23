package perturbation.activator;

import perturbation.AbstractActivator;
import perturbation.PerturbationLocation;

/**
 * Created by spirals on 23/03/16.
 */
public class LocationActivator extends AbstractActivator {

    /**
     * This method will add n time the given location at the list in order to activate it n time.
     * @param location of the perturbation to activate
     * @param n time that the location will be activate
     */
    public void addLocation(PerturbationLocation location, int n) {
        for (int i = 0 ; i < n ; i++)
            this.locations.add(location);
    }

    @Override
    public boolean shouldBeActivated(PerturbationLocation location) {
        if (this.locations.contains(location)) {
            this.locations.remove(location);
            return true;
        } else
            return false;
    }

    @Override
    public String toString() {
        return "nTimeLocation";
    }
}
