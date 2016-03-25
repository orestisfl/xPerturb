package perturbation.enactor;

import perturbation.PerturbationLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by spirals on 23/03/16.
 */
public abstract class AbstractEnactor {

    protected List<PerturbationLocation> locations;

    public AbstractEnactor() {
        this.locations = new ArrayList<PerturbationLocation>();
    }

    public void addLocation(PerturbationLocation location) {
        this.locations.add(location);
    }

    public boolean removeLocation(PerturbationLocation location) {
        return this.locations.remove(location);
    }

    public void reset() {
        this.locations.clear();
    }

    public int numberOfPerturbationOn() {
        return this.locations.size();
    }

    public abstract boolean shouldBeActivated(PerturbationLocation location);


}
