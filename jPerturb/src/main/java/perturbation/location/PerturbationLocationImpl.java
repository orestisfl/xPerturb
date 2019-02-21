package perturbation.location;

import perturbation.enactor.Enactor;
import perturbation.enactor.NeverEnactorImpl;
import perturbation.perturbator.InvPerturbatorImpl;
import perturbation.perturbator.NothingPerturbatorImpl;
import perturbation.perturbator.Perturbator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by spirals on 08/03/16.
 */
public class PerturbationLocationImpl implements PerturbationLocation {

    private final String locationType;

    private final int locationIndex;

    private final String locationInCode;

    private Perturbator perturbator = new NothingPerturbatorImpl();

    private Enactor enactor = new NeverEnactorImpl();

    @Override
    public int getLocationIndex(){
        return this.locationIndex;
    }

    @Override
    public String getLocationInCode() {
        return this.locationInCode;
    }

    @Override
    public String getType() {
        return this.locationType;
    }

    private PerturbationLocationImpl() {
        this.locationInCode = "";
        this.locationIndex = -1;
        this.locationType = "";
    }

    public PerturbationLocationImpl(String location, int index, String type) {
        this.locationInCode = location;
        this.locationIndex = index;
        this.locationType = type;
        this.perturbator = new InvPerturbatorImpl();
    }

    public Perturbator getPerturbator() {
        return this.perturbator;
    }

    public void setPerturbator(Perturbator pertubator) {
        this.perturbator = pertubator;
    }

    @Override
    public Enactor getEnactor() {
        return this.enactor;
    }

    @Override
    public void setEnactor(Enactor enactor) {
        this.enactor = enactor;
    }

    @Override
    public String toString() {
        return locationIndex+"\t"+locationInCode+"\t"+locationType;
    }

    @Override
    public int hashCode() {
        return this.locationIndex;
    }

    @Override
    public boolean equals(Object that) {
        return that instanceof PerturbationLocationImpl && (PerturbationLocationImpl.this.locationIndex) == ((PerturbationLocationImpl)that).locationIndex;
    }

    public static List<PerturbationLocation> getLocationFromClass(Class clazz) {
        List<PerturbationLocation> locations = new ArrayList<PerturbationLocation>();
        try {
            Field[] fields = clazz.getFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.getName().startsWith("__L"))
                    try {
                        locations.add(((PerturbationLocation) (field.get(null))));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

            }
        } catch (NoClassDefFoundError e) {}
        return locations;
    }
}
