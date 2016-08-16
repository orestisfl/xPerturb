package perturbation.rmi;

import perturbation.location.PerturbationLocation;
import perturbation.location.PerturbationLocationImpl;
import perturbation.perturbator.AddOnePerturbatorImpl;
import perturbation.perturbator.InvPerturbatorImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bdanglot on 16/08/16.
 */
public class LocationsListBuilder {

	private static String removeExt(String name) {
		return name.substring(0, ((name.length()) - (".java".length())));
	}

	private static boolean isJava(String name) {
		return name.endsWith(".java");
	}

	static List<PerturbationLocation> buildLocationsList(String project, String packagePath) {
		final List<PerturbationLocation> locations = new ArrayList<PerturbationLocation>();
		List<Class> classes = iterateFolders(new ArrayList<Class>(), project, packagePath);
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

}
