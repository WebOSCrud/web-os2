package cn.donting.web.os.web.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PackageScanner {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String packageName = "cn.donting.web.os.web"; // 指定要扫描的包路径

        List<Class> classes = scanPackage(packageName);

        // 打印扫描到的类名
        for (Class<?> clazz : classes) {
            System.out.println(clazz.getName());
        }
    }

    public static List<Class> scanPackage(String packageName) throws IOException, ClassNotFoundException {
        List<Class> classes = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');

        Enumeration<URL> resources = classLoader.getResources(path);
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getProtocol().equals("file")) {
                // 处理文件系统中的类文件
                File file = new File(resource.getFile());
                classes.addAll(scanDirectory(packageName, file));
            } else if (resource.getProtocol().equals("jar")) {
                // 处理 JAR 文件中的类文件
                JarURLConnection jarConnection = (JarURLConnection) resource.openConnection();
                JarFile jarFile = jarConnection.getJarFile();
                classes.addAll(scanJar(packageName, jarFile));
            }
        }

        return classes;
    }

    private static List<Class<?>> scanDirectory(String packageName, File directory) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return classes;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(scanDirectory(packageName + "." + file.getName(), file));
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                classes.add(Class.forName(className));
            }
        }

        return classes;
    }

    private static List<Class<?>> scanJar(String packageName, JarFile jarFile) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (entry.isDirectory() || !entry.getName().endsWith(".class")) {
                continue;
            }
            String className = entry.getName().substring(0, entry.getName().length() - 6)
                    .replace('/', '.');
            if (className.startsWith(packageName)) {
                classes.add(Class.forName(className));
            }
        }
        return classes;
    }
}
