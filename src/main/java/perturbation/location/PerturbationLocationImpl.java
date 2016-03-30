package perturbation.location;

import perturbation.perturbator.InvPerturbatorImpl;
import perturbation.perturbator.Perturbator;

/**
 * Created by spirals on 08/03/16.
 */
public class PerturbationLocationImpl implements PerturbationLocation {

    private final String locationType;

    private final int locationIndex;

    private final String locationInCode;

    private boolean mustBeEnact = false;

    private Perturbator perturbator;

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

    @Override
    public boolean mustBeEnact() {
        return mustBeEnact;
    }

    @Override
    public void setEnaction(boolean enaction) {
        this.mustBeEnact = enaction;
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
    public String toString() {
        return locationIndex+"\t"+locationInCode+"\t"+locationType;
    }

    @Override
    public boolean equals(Object that) {
        return that instanceof PerturbationLocationImpl && (PerturbationLocationImpl.this.locationIndex) == ((PerturbationLocationImpl)that).locationIndex;
    }

}
