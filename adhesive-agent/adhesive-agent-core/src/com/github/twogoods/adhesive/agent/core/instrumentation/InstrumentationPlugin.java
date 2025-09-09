package com.github.twogoods.adhesive.agent.core.instrumentation;

import com.github.twogoods.adhesive.agent.core.classloader.InstrumentationPluginClassLoader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class InstrumentationPlugin {
    private String name;
    private URL path;

    private ClassLoader classLoader;
    private List<TypeInstrumentation> instrumentations=new ArrayList<>();

    public InstrumentationPlugin(String name, URL path) {
        this.name = name;
        this.path = path;
    }

    public void loadInstrumentations(ClassLoader parentClassLoader) {
        classLoader = new InstrumentationPluginClassLoader(new URL[]{path}, parentClassLoader);
        Iterable<? extends TypeInstrumentation> typeInstrumentations = ServiceLoader.load(TypeInstrumentation.class, classLoader);
        for (TypeInstrumentation instrumentation : typeInstrumentations) {
            instrumentations.add(instrumentation);
        }
    }

    public List<TypeInstrumentation> getInstrumentations() {
        return instrumentations;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }
}
