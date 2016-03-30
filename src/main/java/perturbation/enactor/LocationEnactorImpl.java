package perturbation.enactor;

import perturbation.location.PerturbationLocation;

/**
 * Created by spirals on 23/03/16.
 */
public class LocationEnactorImpl implements Enactor {

    @Override
    public boolean shouldBeActivated(PerturbationLocation location) {
        return location.mustBeEnact();
    }

    public String toString() {
        return "location";
    }

}
