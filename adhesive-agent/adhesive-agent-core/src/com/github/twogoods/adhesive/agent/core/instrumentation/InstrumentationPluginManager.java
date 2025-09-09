package com.github.twogoods.adhesive.agent.core.instrumentation;


import com.github.twogoods.adhesive.agent.core.AgentTransformer;
import com.github.twogoods.adhesive.agent.core.log.AgentLoggerFactory;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import org.slf4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstrumentationPluginManager {
    private static final Logger LOGGER = AgentLoggerFactory.getLogger(InstrumentationPluginManager.class);
    private static List<InstrumentationPlugin> instrumentationPlugins = new ArrayList<>();

    private static Map<InstrumentationPlugin, List<ResettableClassFileTransformer>> installedPlugins = new HashMap<>();

    public static void initPlugins(String agentPath, List<String> enabledPlugins) {
        //TODO load from path
        enabledPlugins.forEach(p -> {
            try {
                URL url = new File(agentPath + p + ".jar").toURI().toURL();
                InstrumentationPlugin plugin = new InstrumentationPlugin(agentPath + p, url);
                instrumentationPlugins.add(plugin);
            } catch (Exception e) {
                LOGGER.error("Find plugin {} error", p, e);
            }
        });
    }

    public static void installPlugins(AgentTransformer transformer) {
        for(InstrumentationPlugin p: instrumentationPlugins){
            p.loadInstrumentations(InstrumentationPluginManager.class.getClassLoader());
            installedPlugins.put(p, transformer.install(p));
        }
    }

    public void resetPlugin(String pluginName) {

    }
}
