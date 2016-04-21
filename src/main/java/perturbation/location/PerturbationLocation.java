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

    int getLocationIndex();

    String getLocationInCode();

    String getType();

    Perturbator getPerturbator();

    void setPerturbator(Perturbator pertubator);

    Enactor getEnactor();

    void setEnactor(Enactor enactor);

}
