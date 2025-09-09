package com.github.twogoods.adhesive.agent.core.instrumentation;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

public class MethodAdvice {
    private ElementMatcher<MethodDescription> methodMatcher;
    private String adviceName;

    public ElementMatcher<MethodDescription> getMethodMatcher() {
        return methodMatcher;
    }

    public String getAdviceName() {
        return adviceName;
    }
}
