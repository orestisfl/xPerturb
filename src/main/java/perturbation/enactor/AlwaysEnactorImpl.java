package perturbation.enactor;

/**
 * Created by spirals on 23/03/16.
 */
public class AlwaysEnactorImpl implements Enactor {

    @Override
    public boolean shouldBeActivated() {
        return true;
    }

    @Override
    public String toString() {
        return "ALWA";
    }

}
