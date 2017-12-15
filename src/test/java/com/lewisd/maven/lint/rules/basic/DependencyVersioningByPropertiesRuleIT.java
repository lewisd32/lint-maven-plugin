package com.lewisd.maven.lint.rules.basic;

import com.lewisd.maven.lint.rules.AbstractRuleIT;
import com.lewisd.maven.lint.rules.POM;
import org.junit.Test;

public class DependencyVersioningByPropertiesRuleIT extends AbstractRuleIT<DependencyVersioningByPropertiesRule> {
    @Override
    public DependencyVersioningByPropertiesRule getRule() {
        return getRule(DependencyVersioningByPropertiesRule.class);
    }

    @Test
    @POM("src/test/resources/it/it-fail-when-dependencies-versions-are-not-in-properties/pom.xml")
    public void test() {
        invokeRule();

        violationAssert().line(20).violates(getRule().getClass()).withMessage("The dependency 'dummy-a' is not using a version property.");
        violationAssert().line(30).violates(getRule().getClass()).withMessage("The dependency 'dummy-c' is not using a version property.");
        violationAssert().line(39).violates(getRule().getClass()).withMessage("The dependency 'dummy-d' is not using a version property.");
        violationAssert().line(49).violates(getRule().getClass()).withMessage("The dependency 'dummy-f' is not using a version property.");
        violationAssert().line(61).violates(getRule().getClass()).withMessage("The dependency 'dummy-g' is not using a version property.");
        violationAssert().line(71).violates(getRule().getClass()).withMessage("The dependency 'dummy-i' is not using a version property.");
        violationAssert().line(79).violates(getRule().getClass()).withMessage("The dependency 'dummy-j' is not using a version property.");
        violationAssert().line(89).violates(getRule().getClass()).withMessage("The dependency 'dummy-l' is not using a version property.");
    }
}
