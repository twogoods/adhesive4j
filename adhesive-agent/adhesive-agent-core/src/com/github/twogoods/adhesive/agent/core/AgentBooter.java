package com.github.twogoods.adhesive.agent.core;

import com.github.twogoods.adhesive.agent.core.common.AgentPathFinder;
import com.github.twogoods.adhesive.agent.core.common.ConfigManager;
import com.github.twogoods.adhesive.agent.core.instrumentation.InstrumentationPluginManager;

import java.lang.instrument.Instrumentation;
import java.util.List;

public class AgentBooter {
    public static void boot(String args, Instrumentation inst) {
        String agentPath = AgentPathFinder.findAgentPath();
        List<String> enabledPlugins = ConfigManager.enabledPlugins();
        InstrumentationPluginManager.initPlugins(agentPath, enabledPlugins);
        AgentTransformer agentTransformer = new AgentTransformer(inst);
        InstrumentationPluginManager.installPlugins(agentTransformer);
    }
}
