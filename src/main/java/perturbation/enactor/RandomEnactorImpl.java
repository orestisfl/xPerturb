package perturbation.enactor;

import java.util.Random;

/**
 * Created by spirals on 23/03/16.
 */
public class RandomEnactorImpl implements Enactor {

    protected float epsilon;
    protected Random rnd;

    public RandomEnactorImpl() {
        this.epsilon = 0.05f;
        this.rnd = new java.util.Random();
    }

    public RandomEnactorImpl(float epsilon) {
        this.epsilon = epsilon;
        this.rnd = new java.util.Random();
    }

    public RandomEnactorImpl(int seed, float epsilon) {
        this.epsilon = epsilon;
        this.rnd = new java.util.Random(seed);
    }

    @Override
    public boolean shouldBeActivated() {
        return rnd.nextFloat() < this.epsilon;
    }

    @Override
    public String toString() {
        return "RAND";
    }
}
