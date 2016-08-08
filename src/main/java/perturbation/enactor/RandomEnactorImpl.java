package perturbation.enactor;

import java.util.Random;

/**
 * Created by spirals on 23/03/16.
 */
public class RandomEnactorImpl extends EnactorDecorator {

	private float epsilon;
	private Random rnd;

	public RandomEnactorImpl(Enactor enactor, int seed, float epsilon) {
		super(enactor);
		this.epsilon = epsilon;
		this.rnd = new java.util.Random(seed);
	}

	public RandomEnactorImpl() {
		this(new AlwaysEnactorImpl(), (int) System.currentTimeMillis(), 0.05f);
	}

	public RandomEnactorImpl(float epsilon) {
		this(new AlwaysEnactorImpl(), (int) System.currentTimeMillis(), epsilon);
	}

	public RandomEnactorImpl(int seed, float epsilon) {
		this(new AlwaysEnactorImpl(), seed, epsilon);
	}

	@Override
	public boolean shouldBeActivated() {
		return rnd.nextFloat() < this.epsilon && super.shouldBeActivated();
	}

	@Override
	public String toString() {
		return "RAND" + super.toString();
	}
}
