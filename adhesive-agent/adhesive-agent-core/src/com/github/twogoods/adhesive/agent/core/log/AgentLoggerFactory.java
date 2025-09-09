package com.github.twogoods.adhesive.agent.core.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgentLoggerFactory {
    public static Logger getLogger(String name) {
        return LoggerFactory.getLogger(name);
    }

    public static Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    public static boolean updateLevel(String name, String level) {
        Level l = Level.toLevel(level, Level.ERROR);
        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        if (loggerFactory instanceof LoggerContext) {
            LoggerContext loggerContext = (LoggerContext) loggerFactory;
            ch.qos.logback.classic.Logger logger = loggerContext.exists(name);
            if (logger != null) {
                logger.setLevel(l);
                return true;
            }
        }
        return false;
    }


}
