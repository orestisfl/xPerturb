package perturbation.enactor;

import perturbation.location.PerturbationLocation;

/**
 * Created by spirals on 23/03/16.
 */
public class AlwaysEnactorImpl implements Enactor {

    @Override
    public boolean shouldBeActivated(PerturbationLocation location) {
        return true;
    }

    @Override
    public String toString() {
        return "ALWA";
    }

}
