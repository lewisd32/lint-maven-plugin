package com.lewisd.maven.lint.rules.order;

import com.lewisd.maven.lint.rules.AbstractRuleIT;
import com.lewisd.maven.lint.rules.POM;
import org.junit.Test;

public class DependenciesAreOrderedRuleIT extends AbstractRuleIT<DependenciesAreOrderedRule> {
    @Override
    public DependenciesAreOrderedRule getRule() {
        return getRule(DependenciesAreOrderedRule.class);
    }

    @Test
    @POM("src/test/resources/it/it-fail-when-dependencies-are-out-of-order/pom.xml")
    public void test() {
        invokeRule();

        violationAssert().line(19).violates(getRule().getClass()).withMessage("The dependency \'a:ab:jar:test\' is out of order. Expected \'a:aa:jar:\'.");
        violationAssert().line(28).violates(getRule().getClass()).withMessage("The dependency \'b:b:test-jar:\' is out of order. Expected \'a:ab:jar:test\'.");
        violationAssert().line(33).violates(getRule().getClass()).withMessage("The dependency \'a:aa:jar:\' is out of order. Expected \'b:b:jar:\'.");
        violationAssert().line(37).violates(getRule().getClass()).withMessage("The dependency \'b:b:jar:\' is out of order. Expected \'b:b:test-jar:\'.");

        violationAssert().line(45).violates(getRule().getClass()).withMessage("The dependency \'a:ab:jar:test\' is out of order. Expected \'a:aa:jar:\'.");
        violationAssert().line(54).violates(getRule().getClass()).withMessage("The dependency \'b:b:test-jar:\' is out of order. Expected \'a:ab:jar:test\'.");
        violationAssert().line(59).violates(getRule().getClass()).withMessage("The dependency \'a:aa:jar:\' is out of order. Expected \'b:b:jar:\'.");
        violationAssert().line(63).violates(getRule().getClass()).withMessage("The dependency \'b:b:jar:\' is out of order. Expected \'b:b:test-jar:\'.");

        violationAssert().line(74).violates(getRule().getClass()).withMessage("The dependency \'a:ab:jar:test\' is out of order. Expected \'a:aa:jar:\'.");
        violationAssert().line(83).violates(getRule().getClass()).withMessage("The dependency \'b:b:test-jar:\' is out of order. Expected \'a:ab:jar:test\'.");
        violationAssert().line(88).violates(getRule().getClass()).withMessage("The dependency \'a:aa:jar:\' is out of order. Expected \'b:b:jar:\'.");
        violationAssert().line(92).violates(getRule().getClass()).withMessage("The dependency \'b:b:jar:\' is out of order. Expected \'b:b:test-jar:\'.");

        violationAssert().line(100).violates(getRule().getClass()).withMessage("The dependency \'a:ab:jar:test\' is out of order. Expected \'a:aa:jar:\'.");
        violationAssert().line(109).violates(getRule().getClass()).withMessage("The dependency \'b:b:test-jar:\' is out of order. Expected \'a:ab:jar:test\'.");
        violationAssert().line(114).violates(getRule().getClass()).withMessage("The dependency \'a:aa:jar:\' is out of order. Expected \'b:b:jar:\'.");
        violationAssert().line(118).violates(getRule().getClass()).withMessage("The dependency \'b:b:jar:\' is out of order. Expected \'b:b:test-jar:\'.");
    }
}
