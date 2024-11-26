package com.github.twogoods.adhesive.agent;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author twogoods
 * @since 2024/9/12
 */
public class WaeverAgentBootstrapClassLoader extends URLClassLoader {
    public WaeverAgentBootstrapClassLoader(URL[] urls) {
        super(urls);
    }
}
