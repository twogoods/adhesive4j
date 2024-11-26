package com.github.twogoods.adhesive.agent;

import java.io.File;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * @author twogoods
 * @since 2024/9/12
 */
public class JarLauncher {
    public static WaeverAgentBootstrapClassLoader launchJar(String jarPath) {
        try {
            JarFile jarFile = new JarFile(jarPath);
            Manifest manifest = jarFile.getManifest();
            String mainClass = manifest.getMainAttributes().getValue("Main-Class");

            WaeverAgentBootstrapClassLoader bootstrapClassLoader = new WaeverAgentBootstrapClassLoader(new URL[]{new File(jarPath).toURL()});
            Class main = bootstrapClassLoader.loadClass(mainClass);
            main.getMethod("main", String[].class).invoke(null, (Object) new String[0]);
            return bootstrapClassLoader;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
