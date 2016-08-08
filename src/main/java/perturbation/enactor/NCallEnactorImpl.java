package perturbation.enactor;

/**
 * Created by beyni on 02/04/16.
 */
public class NCallEnactorImpl extends EnactorDecorator {

	/**
	 * Enact the location at the nth call.
	 */
	private final int n;

	/**
	 * Number of time that the location has been called.
	 */
	private int currentCall;

	public NCallEnactorImpl(int n) {
		this(new AlwaysEnactorImpl(), n);
	}

	public NCallEnactorImpl(Enactor enactor, int n) {
		super(enactor);
		this.n = n;
	}

	@Override
	public boolean shouldBeActivated() {
		return this.currentCall++ == this.n && super.shouldBeActivated();
	}


	@Override
	public String toString() {
		return "NCALL" + super.toString();
	}
}
