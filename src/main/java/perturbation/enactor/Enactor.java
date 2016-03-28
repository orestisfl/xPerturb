package perturbation.enactor;

import perturbation.PerturbationLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by spirals on 23/03/16.
 */
public abstract class Enactor {

    protected List<PerturbationLocation> locations;

    public Enactor() {
        this.locations = new ArrayList<PerturbationLocation>();
    }

    public void addLocation(PerturbationLocation location) {
        location.reset();
        this.locations.add(location);
    }

    public boolean removeLocation(PerturbationLocation location) {
        location.reset();
        return this.locations.remove(location);
    }

    public void reset() {
        for (PerturbationLocation location : this.locations)
            location.reset();
        this.locations.clear();
    }

    public int numberOfPerturbationOn() {
        return this.locations.size();
    }

    public abstract boolean shouldBeActivated(PerturbationLocation location);


}
