package com.lewisd.maven.lint.rules.basic;

import com.lewisd.maven.lint.rules.AbstractRuleIT;
import com.lewisd.maven.lint.rules.POM;
import org.junit.Test;

public class GroupArtifactVersionMustBeInCorrectOrderRuleIT extends AbstractRuleIT<GroupArtifactVersionMustBeInCorrectOrderRule> {

    @Override
    public GroupArtifactVersionMustBeInCorrectOrderRule getRule() {
        return getRule(GroupArtifactVersionMustBeInCorrectOrderRule.class);
    }

    @Test
    @POM("src/test/resources/it/it-fail-when-gavtc-fields-in-wrong-order/pom.xml")
    public void test() throws Exception {

        invokeRule();

        violationAssert().line(5).violates(getRule().getClass()).withMessage("Found 'artifactId' but was expecting 'groupId'");
        violationAssert().line(17).violates(getRule().getClass()).withMessage("Found 'configuration' but was expecting 'version'");
        violationAssert().line(22).violates(getRule().getClass()).withMessage("Found 'artifactId' but was expecting 'groupId'");
        violationAssert().line(34).violates(getRule().getClass()).withMessage("Found 'execution' but was expecting 'artifactId'");
        violationAssert().line(39).violates(getRule().getClass()).withMessage("Found 'artifactId' but was expecting 'groupId'");
        violationAssert().line(52).violates(getRule().getClass()).withMessage("Found 'version' but was expecting 'artifactId'");
        violationAssert().line(58).violates(getRule().getClass()).withMessage("Found 'classifier' but was expecting 'version'");
        violationAssert().line(75).violates(getRule().getClass()).withMessage("Found 'exclusion' but was expecting 'version'");
        violationAssert().line(66).violates(getRule().getClass()).withMessage("Found 'type' but was expecting 'groupId'");
    }
}
