package perturbation.rmi;

import perturbation.location.PerturbationLocation;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by bdanglot on 20/07/16.
 */
public interface PerturbationServer extends Remote {

	/**
	 * @return a list of all locations loaded
	 * @throws RemoteException
	 */
	List<PerturbationLocation> getLocations() throws RemoteException;

	void enableLocation(PerturbationLocation location) throws RemoteException;

	/**
	 *
	 * @param location
	 * @return the number of execution of the given location
	 * @throws RemoteException
	 */
	int getCalls(PerturbationLocation location) throws RemoteException;

	/**
	 *
	 * @param location
	 * @return the number of perturbation of the given location
	 * @throws RemoteException
	 */
	int getEnactions(PerturbationLocation location) throws RemoteException;

	void disableLocation(PerturbationLocation location) throws RemoteException;

	void stopService() throws RemoteException;

}
