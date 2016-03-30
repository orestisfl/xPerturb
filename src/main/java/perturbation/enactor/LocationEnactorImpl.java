package perturbation.enactor;

import perturbation.location.PerturbationLocationImpl;

/**
 * Created by spirals on 23/03/16.
 */
public class LocationEnactorImpl implements Enactor {

    @Override
    public boolean shouldBeActivated(PerturbationLocationImpl location) {
        return location.mustBeEnact();
    }

    public String toString() {
        return "location";
    }

}
