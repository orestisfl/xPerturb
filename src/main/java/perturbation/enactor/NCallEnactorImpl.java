package perturbation.enactor;

import perturbation.PerturbationEngine;
import perturbation.location.PerturbationLocation;
import perturbation.log.LoggerImpl;

/**
 * Created by beyni on 02/04/16.
 */
public class NCallEnactorImpl implements Enactor{

    private int n = 0;
    private PerturbationLocation location;

    public NCallEnactorImpl(int n ,PerturbationLocation location) {
        this.n = n;
        this.location = location;
        PerturbationEngine.logger.logOn(location);
    }

    @Override
    public boolean shouldBeActivated() {
        return PerturbationEngine.logger.isLogging(location) && (PerturbationEngine.logger.getCalls(location)) == (n);
    }

}
