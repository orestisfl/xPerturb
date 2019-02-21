package perturbation.rmi;

import perturbation.enactor.Enactor;
import perturbation.location.PerturbationLocation;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by bdanglot on 20/07/16.
 */
public interface PerturbationServer extends Remote {

	PerturbationLocation enableLocation(PerturbationLocation location) throws RemoteException;

	PerturbationLocation enableLocation(PerturbationLocation location, Enactor enactor) throws RemoteException;

	PerturbationLocation disableLocation(PerturbationLocation location) throws RemoteException;

	List<PerturbationLocation> getLocations() throws RemoteException;

	int getCalls(PerturbationLocation location) throws RemoteException;

	int getEnactions(PerturbationLocation location) throws RemoteException;

	/**
	 * return the number of call for each location, in a array of int where the index of the location
	 * is the index in the array
	 *
	 * @return
	 * @throws RemoteException
	 */
	int[] getCalls() throws RemoteException;

	/**
	 * Same as getCalls() but will set 0 to the counter of the number of calls after retrieve it
	 *
	 * @return
	 * @throws RemoteException
	 */
	int[] getCallsAndResetLogger() throws RemoteException;

	/**
	 * return the number of perturbation for each location, in a array of int where the index of the location
	 * is the index in the array
	 *
	 * @return
	 * @throws RemoteException
	 */
	int[] getEnactions() throws RemoteException;

	/**
	 * Same as getEnactions() but will set 0 to the counter of the number of perturbations after retrieve it
	 *
	 * @return
	 * @throws RemoteException
	 */
	int[] getEnactionsAndResetLogger() throws RemoteException;


	PerturbationLocation logOn(PerturbationLocation location) throws RemoteException;

	void logAllLocation() throws RemoteException;

	PerturbationLocation stopLogOn(PerturbationLocation location) throws RemoteException;

	void stopLogOnAllLocation() throws RemoteException;

	void stopService() throws RemoteException;

}
