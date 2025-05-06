package com.github.twogoods.adhesive.agent.plugin.springmvc;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
/**
 * @author luhaoshuai@bytedance.com
 * @since 2025/4/25
 */
public class LoadOnStartupAdvice {
    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void constructor(@Advice.Return(readOnly = false, typing = Assigner.Typing.DYNAMIC) int result) {
        System.out.println("load on startup。。。");
        result = 1;
    }
}
