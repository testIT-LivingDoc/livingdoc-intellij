package org.livingdoc.intellij.domain;

import org.junit.Ignore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

@Ignore
public class ClassUtils extends org.apache.commons.lang3.ClassUtils {

    public static Set<Class<?>> getClasses(final String packageName) throws Exception {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return getClasses(loader, packageName);
    }

    private static Set<Class<?>> getClasses(final ClassLoader loader, final String packageName)
            throws IOException, ClassNotFoundException {

        final Set<Class<?>> classes = new HashSet<Class<?>>();
        final String path = packageName.replace('.', '/');
        final Enumeration<URL> resources = loader.getResources(path);
        if (resources != null) {
            while (resources.hasMoreElements()) {
                String filePath = resources.nextElement().getFile();
                // WINDOWS HACK
                if (filePath.indexOf("%20") > 0) {
                    filePath = filePath.replaceAll("%20", " ");
                }
                if (filePath != null) {
                    if ((filePath.indexOf("!") > 0) & (filePath.indexOf(".jar") > 0)) {
                        String jarPath = filePath.substring(0, filePath.indexOf("!")).substring(
                                filePath.indexOf(":") + 1);
                        // WINDOWS HACK
                        if (jarPath.indexOf(":") >= 0) {
                            jarPath = jarPath.substring(1);
                        }
                        classes.addAll(getFromJARFile(jarPath, path));
                    } else {
                        classes.addAll(getFromDirectory(new File(filePath), packageName));
                    }
                }
            }
        }
        return classes;
    }

    private static Set<Class<?>> getFromJARFile(final String jar, final String packageName)
            throws ClassNotFoundException, IOException {

        final Set<Class<?>> classes = new HashSet<Class<?>>();
        final JarInputStream jarFile = new JarInputStream(new FileInputStream(jar));
        JarEntry jarEntry;
        do {
            jarEntry = jarFile.getNextJarEntry();
            if (jarEntry != null) {
                String className = jarEntry.getName();
                if (className.endsWith(".class")) {
                    className = stripFilenameExtension(className);
                    if (className.startsWith(packageName)) {
                        classes.add(Class.forName(className.replace('/', '.')));
                    }
                }
            }
        }
        while (jarEntry != null);
        jarFile.close();
        return classes;
    }

    private static Set<Class<?>> getFromDirectory(final File directory, final String packageName)
            throws ClassNotFoundException {

        final Set<Class<?>> classes = new HashSet<Class<?>>();
        if (directory.exists()) {
            for (final String file : directory.list()) {
                if (file.endsWith(".class")) {
                    final String name =
                            packageName + '.' + stripFilenameExtension(file);
                    final Class<?> clazz = Class.forName(name);
                    classes.add(clazz);
                }
            }
        }
        return classes;
    }

    private static String stripFilenameExtension(final String file) {
        return file.substring(0, file.length() - (".class".length()));
    }

}
