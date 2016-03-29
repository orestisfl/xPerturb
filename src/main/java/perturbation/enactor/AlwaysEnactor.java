package perturbation.enactor;

import perturbation.PerturbationLocation;

/**
 * Created by spirals on 23/03/16.
 */
public class AlwaysEnactor extends Enactor {

    public AlwaysEnactor() {
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
