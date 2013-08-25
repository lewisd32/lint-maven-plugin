package com.lewisd.maven.lint.rules.basic;

import com.lewisd.maven.lint.rules.AbstractRuleIT;
import com.lewisd.maven.lint.rules.POM;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.Test;

import java.io.IOException;

public class ExecutionIdRuleIT extends AbstractRuleIT<ExecutionIdRule> {

    @Override
    public ExecutionIdRule getRule() {
        return getRule(ExecutionIdRule.class);
    }

    @Test
    @POM("src/it/it-fail-when-execution-without-id/pom.xml")
    public void test() throws IOException, XmlPullParserException {

        invokeRule();

        violationAssert().line(19).violates(ExecutionIdRule.class).withMessage("Executions must specify an id");
        violationAssert().line(32).violates(ExecutionIdRule.class);
        violationAssert().line(51).violates(ExecutionIdRule.class);
        violationAssert().line(64).violates(ExecutionIdRule.class);
    }
}
