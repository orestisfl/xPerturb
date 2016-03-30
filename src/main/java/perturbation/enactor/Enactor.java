package perturbation.enactor;

import perturbation.location.PerturbationLocationImpl;

/**
 * Created by spirals on 23/03/16.
 */
public interface  Enactor {

    boolean shouldBeActivated(PerturbationLocationImpl location);

}
