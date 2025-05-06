package com.github.twogoods.adhesive.agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.jar.JarFile;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * @author twogoods
 * @since 2024/9/12
 */
public class Bootstrap {

    private static Map<ClassLoader, URLClassLoader> classLoaderMap = new ConcurrentHashMap<>();


    public static void premain(String args, Instrumentation inst) throws Exception {
        inst.appendToSystemClassLoaderSearch(new JarFile(new File("adhesive-agent/adhesive-agent-spy/target/adhesive-agent-spy-0.0.1.jar")));

        AgentBuilder agentBuilder = new AgentBuilder.Default()
                .disableClassFormatChanges()
                .with(new AgentBuilder.CircularityLock.Global())
                .ignore(named("xxx.yyy"))
//                .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .with(new AgentBuilder.Listener() {
                    @Override
                    public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {

                    }

                    @Override
                    public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded, DynamicType dynamicType) {

                    }

                    @Override
                    public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded) {

                    }

                    @Override
                    public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded, Throwable throwable) {
                        System.out.println("error  " + typeName);
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {

                    }
                });
        ResettableClassFileTransformer inboundTransformer = agentBuilder.type(
                        ElementMatchers.<TypeDescription>namedOneOf("org.apache.dubbo.rpc.protocol.dubbo.DubboProtocol"))
                .transform(new InboundTransformer())
                .installOn(inst);

        ResettableClassFileTransformer outboundTransformer = agentBuilder.type(
                        ElementMatchers.<TypeDescription>namedOneOf("org.apache.dubbo.rpc.protocol.dubbo.DubboInvoker"))
                .transform(new OutboundTransformer())
                .installOn(inst);

        ResettableClassFileTransformer httpInboundTransformer = agentBuilder.type(
                        ElementMatchers.<TypeDescription>namedOneOf("org.springframework.web.servlet.FrameworkServlet"))
                .transform(new HttpInboundTransformer())
                .installOn(inst);

        ResettableClassFileTransformer httpOutboundTransformer = agentBuilder.type(
                        ElementMatchers.<TypeDescription>namedOneOf("org.springframework.http.client.AbstractClientHttpRequest"))
                .transform(new HttpOutboundTransformer())
                .installOn(inst);

        ResettableClassFileTransformer loadOnStartupTransformer = agentBuilder.type(
                        ElementMatchers.<TypeDescription>namedOneOf("org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties$Servlet"))
                .transform(new LoadOnStartupTransformer())
                .installOn(inst);
    }

    public static void agentmain(String agentArgs, Instrumentation instrumentation) throws Exception {
        premain(agentArgs, instrumentation);
    }

    private static ExecutorService executorService = Executors.newFixedThreadPool(4);

    public static void main(String[] args) throws Exception {
        List<String> jars = new ArrayList<>();
        String provider = "././samples/provider/target/provider.jar";
        String consumer = "././samples/consumer/target/consumer.jar";
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                JarLauncher.launchJar(provider);
            }
        });
        Thread.sleep(20000);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                JarLauncher.launchJar(consumer);
            }
        });
    }

    public static ClassLoader getAdviceLoader(ClassLoader classLoader) {
        return classLoaderMap.computeIfAbsent(classLoader, cl -> {
            try {
                URL url = new File("adhesive-agent/adhesive-agent-plugin/target/adhesive-agent-plugin-0.0.1.jar").toURL();
                URLClassLoader loader = new URLClassLoader(new URL[]{url}, cl);
                Class clazz = loader.loadClass("com.github.twogoods.adhesive.agent.plugin.dubbo.DubboLocalInvoker");
                Method method = clazz.getDeclaredMethod("regist", ClassLoader.class);
                method.invoke(null, cl);

                clazz = loader.loadClass("com.github.twogoods.adhesive.agent.plugin.rest.HttpLocalInvoker");
                method = clazz.getDeclaredMethod("regist", ClassLoader.class);
                method.invoke(null, cl);

                return loader;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }


    static class InboundTransformer implements AgentBuilder.Transformer {
        @Override
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
            System.out.println(typeDescription.getCanonicalName() + " Transformer....");
            try {
                Class clazz = getAdviceLoader(classLoader).loadClass("com.github.twogoods.adhesive.agent.plugin.dubbo.DubboProtocolAdvice");
                return builder.visit(Advice.to(clazz).on(ElementMatchers.named("export").and(takesArguments(1))));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return builder;
        }
    }

    static class OutboundTransformer implements AgentBuilder.Transformer {
        @Override
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
            System.out.println(typeDescription.getCanonicalName() + " Transformer....");
            try {
                Class clazz = getAdviceLoader(classLoader).loadClass("com.github.twogoods.adhesive.agent.plugin.dubbo.DubboInvokerAdvice");
                return builder.visit(Advice.to(clazz).on(ElementMatchers.named("doInvoke")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return builder;
        }
    }

    static class HttpInboundTransformer implements AgentBuilder.Transformer {
        @Override
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
            System.out.println(typeDescription.getCanonicalName() + " Transformer....");
            try {
                Class clazz = getAdviceLoader(classLoader).loadClass("com.github.twogoods.adhesive.agent.plugin.springmvc.DispatcherServletAdvice");
                return builder.visit(Advice.to(clazz).on(ElementMatchers.named("setDispatchOptionsRequest").and(takesArguments(1))));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return builder;
        }
    }

    static class HttpOutboundTransformer implements AgentBuilder.Transformer {
        @Override
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
            System.out.println(typeDescription.getCanonicalName() + " Transformer....");
            try {
                Class clazz = getAdviceLoader(classLoader).loadClass("com.github.twogoods.adhesive.agent.plugin.rest.ClientHttpRequestAdvice");
                return builder.visit(Advice.to(clazz).on(ElementMatchers.named("execute")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return builder;
        }
    }

    static class LoadOnStartupTransformer implements AgentBuilder.Transformer {
        @Override
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
            System.out.println(typeDescription.getCanonicalName() + " Transformer....");
            try {
                Class clazz = getAdviceLoader(classLoader).loadClass("com.github.twogoods.adhesive.agent.plugin.springmvc.LoadOnStartupAdvice");
                return builder.visit(Advice.to(clazz).on(ElementMatchers.named("getLoadOnStartup")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return builder;
        }
    }
}
