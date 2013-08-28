package com.lewisd.maven.lint.rules;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.Violation;
import org.fest.assertions.core.Condition;

public class AssertHelper {
    public static Condition<? super ResultCollector> haveViolationAtLine(final int line) {
        return new Condition<ResultCollector>() {
            @Override
            public Condition<ResultCollector> as(String newDescription) {
                return super.as("to have a violation at line " + line);    //To change body of overridden methods use File | Settings | File Templates.
            }

            @Override
            public boolean matches(ResultCollector collector) {
                for (Violation violation : collector.getViolations()) {
                    if (violation.getInputLocation().getLineNumber() == line) {
                        return true;
                    }
                }
                return false;
            }
        };
    }
}
