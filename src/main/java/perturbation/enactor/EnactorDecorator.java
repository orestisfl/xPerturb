package perturbation.enactor;

import perturbation.PerturbationLocation;

/**
 * Created by spirals on 25/03/16.
 */
public class EnactorDecorator extends Enactor {

    protected Enactor decoratedEnactor;

    public EnactorDecorator(Enactor decoratedEnactor) {
        super();
        this.decoratedEnactor = decoratedEnactor;
    }

    @Override
    public void addLocation(PerturbationLocation location) {
        this.decoratedEnactor.addLocation(location);
    }

    @Override
    public boolean removeLocation(PerturbationLocation location) {
        return this.decoratedEnactor.removeLocation(location);
    }

    @Override
    public void reset() {
        this.decoratedEnactor.reset();
    }

    @Override
    public int numberOfPerturbationOn() {
        return this.decoratedEnactor.numberOfPerturbationOn();
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
