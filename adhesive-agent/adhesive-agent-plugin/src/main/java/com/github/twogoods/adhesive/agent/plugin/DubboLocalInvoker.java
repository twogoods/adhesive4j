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
    public Object inboundInvoke(String iface, String methodName, Object[] args) throws Exception {
        for (Exporter exporter : dubboProtocol.getExporters()) {
            Invoker invoker = exporter.getInvoker();
            if (invoker.getUrl().getServiceInterface().equals(iface)) {
                Class[] paramType = new Class[args.length];
                for (int i = 0; i < args.length; i++) {
                    paramType[i] = args[i].getClass();
                }
                Invocation invocation = new RpcInvocation(iface, null, methodName, iface, null, paramType, args, new HashMap<>(), invoker, new HashMap<>(), null);
                Result res = invoker.invoke(invocation);
                if (res.getException() != null) {
                    res.getException().printStackTrace();
                } else {
                    System.out.println("result---" + res.getValue());
                    return res.getValue();
                }
            }
        }
        throw new Exception("iface not exist");
    }
}
