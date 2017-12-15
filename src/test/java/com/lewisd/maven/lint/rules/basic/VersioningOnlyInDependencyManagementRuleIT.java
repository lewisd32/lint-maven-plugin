package com.lewisd.maven.lint.rules.basic;

import com.lewisd.maven.lint.rules.AbstractRuleIT;
import com.lewisd.maven.lint.rules.POM;
import org.junit.Test;

public class VersioningOnlyInDependencyManagementRuleIT extends AbstractRuleIT<VersioningOnlyInDependencyManagementRule> {
    @Override
    public VersioningOnlyInDependencyManagementRule getRule() {
        return getRule(VersioningOnlyInDependencyManagementRule.class);
    }

    @Test
    @POM("src/test/resources/it/it-fail-when-dependencies-versions-are-not-in-dependency-management/pom.xml")
    public void test() {
        invokeRule();

        violationAssert().line(19).violates(getRule().getClass()).withMessage("Versioning should be in the dependency management.");
        violationAssert().line(28).violates(getRule().getClass()).withMessage("Versioning should be in the dependency management.");
        violationAssert().line(58).violates(getRule().getClass()).withMessage("Versioning should be in the dependency management.");
        violationAssert().line(67).violates(getRule().getClass()).withMessage("Versioning should be in the dependency management.");
    }
}
