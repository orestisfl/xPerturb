package perturbation.enactor;

import perturbation.location.PerturbationLocation;

/**
 * Created by spirals on 23/03/16.
 */
public class NTimeEnactorImpl implements Enactor {

    private int n;
    private int timeCall;

    public NTimeEnactorImpl() {
        this.n = 1;
        this.timeCall = 0;
    }

    public NTimeEnactorImpl(int n) {
        this.n = n;
        this.timeCall = 0;
    }

    @Override
    public boolean shouldBeActivated() {
        return this.n > this.timeCall++;
    }

    @Override
    public String toString() {
        return "NTIM";
    }
}
