package com.github.twogoods.adhesive.agent.core.classloader;

import java.net.URL;
import java.net.URLClassLoader;

public class InstrumentationPluginClassLoader extends URLClassLoader {

    public InstrumentationPluginClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }
}
