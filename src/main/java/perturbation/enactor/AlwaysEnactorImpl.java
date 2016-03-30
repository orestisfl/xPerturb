package perturbation.enactor;

import perturbation.location.PerturbationLocationImpl;

/**
 * Created by spirals on 23/03/16.
 */
public class AlwaysEnactorImpl implements Enactor {

    @Override
    public boolean shouldBeActivated(PerturbationLocationImpl location) {
        return true;
    }

    @Override
    public String toString() {
        return "always";
    }

}
