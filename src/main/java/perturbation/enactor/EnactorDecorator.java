package perturbation.enactor;

import perturbation.location.PerturbationLocation;

/**
 * Created by spirals on 25/03/16.
 */
public class EnactorDecorator implements Enactor {

    protected Enactor decoratedEnactor;

    public EnactorDecorator(Enactor decoratedEnactor) {
        super();
        this.decoratedEnactor = decoratedEnactor;
    }

    @Override
    public boolean shouldBeActivated(PerturbationLocation location) {
        return this.decoratedEnactor.shouldBeActivated(location);
    }

    @Override
    public String toString() {
        return this.decoratedEnactor.toString();
    }
}
