package com.github.twogoods.adhesive.agent.spy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author twogoods
 * @since 2024/11/22
 */
public class DubboSpy {
    public static Map<ClassLoader, DubboAdhesiveInvoker> dubboAdhesiveInvokers = new ConcurrentHashMap<>();
    public static Map<String, ClassLoader> providers = new ConcurrentHashMap<>();

    public static void registerInvoker(ClassLoader classLoader, DubboAdhesiveInvoker invoker) {
        dubboAdhesiveInvokers.put(classLoader, invoker);
    }

    public static void updateInvoker(ClassLoader classLoader, String iface, Object object) {
        providers.put(iface, classLoader);
        DubboAdhesiveInvoker invoker = dubboAdhesiveInvokers.get(classLoader);
        if (invoker != null) {
            invoker.update(object);
        }
    }

    public static boolean hasProvider(String iface) {
        return providers.containsKey(iface);
    }

    public static Object invoke(ClassLoader callerCl, String iface, String methodName, Object[] args) throws Throwable {
        ClassLoader providerCl = providers.get(iface);
        DubboAdhesiveInvoker invoker = dubboAdhesiveInvokers.get(providerCl);
        if (invoker != null) {
            return invoker.invoke(callerCl, providerCl, iface, methodName, args);
        }
        throw new RuntimeException("no invoker found");
    }
}