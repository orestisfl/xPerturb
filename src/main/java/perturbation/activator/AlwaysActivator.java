package perturbation.activator;

import perturbation.AbstractActivator;
import perturbation.PerturbationLocation;

/**
 * Created by spirals on 23/03/16.
 */
public class AlwaysActivator extends AbstractActivator {

    public AlwaysActivator() {
        super();
    }

    @Override
    public boolean shouldBeActivated(PerturbationLocation location) {
        return true;
    }

    @Override
    public String toString() {
        return "always";
    }
}
