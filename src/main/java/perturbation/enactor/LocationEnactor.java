package perturbation.enactor;

import perturbation.PerturbationLocation;

/**
 * Created by spirals on 23/03/16.
 */
public class LocationEnactor extends AbstractEnactor {

    @Override
    public boolean shouldBeActivated(PerturbationLocation location) {
        return this.locations.contains(location);
    }

    public String toString() {
        return "location";
    }

}
