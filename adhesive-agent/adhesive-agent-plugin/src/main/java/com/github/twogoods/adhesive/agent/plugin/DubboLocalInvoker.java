package com.github.twogoods.adhesive.agent.plugin;

import com.github.twogoods.adhesive.agent.spy.DubboSpy;
import com.github.twogoods.adhesive.agent.spy.DubboAdhesiveInvoker;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.protocol.dubbo.DubboProtocol;

import java.lang.reflect.Constructor;
import java.util.HashMap;

/**
 * @author twogoods
 * @since 2024/11/22
 */
public class DubboLocalInvoker implements DubboAdhesiveInvoker {
    public DubboProtocol dubboProtocol;

    public DubboLocalInvoker() {
    }

    public static void regist(ClassLoader classLoader) {
        DubboSpy.registerInvoker(classLoader, new DubboLocalInvoker());
    }

    @Override
    public boolean localExist(String iface) {
        return false;
    }

    @Override
    public void update(Object dubbo) {
        if (dubbo instanceof DubboProtocol) {
            this.dubboProtocol = (DubboProtocol) dubbo;
        }
    }

    @Override
    public Object invoke(ClassLoader callerCl, ClassLoader providerCl, String iface, String methodName, Object[] args) throws Throwable {
        Invoker invoker = null;
        for (Exporter exporter : dubboProtocol.getExporters()) {
            if (exporter.getInvoker().getUrl().getServiceInterface().equals(iface)) {
                invoker = exporter.getInvoker();
                break;
            }
        }
        if (invoker == null) {
            throw new Exception("iface not exist");
        }

        Object[] params = convertParams(callerCl, providerCl, args);
        Class[] paramType = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            paramType[i] = params[i].getClass();
        }
        Invocation invocation = new RpcInvocation(iface, null, methodName, iface, null,
                paramType, params, new HashMap<>(), invoker, new HashMap<>(), null);
        Result res = invoker.invoke(invocation);
        if (res.getException() != null) {
            res.getException().printStackTrace();
            throw convertException(callerCl, providerCl, res.getException());
        } else {
            System.out.println("result---" + res.getValue());
            return convertResult(callerCl, providerCl, res.getValue());
        }
    }

    private Object[] convertParams(ClassLoader callerCl, ClassLoader providerCl, Object[] args) {
        //TODO 不同classloader下的pojo转换，直接走一下序列化
        try {
            Class clazz = providerCl.loadClass("com.github.twogoods.iface.User");
            Constructor constructor = clazz.getConstructor(String.class, int.class);
            Object param = constructor.newInstance("d-t", 1212);
            return new Object[]{param};
        } catch (Exception e) {
            e.printStackTrace();
        }
        return args;
    }

    private Object convertResult(ClassLoader callerCl, ClassLoader providerCl, Object res) {
        if (res.getClass().getClassLoader() == null) {
            return res;
        } else {
            //TODO 不同classloader下的pojo转换，直接走一下序列化
        }
        return res;
    }

    private Throwable convertException(ClassLoader callerCl, ClassLoader providerCl, Throwable throwable) {
        if (throwable.getClass().getClassLoader() == null) {
            return throwable;
        } else {
            //TODO 不同classloader下的pojo转换，直接走一下序列化
        }
        return throwable;
    }
}
