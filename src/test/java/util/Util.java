package util;

import processor.AssignmentProcessor;
import processor.LocalVariableProcessor;
import spoon.Launcher;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by spirals on 07/03/16.
 */
public class Util {

    public static Launcher createSpoon() {
        Launcher launcher = new Launcher();

        launcher.addProcessor(new AssignmentProcessor());
        launcher.addProcessor(new LocalVariableProcessor());

//        launcher.addProcessor(new VariableCaster());

        launcher.getEnvironment().setShouldCompile(true);
        launcher.getEnvironment().setAutoImports(true);
        launcher.setBinaryOutputDirectory("spooned/bin");

        return launcher;
    }

    public static void addPathToClassPath(URL u) {

        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class sysclass = URLClassLoader.class;

        try {
            Method method = sysclass.getDeclaredMethod("addURL", new Class[]{URL.class});
            method.setAccessible(true);
            method.invoke(sysloader, new Object[]{u});
        } catch (Throwable t) {
            t.printStackTrace();
            throw new RuntimeException("Error, could not add URL to system classloader");
        }
    }

    public static Object execMethod(String name, Class<?> aClass, Object o) throws Exception {
        Method method = aClass.getMethod(name);
        return method.invoke(o);
    }

}
