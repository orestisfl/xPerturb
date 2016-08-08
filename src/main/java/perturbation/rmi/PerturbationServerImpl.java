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
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import static java.lang.ClassLoader.getSystemClassLoader;

public class PerturbationServerImpl implements PerturbationServer {
	private static String removeExt(String name) {
		return name.substring(0, ((name.length()) - (".java".length())));
	}

	private static boolean isJava(String name) {
		return name.endsWith(".java");
	}

	private static ClassLoader loader = getSystemClassLoader();

	private String project;

	private String packagePath;

	public static final int PORT = 13223;

	private List<PerturbationLocation> locations;

	public PerturbationServerImpl(String project, String packagePath) {
		PerturbationServerImpl.this.project = project;
		PerturbationServerImpl.this.packagePath = packagePath;
		PerturbationServerImpl.this.locations = PerturbationServerImpl.getAllLocations(project, packagePath);
	}

	private static List<PerturbationLocation> getAllLocations(String project, String packagePath) {
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
				PerturbationServerImpl.iterateFolders(classes, ((path + (subFile.getName())) + "/"), ((currentPackage + ".") + (subFile.getName())));
			else if (PerturbationServerImpl.isJava(subFile.getName())) {
				try {
					Class<?> clazz = Class.forName(((currentPackage + ".") + (PerturbationServerImpl.removeExt(subFile.getName()))));
					classes.add(clazz);
				} catch (ClassNotFoundException e) {
					continue;
				}
			}
		}
		return classes;
	}

	@Override
	public List<PerturbationLocation> getAllLocations() throws RemoteException {
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

	public static void startServer(final String project, final String packagePath) {
		Thread thread = new Thread(new Runnable() {
			Registry registry;

			@Override
			public void run() {
				PerturbationServer skeleton = null;
				try {
					skeleton = ((PerturbationServer) (UnicastRemoteObject.exportObject(new PerturbationServerImpl(project, packagePath), PORT)));
				} catch (RemoteException e) {
					throw new RuntimeException(e);
				}
				try {
					LocateRegistry.getRegistry(PORT).list();
					registry = LocateRegistry.getRegistry(PORT);
				} catch (Exception ex) {
					try {
						registry = LocateRegistry.createRegistry(PORT);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
				try {
					registry.bind("Perturbation", skeleton);
				} catch (RemoteException e) {
					throw new RuntimeException(e);
				} catch (AlreadyBoundException e) {
					e.printStackTrace();
				}
			}
		});
		thread.run();
	}
}

