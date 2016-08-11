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

import perturbation.enactor.AlwaysEnactorImpl;
import perturbation.enactor.NeverEnactorImpl;
import perturbation.location.PerturbationLocation;
import perturbation.location.PerturbationLocationImpl;
import perturbation.perturbator.AddOnePerturbatorImpl;
import perturbation.perturbator.InvPerturbatorImpl;

import java.io.File;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class PerturbationServerImpl implements PerturbationServer {

	public static final int PORT = 13223;

	public static final String NAME = "Perturbation";

	private List<PerturbationLocation> locations;

	public PerturbationServerImpl(String project, String packagePath) {
		this.locations = PerturbationServerImpl.buildLocationsList(project, packagePath);
	}

	private static String removeExt(String name) {
		return name.substring(0, ((name.length()) - (".java".length())));
	}

	private static boolean isJava(String name) {
		return name.endsWith(".java");
	}

	private static List<PerturbationLocation> buildLocationsList(String project, String packagePath) {
		final List<PerturbationLocation> locations = new ArrayList<PerturbationLocation>();
		List<Class> classes = PerturbationServerImpl.iterateFolders(new ArrayList<Class>(), project, packagePath);
		for (int i = 0; i < (classes.size()); i++) {
			Class clazz = classes.get(i);
			List<PerturbationLocation> locationFromClass = PerturbationLocationImpl.getLocationFromClass(clazz);
			for (int j = 0; j < (locationFromClass.size()); j++) {
				PerturbationLocation location = locationFromClass.get(j);
				locations.add(location);
				location.setPerturbator(new AddOnePerturbatorImpl(new InvPerturbatorImpl()));
			}
		}
		return locations;
	}

	private static List<Class> iterateFolders(List<Class> classes, String path, String currentPackage) {
		File root = new File(path);
		assert (root.listFiles()) != null;
		for (File subFile : root.listFiles()) {
			if (subFile.isDirectory())
				iterateFolders(classes, ((path + (subFile.getName())) + "/"), ((currentPackage + ".") + (subFile.getName())));
			else if (isJava(subFile.getName())) {
				try {
					String packageAsString = currentPackage.isEmpty() ? "" : currentPackage + ".";
					Class<?> clazz = Class.forName(packageAsString + removeExt(subFile.getName()));
					classes.add(clazz);
				} catch (ClassNotFoundException e) {
					continue;
				}
			}
		}
		return classes;
	}

	public List<PerturbationLocation> getLocations() throws RemoteException {
		return PerturbationServerImpl.this.locations;
	}

	@Override
	public void enableLocation(PerturbationLocation location) throws RemoteException {
		location.setEnactor(new AlwaysEnactorImpl());
	}

	@Override
	public void disableLocation(PerturbationLocation location) throws RemoteException {
		location.setEnactor(new NeverEnactorImpl());
	}

	public void stopService() throws RemoteException {
		try {
			registry.unbind(PerturbationServerImpl.NAME);
		} catch (NotBoundException ignored) {}
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
					registry.rebind(PerturbationServerImpl.NAME, server);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		}).start();
	}
}

