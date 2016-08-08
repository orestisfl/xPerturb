package perturbation.enactor;

/**
 * Created by spirals on 23/03/16.
 */
public class NTimeEnactorImpl extends EnactorDecorator {

	/**
	 * number of time to perturb
	 */
	private final int n;

	/**
	 * current number of perturbation
	 */
	private int timeCall;

	public NTimeEnactorImpl(Enactor enactor, int n) {
		super(enactor);
		this.n = n;
		this.timeCall = 0;
	}

	public NTimeEnactorImpl(Enactor enactor) {
		this(enactor, 1);
	}

	public NTimeEnactorImpl(int n) {
		this(new AlwaysEnactorImpl(), n);
	}

	public NTimeEnactorImpl() {
		this(new AlwaysEnactorImpl(), 1);
	}

	@Override
	public boolean shouldBeActivated() {
		return this.n > this.timeCall++ && super.shouldBeActivated();
	}

	@Override
	public String toString() {
		return "NTIM" + super.toString();
	}
}
