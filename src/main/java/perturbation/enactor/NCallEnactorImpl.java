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
        location.setEnactor(this);
        PerturbationEngine.loggers.put("NCallEnactor", new LoggerImpl());
        PerturbationEngine.loggers.get("NCallEnactor").logOn(location);
    }

    @Override
    public boolean shouldBeActivated() {
        boolean active =  PerturbationEngine.loggers.get("NCallEnactor").isLogging(location) &&
                PerturbationEngine.loggers.get("NCallEnactor").getCalls(location) == this.n;
        return active;
    }

}
