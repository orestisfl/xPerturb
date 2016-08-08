package perturbation.enactor;

/**
 * Created by beyni on 02/04/16.
 */
public class NeverEnactorImpl implements Enactor{
    @Override
    public boolean shouldBeActivated() {
        return false;
    }
}
