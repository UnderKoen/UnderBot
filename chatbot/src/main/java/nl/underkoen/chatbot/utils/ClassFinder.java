package nl.underkoen.chatbot.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * Created by Under_Koen on 20/03/2019.
 */
public class ClassFinder {
    public static Collection<Class> getClassesIn(String packageName) {
        try {
            Collection<Class> classes = getClassesInRaw(packageName);
            if (classes != null) return classes;
        } catch (Exception ignored) { }
        return new ArrayList<>();
    }

    public static Collection<Class> getClassesInRaw(String packageName) throws IOException, URISyntaxException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        Set<File> dirs = new LinkedHashSet<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            URI uri = new URI(resource.toString());
            String uriPath = uri.getPath();
            if (uriPath != null) {
                dirs.add(new File(uriPath));
            }
        }
        Set<Class> classes = new HashSet<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }

    public static Collection<Class> findClasses(File directory, String packageName) {
        String classExtension = ".class";

        Set<Class> classes = new HashSet<>();
        File[] files;
        if (!directory.exists() || (files = directory.listFiles()) == null) {
            return classes;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(classExtension)) {
                Class cls = findOrLoad((packageName + '.' + file.getName().replaceAll(classExtension + "$", "")).replaceAll("^\\.", ""));
                if (cls != null) {
                    classes.add(cls);
                }
            }
        }
        return classes;
    }

    public static Class findOrLoad(String className) {
        Class cls;
        try {
            cls = Class.forName(className);
        } catch (Exception ex) {
            cls = null;
        }
        return cls;
    }

}
