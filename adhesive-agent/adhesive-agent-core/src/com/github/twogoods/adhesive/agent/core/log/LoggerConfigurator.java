package com.github.twogoods.adhesive.agent.core.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.util.FileSize;
import com.github.twogoods.adhesive.agent.core.util.StringUtils;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.charset.StandardCharsets;

public class LoggerConfigurator extends ContextAwareBase implements Configurator {
    @Override
    public ExecutionStatus configure(LoggerContext loggerContext) {
        this.addInfo("Setting up default configuration.");
        Logger rootLogger = loggerContext.getLogger("ROOT");
        if ("true".equals(getProperty("mse.javaagent.debug", "MSE_JAVAAGENT_DEBUG", StringUtils.EMPTY))) {
            rootLogger.setLevel(Level.DEBUG);
        } else {
            customizeLogLevel(rootLogger);
        }
        startConsoleAppender(rootLogger);
        if ("true".equals(getProperty("mse.log.file", "MSE_LOG_FILE", StringUtils.EMPTY))) {
            startRollingFileAppender(rootLogger);
        }
        return ExecutionStatus.DO_NOT_INVOKE_NEXT_IF_ANY;
    }

    private void customizeLogLevel(Logger rootLogger) {
        String logLevel = getProperty("mse.log.level", "MSE_LOG_LEVEL", "INFO");
        rootLogger.setLevel(Level.valueOf(logLevel));
    }

    private void startConsoleAppender(Logger rootLogger) {
        ConsoleAppender<ILoggingEvent> ca = new ConsoleAppender();
        ca.setContext(this.context);
        ca.setName("console");
        LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder();
        encoder.setContext(this.context);
        PatternLayout layout = new PatternLayout();
        layout.setPattern("[mse.javaagent %d{yyyy-MM-dd HH:mm:ss.SSS Z}] %magenta([%thread]) %highlight(%-5level) %cyan(%logger) %blue(%line) - %msg%n");
        layout.setContext(this.context);
        layout.start();
        encoder.setLayout(layout);
        ca.setEncoder(encoder);
        ca.start();
        rootLogger.addAppender(ca);
    }

    private void startRollingFileAppender(Logger rootLogger) {
        String path = getProperty("mse.log.path", "MSE_LOG_PATH", "/otel-auto-instrumentation/log");
        String application = getProperty("mse.service.name", "MSE_SERVICE_NAME", "UNKNOW");
        String maxHistory = getProperty("mse.log.maxHistory", "MSE_LOG_MAXHISTORY", "30");
        String fileSize = getProperty("mse.log.fileSize", "MSE_LOG_FILESIZE", "1GB");
        String totalSize = getProperty("mse.log.totalSize", "MSE_LOG_TOTALSIZE", "10GB");

        String filename = String.format("%s-%s-", application, getPid()) + "agent-%d{yyyy-MM-dd}.%i.log";
        if (path.endsWith(File.separator)) {
            path = path + filename;
        } else {
            path = path + "/" + filename;
        }

        PatternLayoutEncoder patternLayoutEncoder = new PatternLayoutEncoder();
        patternLayoutEncoder.setContext(this.context);
        patternLayoutEncoder.setCharset(StandardCharsets.UTF_8);
        patternLayoutEncoder.setPattern("[mse.javaagent %d{yyyy-MM-dd HH:mm:ss.SSS Z}] %magenta([%thread]) %highlight(%-5level) %cyan(%logger) %blue(%line) - %msg%n");

        RollingFileAppender appender = new RollingFileAppender<PatternLayoutEncoder>();
        appender.setContext(this.context);
        appender.setEncoder(patternLayoutEncoder);
        appender.setName("agentFileAppender");
        appender.setAppend(true);
        appender.setPrudent(true);

        SizeAndTimeBasedRollingPolicy policy = new SizeAndTimeBasedRollingPolicy<>();
        policy.setContext(this.context);
        policy.setMaxFileSize(FileSize.valueOf(fileSize));
        policy.setFileNamePattern(path);
        policy.setMaxHistory(Integer.parseInt(maxHistory));
        policy.setTotalSizeCap(FileSize.valueOf(totalSize));
        policy.setParent(appender);

        appender.setRollingPolicy(policy);

        patternLayoutEncoder.start();
        policy.start();
        appender.start();
        rootLogger.addAppender(appender);
    }

    private String getPid() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String name = runtime.getName();
        return name.substring(0, name.indexOf('@'));
    }

    private String getProperty(String propertyKey, String envKey, String defaultValue) {
        String value = System.getProperty(propertyKey);
        if (StringUtils.isEmpty(value)) {
            value = System.getenv(envKey);
        }
        if (StringUtils.isEmpty(value)) {
            value = defaultValue;
        }
        return value;
    }
}
