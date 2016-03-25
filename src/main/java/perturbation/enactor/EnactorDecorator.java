package perturbation.enactor;

import perturbation.PerturbationLocation;

/**
 * Created by spirals on 25/03/16.
 */
public class EnactorDecorator extends Enactor {

    protected Enactor decoratedEnactor;

    public EnactorDecorator(Enactor decoratedEnactor) {
        this.decoratedEnactor = decoratedEnactor;
    }

    @Override
    public boolean shouldBeActivated(PerturbationLocation location) {
        return  this.decoratedEnactor.shouldBeActivated(location);
    }

    @Override
    public String toString() {
        return this.decoratedEnactor.toString();
    }
}
