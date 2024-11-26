package com.github.twogoods.adhesive.agent.spy;

/**
 * @author twogoods
 * @since 2024/11/22
 */
public interface DubboAdhesiveInvoker {

    boolean localExist(String iface);

    void update(Object dubbo);

    Object inboundInvoke(String iface, String methodName, Object[] args) throws Exception;
}