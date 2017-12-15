package com.lewisd.maven.lint.rules.order;

import com.lewisd.maven.lint.rules.AbstractRuleIT;
import com.lewisd.maven.lint.rules.POM;
import org.junit.Test;

public class PluginsAreOrderedRuleIT extends AbstractRuleIT<PluginsAreOrderedRule> {
    @Override
    public PluginsAreOrderedRule getRule() {
        return getRule(PluginsAreOrderedRule.class);
    }

    @Test
    @POM("src/test/resources/it/it-fail-when-plugins-are-out-of-order/pom.xml")
    public void test() {
        invokeRule();

        violationAssert().line(20).violates(getRule().getClass()).withMessage("The plugin \'b:b\' is out of order. Expected \'a:aa\'.");
        violationAssert().line(28).violates(getRule().getClass()).withMessage("The plugin \'a:aa\' is out of order. Expected \'b:b\'.");

        violationAssert().line(36).violates(getRule().getClass()).withMessage("The plugin \'b:b\' is out of order. Expected \'a:aa\'.");
        violationAssert().line(44).violates(getRule().getClass()).withMessage("The plugin \'a:aa\' is out of order. Expected \'b:b\'.");
    }
}
