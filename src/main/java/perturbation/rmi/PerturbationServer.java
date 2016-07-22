package perturbation.rmi;

import perturbation.location.PerturbationLocation;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by bdanglot on 20/07/16.
 */
public interface PerturbationServer extends Remote {

	List<PerturbationLocation> getAllLocations() throws RemoteException;

	void enableLocation(PerturbationLocation location) throws RemoteException;

	void disableLocation(PerturbationLocation location) throws RemoteException;

}
