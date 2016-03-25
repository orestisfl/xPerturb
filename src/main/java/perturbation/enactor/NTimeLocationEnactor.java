package perturbation.enactor;

import perturbation.PerturbationLocation;

/**
 * Created by spirals on 23/03/16.
 */
public class NTimeLocationEnactor extends AbstractEnactor {

    private int n = 1;

    public NTimeLocationEnactor(int n) {
        super();
        this.n = n;
    }

    /**
     * This method will add n time the given location at the list in order to activate it n time.
     * @param location of the perturbation to activate
     */
    public void addLocation(PerturbationLocation location) {
        for (int i = 0 ; i < n ; i++)
            this.locations.add(location);
    }

    @Override
    public boolean shouldBeActivated(PerturbationLocation location) {
        if (this.locations.contains(location)) {
            this.locations.remove(location);
            return true;
        } else
            return false;
    }

    @Override
    public String toString() {
        return n+"_TimeLocation";
    }
}
