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

    static List<PerturbationLocation> getLocationFromClass(Class clazz) {

        Field[] fields = clazz.getFields();

        List<PerturbationLocation> locations = new ArrayList<PerturbationLocation>();

        for (int i = 0 ; i < fields.length ; i++) {
            if (fields[i].getName().startsWith("__L"))
                try {
                    locations.add((PerturbationLocation) fields[i].get(null));
                } catch (IllegalAccessException e) {//won't occurs any way
                    e.printStackTrace();
                }
        }

        return locations;
    }

}
