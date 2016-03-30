package perturbation.enactor;

import perturbation.location.PerturbationLocationImpl;

import java.util.Random;

/**
 * Created by spirals on 23/03/16.
 */
public class RandomEnactorImpl extends EnactorDecorator {

    protected float epsilon;
    protected Random rnd;

    public RandomEnactorImpl() {
        super(new LocationEnactorImpl());
        this.epsilon = 0.05f;
        this.rnd = new java.util.Random();
    }

    public RandomEnactorImpl(float epsilon) {
        super(new LocationEnactorImpl());
        this.epsilon = epsilon;
        this.rnd = new java.util.Random();
    }

    public RandomEnactorImpl(Enactor decoratedEnactor, float epsilon) {
        super(decoratedEnactor);
        this.epsilon = epsilon;
        this.rnd = new java.util.Random();
    }

    public RandomEnactorImpl(Enactor decoratedEnactor, float epsilon, int seed) {
        super(decoratedEnactor);
        this.epsilon = epsilon;
        this.rnd = new java.util.Random(seed);
    }

    @Override
    public boolean shouldBeActivated(PerturbationLocationImpl location) {
        return rnd.nextFloat() < this.epsilon && super.shouldBeActivated(location);
    }

    @Override
    public String toString() {
        return "random:"+this.epsilon + "_" + super.toString();
    }
}
