package perturbation.location;

import perturbation.enactor.Enactor;
import perturbation.perturbator.Perturbator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by spirals on 30/03/16.
 */
public interface PerturbationLocation {

    /**
     * @return the unique index of this location
     */
    int getLocationIndex();

    /**
     * @return a string caracterizing the source position (in the original code) of this location
     */
    String getLocationInCode();

    /**
     * @return as string the type of the location : Numerical for instance.
     */
    String getType();

    Perturbator getPerturbator();

    void setPerturbator(Perturbator pertubator);

    Enactor getEnactor();

    void setEnactor(Enactor enactor);

}
