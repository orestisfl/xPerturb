package perturbation.enactor;

import perturbation.location.PerturbationLocation;

/**
 * Created by beyni on 02/04/16.
 */
public class NeverEnactorImpl implements Enactor{
    @Override
    public boolean shouldBeActivated() {
        return false;
    }
}
