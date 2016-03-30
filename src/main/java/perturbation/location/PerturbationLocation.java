package perturbation.location;

import perturbation.perturbator.Perturbator;

/**
 * Created by spirals on 30/03/16.
 */
public interface PerturbationLocation {

    int getLocationIndex();

    String getLocationInCode();

    String getType();

    boolean mustBeEnact();

    void setEnaction(boolean enaction);

    Perturbator getPerturbator();

    void setPerturbator(Perturbator pertubator) ;

}
