/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


package perturbation.rmi;

import perturbation.PerturbationEngine;
import perturbation.enactor.AlwaysEnactorImpl;
import perturbation.enactor.NeverEnactorImpl;
import perturbation.location.PerturbationLocation;
import perturbation.log.LoggerImpl;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import static perturbation.PerturbationEngine.loggers;

public class PerturbationServerImpl implements PerturbationServer {

	private static final String NAME_LOGGER = "PerturbationServerLogger";

	public static final int PORT = 13223;

	public static final String NAME_SERVER = "PerturbationServer";

	private List<PerturbationLocation> locations;

	public PerturbationServerImpl(String project, String packagePath) {
		this.locations = LocationsListBuilder.buildLocationsList(project, packagePath);
		loggers.put(NAME_LOGGER, new LoggerImpl());
	}


	public List<PerturbationLocation> getLocations() throws RemoteException {
		return PerturbationServerImpl.this.locations;
	}

	@Override
	public void enableLocation(PerturbationLocation location) throws RemoteException {
		location.setEnactor(new AlwaysEnactorImpl());
		loggers.get(NAME_LOGGER).logOn(location);
	}

	@Override
	public int getCalls(PerturbationLocation location) throws RemoteException {
		return loggers.get(NAME_LOGGER).getCalls(location);
	}

	@Override
	public int getEnactions(PerturbationLocation location) throws RemoteException {
		return PerturbationEngine.loggers.get(NAME_LOGGER).getEnactions(location);
	}

	@Override
	public void disableLocation(PerturbationLocation location) throws RemoteException {
		location.setEnactor(new NeverEnactorImpl());
		loggers.get(NAME_LOGGER).remove(location);
	}

	public void stopService() throws RemoteException {
		try {
			registry.unbind(PerturbationServerImpl.NAME_SERVER);
		} catch (NotBoundException ignored) {
		}
		UnicastRemoteObject.unexportObject(server, true);
	}

	/**
	 * Object RMI
	 */
	private static PerturbationServer server;

	private static Registry registry;

	/**
	 * Start the server in the new Thread.
	 */
	public static void startServer(final String project, final String packagePath) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					server = new PerturbationServerImpl(project, packagePath);
					UnicastRemoteObject.exportObject(server, PerturbationServerImpl.PORT);
					registry = LocateRegistry.createRegistry(PerturbationServerImpl.PORT);
					registry.rebind(PerturbationServerImpl.NAME_SERVER, server);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		}).start();
	}
}

