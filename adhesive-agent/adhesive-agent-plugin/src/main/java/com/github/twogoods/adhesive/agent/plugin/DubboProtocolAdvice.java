package com.github.twogoods.adhesive.agent.plugin;

import com.github.twogoods.adhesive.agent.spy.DubboSpy;
import net.bytebuddy.asm.Advice;
import org.apache.dubbo.rpc.Invoker;

/**
 * @author twogoods
 * @since 2024/11/22
 */
public class DubboProtocolAdvice {
    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void export(@Advice.Argument(0) Invoker invoker, @Advice.This Object dubboProtocol) {
        System.out.println("--------------invoker export--------------");
        String iface = invoker.getUrl().getServiceInterface();
        ClassLoader classLoader = dubboProtocol.getClass().getClassLoader();
        try {
            DubboSpy.updateInvoker(classLoader, iface, dubboProtocol);
        } catch (Error e) {
            e.printStackTrace();
        }
    }
}
