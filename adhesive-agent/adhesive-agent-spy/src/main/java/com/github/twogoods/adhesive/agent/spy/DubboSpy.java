package com.github.twogoods.adhesive.agent.spy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author twogoods
 * @since 2024/11/22
 */
public class DubboSpy {
    public static Map<ClassLoader, DubboAdhesiveInvoker> dubboAdhesiveInvokers = new ConcurrentHashMap<>();

    public static void registerInvoker(ClassLoader classLoader, DubboAdhesiveInvoker invoker) {
        System.out.println("--------------DubboSpy registerInvoker--------------");
        dubboAdhesiveInvokers.put(classLoader, invoker);
    }

    public static void updateInvoker(ClassLoader classLoader, Object object) {
        DubboAdhesiveInvoker invoker = dubboAdhesiveInvokers.get(classLoader);
        if (invoker != null) {
            invoker.update(object);
        }
    }

    public static boolean invokerExist(ClassLoader classLoader) {
        return dubboAdhesiveInvokers.containsKey(classLoader);
    }


    public static ClassLoader anotherClassloader(ClassLoader classLoader) {
        for (Map.Entry<ClassLoader, DubboAdhesiveInvoker> entry : dubboAdhesiveInvokers.entrySet()) {
            if (!entry.getKey().equals(classLoader)) {
                return entry.getKey();
            }
        }

        return classLoader;
    }


    public static Object invoke(ClassLoader classLoader, String iface, String methodName, Object[] args) throws Exception {
        DubboAdhesiveInvoker invoker = null;
        for (Map.Entry entry : dubboAdhesiveInvokers.entrySet()) {
            if (!entry.getKey().equals(classLoader)) {
                invoker = (DubboAdhesiveInvoker) entry.getValue();
                break;
            }
        }
        if (invoker != null) {
            return invoker.inboundInvoke(iface, methodName, args);
        }
        throw new RuntimeException("no invoker found");
    }
}