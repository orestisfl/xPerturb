package perturbation.enactor;

import perturbation.PerturbationLocation;

import java.util.Random;

/**
 * Created by spirals on 23/03/16.
 */
public class RandomEnactor extends EnactorDecorator {

    protected float epsilon;
    protected Random rnd;

    public RandomEnactor() {
        super(new LocationEnactor());
        this.epsilon = 0.05f;
        this.rnd = new java.util.Random();
    }

    public RandomEnactor(float epsilon) {
        super(new LocationEnactor());
        this.epsilon = epsilon;
        this.rnd = new java.util.Random();
    }

    public RandomEnactor(Enactor decoratedEnactor, float epsilon) {
        super(decoratedEnactor);
        this.epsilon = epsilon;
        this.rnd = new java.util.Random();
    }

    public RandomEnactor(Enactor decoratedEnactor, float epsilon, int seed) {
        super(decoratedEnactor);
        this.epsilon = epsilon;
        this.rnd = new java.util.Random(seed);
    }

    @Override
    public boolean shouldBeActivated(PerturbationLocation location) {
        return rnd.nextFloat() < this.epsilon && super.shouldBeActivated(location);
    }

    @Override
    public String toString() {
        return "random:"+this.epsilon + "_" + super.toString();
    }
}
