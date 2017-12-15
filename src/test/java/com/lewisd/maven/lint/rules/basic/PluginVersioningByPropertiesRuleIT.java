package com.lewisd.maven.lint.rules.basic;

import com.lewisd.maven.lint.rules.AbstractRuleIT;
import com.lewisd.maven.lint.rules.POM;
import org.junit.Test;

public class PluginVersioningByPropertiesRuleIT extends AbstractRuleIT<PluginVersioningByPropertiesRule> {
    @Override
    public PluginVersioningByPropertiesRule getRule() {
        return getRule(PluginVersioningByPropertiesRule.class);
    }

    @Test
    @POM("src/test/resources/it/it-fail-when-plugins-versions-are-not-in-properties/pom.xml")
    public void test() {
        invokeRule();

        violationAssert().line(21).violates(getRule().getClass()).withMessage("The plugin 'dummy-a' is not using a version property.");
        violationAssert().line(31).violates(getRule().getClass()).withMessage("The plugin 'dummy-c' is not using a version property.");
        violationAssert().line(39).violates(getRule().getClass()).withMessage("The plugin 'dummy-d' is not using a version property.");
        violationAssert().line(49).violates(getRule().getClass()).withMessage("The plugin 'dummy-f' is not using a version property.");
    }
}
