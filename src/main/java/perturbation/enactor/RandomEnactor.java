package perturbation.enactor;

import perturbation.PerturbationLocation;

import java.util.Random;

/**
 * Created by spirals on 23/03/16.
 */
public class RandomEnactor extends AbstractEnactor {

    protected float epsilon;
    protected Random rnd;

    public RandomEnactor(float epsilon) {
        super();
        this.epsilon = epsilon;
        this.rnd = new java.util.Random();
    }

    public RandomEnactor(float epsilon, int seed) {
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
