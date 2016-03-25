package perturbation.enactor;

import perturbation.PerturbationLocation;

/**
 * Created by spirals on 23/03/16.
 */
public class AlwaysEnactor extends Enactor {

    public AlwaysEnactor() {
        super();
    }

    @Override
    public boolean shouldBeActivated(PerturbationLocation location) {
        return true;
    }

    @Override
    public String toString() {
        return "always";
    }

    public static void main(String[] args) {

        Enactor e = new RandomEnactor(new NTimeEnactor(new LocationEnactor(), 1), 0.05f);
        Enactor e1 = new RandomEnactor(new LocationEnactor(), 0.05f);
        Enactor e2 = new NTimeEnactor(new LocationEnactor(), 1);
        Enactor e3 = new LocationEnactor();

        System.out.println(e);
        System.out.println(e1);
        System.out.println(e2);
        System.out.println(e3);

    }
}
