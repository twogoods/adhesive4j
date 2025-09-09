package com.github.twogoods.adhesive.agent.core.instrumentation;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

public interface TypeInstrumentation {
    ElementMatcher<TypeDescription> typeMatcher();

    MethodAdvice[] methodAdvices();
}
