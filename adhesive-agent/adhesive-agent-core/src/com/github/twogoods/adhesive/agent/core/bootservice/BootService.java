package com.github.twogoods.adhesive.agent.core.bootservice;

public interface BootService {

    String name();

    void start();

    void stop();

    default int priority() {
        return 0;
    }
}
