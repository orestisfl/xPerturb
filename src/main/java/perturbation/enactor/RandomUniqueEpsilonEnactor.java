package perturbation.enactor;

/**
 * Created by bdanglot on 07/06/16.
 */
public class RandomUniqueEpsilonEnactor extends RandomEnactorImpl {

	private boolean firstTime;

	public RandomUniqueEpsilonEnactor(Enactor enactor, int seed, float epsilon) {
		super(enactor, seed, epsilon);
		this.firstTime = true;
	}

	public RandomUniqueEpsilonEnactor(float epsilon) {
		super(epsilon);
		this.firstTime = true;
	}

	public RandomUniqueEpsilonEnactor(int seed, float epsilon) {
		super(seed, epsilon);
		this.firstTime = true;
	}


	@Override
	public boolean shouldBeActivated() {
		boolean activation = super.shouldBeActivated() && this.firstTime;
		this.firstTime = !activation;
		return activation;
	}

	@Override
	public String toString() {
		return "RNDU";
	}
}
