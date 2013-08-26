package com.lewisd.maven.lint.rules.basic;

import com.lewisd.maven.lint.rules.AbstractRuleIT;
import com.lewisd.maven.lint.rules.POM;
import org.junit.Test;

public class DuplicateDependenciesRuleIT extends AbstractRuleIT<DuplicateDependenciesRule> {

    @Override
    public DuplicateDependenciesRule getRule() {
        return getRule(DuplicateDependenciesRule.class);
    }
    @Test
    @POM("src/test/resources/it/it-fail-when-duplicate-dependency/pom.xml")
    public void test() throws Exception {

        invokeRule();

        violationAssert().line(50).column(17).violates(getRule().getClass()).withMessage("Dependency 'localhost:dummy-d:jar' is declared multiple times with the same version: 55:17");
        violationAssert().line(60).column(17).violates(getRule().getClass()).withMessage("Dependency 'localhost:dummy-e:jar' is declared multiple times with the same version: 64:17");
        violationAssert().line(68).column(17).violates(getRule().getClass()).withMessage("Dependency 'localhost:dummy-f:jar' is declared multiple times with different versions (1.0, 2.0)");
        violationAssert().line(18).column(19).violates(getRule().getClass()).withMessage("Managed dependency 'localhost:dummy-a:jar' is declared multiple times with the same version: 23:19");
        violationAssert().line(28).column(19).violates(getRule().getClass()).withMessage("Managed dependency 'localhost:dummy-b:jar' is declared multiple times with the same version: 32:19");
        violationAssert().line(36).column(19).violates(getRule().getClass()).withMessage("Managed dependency 'localhost:dummy-c:jar' is declared multiple times with different versions (1.0, 2.0)");
    }
}
