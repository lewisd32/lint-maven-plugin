package com.lewisd.maven.lint.rules.basic;

import com.lewisd.maven.lint.rules.AbstractRuleIT;
import com.lewisd.maven.lint.rules.POM;
import org.junit.Test;

public class VersioningOnlyInPluginManagementRuleIT extends AbstractRuleIT<VersioningOnlyInPluginManagementRule> {
    @Override
    public VersioningOnlyInPluginManagementRule getRule() {
        return getRule(VersioningOnlyInPluginManagementRule.class);
    }

    @Test
    @POM("src/test/resources/it/it-fail-when-plugins-versions-are-not-in-plugin-management/pom.xml")
    public void test() {
        invokeRule();

        violationAssert().line(20).violates(getRule().getClass()).withMessage("Versioning should be in the plugin management.");
        violationAssert().line(29).violates(getRule().getClass()).withMessage("Versioning should be in the plugin management.");
        violationAssert().line(42).violates(getRule().getClass()).withMessage("Versioning should be in the plugin management.");
        violationAssert().line(51).violates(getRule().getClass()).withMessage("Versioning should be in the plugin management.");
    }
}
