package com.github.twogoods.adhesive.agent.core.common;

import java.io.File;

public class AgentPathFinder {
    public static String findAgentPath() {
        String agentPath = new File(AgentPathFinder.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
        return agentPath;
    }
}
