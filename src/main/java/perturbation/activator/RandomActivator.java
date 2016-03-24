package perturbation.activator;

import perturbation.AbstractActivator;
import perturbation.PerturbationLocation;

import java.util.Random;

/**
 * Created by spirals on 23/03/16.
 */
public class RandomActivator extends AbstractActivator {

    protected float epsilon;
    protected Random rnd;

    public RandomActivator(float epsilon) {
        super();
        this.epsilon = epsilon;
        this.rnd = new java.util.Random(System.currentTimeMillis());
    }

    public RandomActivator(float epsilon, int seed) {
        super();
        this.epsilon = epsilon;
        this.rnd = new java.util.Random(seed);
    }

    @Override
    public boolean shouldBeActivated(PerturbationLocation location) {
        return rnd.nextFloat() < this.epsilon;
    }

    @Override
    public String toString() {
        return "random_"+this.epsilon;
    }
}
