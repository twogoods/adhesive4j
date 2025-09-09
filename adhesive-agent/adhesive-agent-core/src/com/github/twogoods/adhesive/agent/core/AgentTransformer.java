package com.github.twogoods.adhesive.agent.core;

import com.github.twogoods.adhesive.agent.core.instrumentation.MethodAdvice;
import com.github.twogoods.adhesive.agent.core.instrumentation.InstrumentationPlugin;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.named;

public class AgentTransformer {

    private AgentBuilder agentBuilder;
    private Instrumentation inst;


    public AgentTransformer(Instrumentation inst) {
        this.inst = inst;
        agentBuilder = new AgentBuilder.Default()
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
    }

    public List<ResettableClassFileTransformer> install(InstrumentationPlugin plugin) {
        List<ResettableClassFileTransformer> resettableClassFileTransformers = new ArrayList<>(plugin.getInstrumentations().size());
        plugin.getInstrumentations().forEach(typeInstrumentation -> {
            ResettableClassFileTransformer resettableClassFileTransformer = agentBuilder.type(typeInstrumentation.typeMatcher())
                    .transform(methodTransformer(typeInstrumentation.methodAdvices(),plugin.getClassLoader()))
                    .installOn(inst);
            resettableClassFileTransformers.add(resettableClassFileTransformer);
        });
        return resettableClassFileTransformers;
    }

    public AgentBuilder.Transformer methodTransformer(MethodAdvice[] methodAdvices, ClassLoader classLoader) {
        return new CommonTransformer(methodAdvices, classLoader);
    }

    static class CommonTransformer implements AgentBuilder.Transformer {
        private MethodAdvice[] methodAdvices;
        private ClassLoader classLoader;

        public CommonTransformer(MethodAdvice[] methodAdvices, ClassLoader classLoader) {
            this.methodAdvices = methodAdvices;
            this.classLoader = classLoader;
        }

        @Override
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, ProtectionDomain protectionDomain) {
            try {
                for (MethodAdvice methodAdvice : methodAdvices) {
                    Class clazz = classLoader.loadClass(methodAdvice.getAdviceName());
                    builder = builder.visit(Advice.to(clazz).on(methodAdvice.getMethodMatcher()));
                }
                return builder;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
