package perturbation.log;

import perturbation.location.PerturbationLocation;

/**
 * Created by spirals on 31/03/16.
 */
public class LoggerImpl implements Logger {

    private int numberOfCalls = 0;
    private int numberOfEnactions = 0;
    private PerturbationLocation location;

    public int getNumberOfCalls() {
        return this.numberOfCalls;
    }

    public int getNumberOfEnactions() {
        return this.numberOfEnactions;
    }

    public void reset() {
        this.location = null;
        this.numberOfCalls = 0;
        this.numberOfEnactions = 0;
    }

    @Override
    public void logOn(PerturbationLocation location) {
        this.location = location;
    }

    @Override
    public void logCall(PerturbationLocation location) {
        if (location.equals(this.location))
            this.numberOfCalls++;
    }

    @Override
    public void logEnaction(PerturbationLocation location) {
        if (location.equals(this.location))
            this.numberOfEnactions++;
    }
}
