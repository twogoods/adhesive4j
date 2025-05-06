package com.github.twogoods.adhesive.agent.spy.http;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author luhaoshuai@bytedance.com
 * @since 2025/4/21
 */
public class HttpSpy {
    public static Map<ClassLoader, HttpAdhesiveInvoker> adhesiveInvokers = new ConcurrentHashMap<>();
    public static Map<String, ClassLoader> providers = new ConcurrentHashMap<>();

    public static void registerInvoker(ClassLoader classLoader, HttpAdhesiveInvoker invoker) {
        adhesiveInvokers.put(classLoader, invoker);
    }

    public static void updateInvoker(ClassLoader classLoader, Object object) {
        HttpAdhesiveInvoker invoker = adhesiveInvokers.get(classLoader);
        if (invoker != null) {
            invoker.update(object);
        }
    }

    public static boolean hasProvider(String serviceName) {
        return providers.containsKey(serviceName);
    }

    public static HttpResponse invoke(ClassLoader callerCl, HttpRequest request) throws Throwable {
//        ClassLoader providerCl = providers.get(request.uri.getAuthority());
//        HttpAdhesiveInvoker invoker = adhesiveInvokers.get(providerCl);
        ClassLoader providerCl = null;
        HttpAdhesiveInvoker invoker = null;

        for (Map.Entry entry : adhesiveInvokers.entrySet()) {
            if (entry.getKey() != callerCl) {
                providerCl = (ClassLoader) entry.getKey();
                invoker = (HttpAdhesiveInvoker) entry.getValue();
            }
        }

        if (invoker != null) {
            return invoker.invoke(callerCl, providerCl, request);
        }
        throw new RuntimeException("no invoker found");
    }



    public static Object convertRestResponse(ClassLoader callerCl, HttpResponse resp) {
        return adhesiveInvokers.get(callerCl).convertRestResponse(resp);
    }
}
