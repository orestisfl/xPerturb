package perturbation.enactor;

import perturbation.location.PerturbationLocation;
import perturbation.log.LoggerImpl;

/**
 * Created by beyni on 02/04/16.
 */
public class NCallEnactorImpl extends LoggerImpl implements Enactor{

    private int n = 0;
    private PerturbationLocation location;

    public NCallEnactorImpl(int n, PerturbationLocation location) {
        this.n = n;
        this.logOn(location);
        this.location = location;
    }

    @Override
    public boolean shouldBeActivated() {
        return this.getCalls(this.location) == n;
    }

}
