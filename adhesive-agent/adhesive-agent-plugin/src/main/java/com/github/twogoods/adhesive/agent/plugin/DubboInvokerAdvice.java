package com.github.twogoods.adhesive.agent.plugin;

import com.github.twogoods.adhesive.agent.spy.DubboSpy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import org.apache.dubbo.rpc.AppResponse;
import org.apache.dubbo.rpc.AsyncRpcResult;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Result;

import java.lang.reflect.Constructor;
import java.util.concurrent.CompletableFuture;

/**
 * @author twogoods
 * @since 2024/11/22
 */
public class DubboInvokerAdvice {
    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    public static boolean doInvoke(@Advice.Argument(0) Invocation invocation) {
        System.out.println("--------------DubboInvokerAdvice enter skip--------------");
        return true;
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void exit(@Advice.Argument(0) Invocation invocation,
                            @Advice.Return(readOnly = false, typing = Assigner.Typing.DYNAMIC) Result result) {
        System.out.println("--------------DubboInvokerAdvice exit--------------");
        try {
            ClassLoader classLoader = invocation.getClass().getClassLoader();
            //TODO invocation.getArguments()
            ClassLoader cl2 = DubboSpy.anotherClassloader(classLoader);
            Class clazz = cl2.loadClass("com.github.twogoods.iface.User");
            Constructor constructor = clazz.getConstructor(String.class, int.class);
            Object param = constructor.newInstance("d-t", 1212);
            Object obj = DubboSpy.invoke(classLoader, invocation.getServiceName(), invocation.getMethodName(), new Object[]{param});
            result = new AsyncRpcResult(CompletableFuture.completedFuture(new AppResponse(obj)), invocation);
        } catch (Exception e) {
            result = new AsyncRpcResult(CompletableFuture.completedFuture(new AppResponse(e)), invocation);
        }
    }
}
