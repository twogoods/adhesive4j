package com.github.twogoods.adhesive.agent.plugin;

import com.github.twogoods.adhesive.agent.spy.DubboSpy;
import com.github.twogoods.adhesive.agent.spy.DubboAdhesiveInvoker;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.protocol.dubbo.DubboProtocol;

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

        Object[] params = convertParams(providerCl, args);
        Class[] paramType = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            paramType[i] = params[i].getClass();
        }
        Invocation invocation = new RpcInvocation(iface, null, methodName, iface, null,
                paramType, params, new HashMap<>(), invoker, new HashMap<>(), null);
        Result res = invoker.invoke(invocation);
        if (res.getException() != null) {
            res.getException().printStackTrace();
            throw convertException(callerCl, res.getException());
        } else {
            System.out.println("result---" + res.getValue());
            return convertObj(callerCl, res.getValue());
        }
    }

    private Object[] convertParams(ClassLoader cl, Object[] args) throws Exception {
        Object[] params = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            params[i] = convertObj(cl, args[i]);
        }
        return params;
    }

    private Object convertObj(ClassLoader cl, Object res) throws Exception {
        if (res.getClass().getClassLoader() == null) {
            return res;
        } else {
            byte[] bytes = SerializationUtils.serialize(res);
            return SerializationUtils.deserialize(bytes, type(cl, res.getClass()));
        }
    }

    private Throwable convertException(ClassLoader cl, Throwable throwable) throws Exception {
        if (throwable.getClass().getClassLoader() == null) {
            return throwable;
        } else {
            RpcException rpcException = new RpcException(throwable.getMessage(), throwable);
            return rpcException;
//            byte[] bytes = SerializationUtils.serialize(throwable);
//            return (Throwable) SerializationUtils.deserialize(bytes, type(cl, throwable.getClass()));
        }
    }

    private Class type(ClassLoader cl, Class clazz) throws ClassNotFoundException {
        return cl.loadClass(clazz.getName());
    }
}
