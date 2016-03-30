package perturbation.enactor;

import perturbation.location.PerturbationLocation;

/**
 * Created by spirals on 23/03/16.
 */
public class NTimeEnactorImpl extends LocationEnactorImpl {

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
    public boolean shouldBeActivated(PerturbationLocation location) {
        if (super.shouldBeActivated(location) && n > 0) {
            if (this.timeCall+1 == n) {
                this.timeCall = 0;
                location.setEnaction(false);
            } else
                this.timeCall++;
            return true;
        } else
            return false;
    }

    @Override
    public String toString() {
        return n+":Time_"+super.toString();
    }
}
