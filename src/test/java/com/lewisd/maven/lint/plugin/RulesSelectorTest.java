package com.lewisd.maven.lint.plugin;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.Rule;
import com.lewisd.maven.lint.rules.AbstractRule;
import org.apache.maven.project.MavenProject;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;

public class RulesSelectorTest {

    abstract class ARule extends AbstractRule {
        private final String identifier;

        public ARule(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public void invoke(MavenProject mavenProject, Map<String, Object> models, ResultCollector resultCollector) {
            // ok
        }

        @Override
        public String getIdentifier() {
            return identifier;
        }

        @Override
        public String getDescription() {
            return "";
        }
    }

    class A1Rule extends ARule {
        public A1Rule() {
            super("A1");
        }
    }

    class A2Rule extends ARule {
        public A2Rule() {
            super("A2");
        }
    }

    private Rule[] allRules = new Rule[]{new A1Rule(), new A2Rule()};
    private final RulesSelector selector = new RulesSelector(Arrays.asList(allRules));

    @Test
    public void testAll() {
        assertThat(selector.selectRules("all")).contains(allRules);
    }

    @Test
    public void testAllAll() {
        assertThat(selector.selectRules(new String[]{"all","all"})).contains(allRules);
    }

    @Test
    public void testUnsupportedRule() {
        try {
            selector.selectRules(new String[]{"x","A2"});
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("unsupported rule(s) x");
        }
    }

    @Test
    public void testUnsupportedRuleWithAll() {
        assertThat(selector.selectRules(new String[]{"x1","all"})).contains(allRules);
    }

    @Test
    public void testExplicitAll() {
        assertThat(selector.selectRules(new String[]{"A1","A2"})).contains(allRules);
    }

    @Test
    public void testExplicitA1() {
        assertThat(selector.selectRules(new String[]{"A1"})).contains(allRules[0]);
    }
}
