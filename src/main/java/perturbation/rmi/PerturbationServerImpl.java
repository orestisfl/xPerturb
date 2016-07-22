package perturbation.rmi;

import perturbation.enactor.AlwaysEnactorImpl;
import perturbation.enactor.NeverEnactorImpl;
import perturbation.location.PerturbationLocation;
import perturbation.location.PerturbationLocationImpl;
import perturbation.perturbator.AddOnePerturbatorImpl;
import perturbation.perturbator.InvPerturbatorImpl;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bdanglot on 20/07/16.
 */
public class PerturbationServerImpl implements PerturbationServer {

	private static String removeExt(String name) {
		return name.substring(0, name.length() - ".java".length());
	}

	private static boolean isJava(String name) {
		return name.endsWith(".java");
	}

	private static ClassLoader loader = ClassLoader.getSystemClassLoader();

	private String project;
	private String packagePath;

	private List<PerturbationLocation> locations;

	public PerturbationServerImpl(String project, String packagePath) {
		this.project = project;
		this.packagePath = packagePath;
		this.locations = getAllLocations(project, packagePath);
	}

	private static List<PerturbationLocation> getAllLocations(String project, String packagePath) {
		final List<PerturbationLocation> locations = new ArrayList<PerturbationLocation>();
		List<Class> classes = iterateFolders(new ArrayList<Class>(), project, packagePath);
		for (int i = 0; i < classes.size(); i++) {
			Class clazz = classes.get(i);
			List<PerturbationLocation> locationFromClass = PerturbationLocationImpl.getLocationFromClass(clazz);
			for (int j = 0; j < locationFromClass.size(); j++) {
				PerturbationLocation location = locationFromClass.get(j);
				locations.add(location);
				location.setPerturbator(new AddOnePerturbatorImpl(new InvPerturbatorImpl()));
			}
		}

		return locations;
	}
	private static List<Class> iterateFolders(List<Class> classes, String path, String currentPackage) {
		File root = new File(path);
		assert root.listFiles() != null;
		for (File subFile : root.listFiles()) {
			if (subFile.isDirectory())
				iterateFolders(classes, path + subFile.getName() + "/", currentPackage + "." + subFile.getName());
			else if (isJava(subFile.getName())) {
				try {
//                    Class<?> clazz = Class.forName(currentPackage + "." + removeExt(subFile.getName()));
					Class<?> clazz = loader.loadClass(currentPackage + "." + removeExt(subFile.getName()));
//                    System.out.println(currentPackage + "." + removeExt(subFile.getName()));
					classes.add(clazz);
				} catch (ClassNotFoundException e) {
//                    e.printStackTrace();
					continue;
				}
			}
		}
		return classes;
	}

	@Override
	public List<PerturbationLocation> getAllLocations() throws RemoteException {
		return this.locations;
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
				int port = 8023;
				try {
					skeleton = (PerturbationServer) UnicastRemoteObject.exportObject(new PerturbationServerImpl(project, packagePath), port);
				} catch (RemoteException e) {
					throw new RuntimeException(e);
				}
				try {
					LocateRegistry.getRegistry(port).list();
					registry = LocateRegistry.getRegistry(port);
				} catch (Exception ex) {
					try {
						registry = LocateRegistry.createRegistry(port);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
				try {
					registry.rebind("Perturbation", skeleton);
				} catch (RemoteException e) {
					throw new RuntimeException(e);
				}
			}
		});
		thread.run();
	}

}
