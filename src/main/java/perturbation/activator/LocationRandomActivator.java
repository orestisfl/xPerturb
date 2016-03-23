package perturbation.activator;

import perturbation.AbstractActivator;
import perturbation.PerturbationLocation;

import java.util.Random;

/**
 * Created by spirals on 23/03/16.
 */
public class LocationRandomActivator extends AbstractActivator {

    private float epsilon;
    private Random rnd;

    public LocationRandomActivator(float epsilon) {
        super();
        this.epsilon = epsilon;
        this.rnd = new java.util.Random(System.currentTimeMillis());
    }

    public LocationRandomActivator(float epsilon, int seed) {
        super();
        this.epsilon = epsilon;
        this.rnd = new java.util.Random(seed);
    }

    @Override
    public boolean shouldBeActivated(PerturbationLocation location) {
        return locations.contains(location) && rnd.nextFloat() <= this.epsilon;
    }

    @Override
    public String toString() {
        return "locationRnd_"+this.epsilon;
    }
}
