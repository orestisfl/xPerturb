package perturbation.log;

import perturbation.location.PerturbationLocation;

/**
 * Created by spirals on 28/03/16.
 */
public interface Logger {

    void logOn(PerturbationLocation location);

    void remove(PerturbationLocation locatio);

    void logCall(PerturbationLocation location);

    void logEnaction(PerturbationLocation location);

    int getCalls(PerturbationLocation location);

    int getEnactions(PerturbationLocation location);

    boolean isLogging(PerturbationLocation location);

    void reset();

}
