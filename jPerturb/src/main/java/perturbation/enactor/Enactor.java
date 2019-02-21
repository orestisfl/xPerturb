package perturbation.enactor;

import java.io.Serializable;

/**
 * Created by spirals on 23/03/16.
 */
public interface  Enactor extends Serializable {

    /**
     * @return if the perturbation should be activated
     */
    boolean shouldBeActivated();

}
