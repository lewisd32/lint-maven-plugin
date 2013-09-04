package com.lewisd.maven.lint.plugin;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.Rule;
import com.lewisd.maven.lint.rules.AbstractRule;
import org.apache.maven.model.PatternSet;
import org.apache.maven.project.MavenProject;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

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

    class A3Rule extends ARule {
        public A3Rule() {
            super("A3");
        }
    }

    class BRule extends ARule {
        public BRule() {
            super("B");
        }
    }

    private Rule[] allRules = new Rule[]{new A1Rule(), new A2Rule(), new A3Rule(), new BRule()};
    private final RulesSelector selector = new RulesSelector(Sets.newHashSet(allRules));

    @Test
    public void testAll() {
        assertThat(selector.selectRules("all")).contains(allRules);
    }

    @Test
    public void testAllAll() {
        assertThat(selector.selectRules(new String[]{"all", "all"})).contains(allRules);
    }

    @Test
    public void testUnsupportedRule() {
        try {
            selector.selectRules(new String[]{"x", "A2"});
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("unsupported rule(s) x");
        }
    }

    @Test
    public void testUnsupportedRuleWithAll() {
        assertThat(selector.selectRules(new String[]{"x1", "all"})).contains(allRules);
    }

    @Test
    public void testExplicitAll() {
        assertThat(selector.selectRules(new String[]{"A1", "A2", "A3", "B"})).contains(allRules);
    }

    @Test
    public void testExplicitA1() {
        assertThat(selector.selectRules(new String[]{"A1"})).contains(allRules[0]);
    }

    @Test
    public void testRulesSelectionWithExcludePattern() {
        PatternSet patternSet = new PatternSet();
        patternSet.setExcludes(Lists.newArrayList("A*"));
        patternSet.setIncludes(Lists.newArrayList("*"));

        assertThat(selector.selectRules(patternSet)).containsExactly(new BRule());
    }

    @Test
    public void testRulesSelectionWithExcludePattern2() {
        PatternSet patternSet = new PatternSet();
        patternSet.setExcludes(Lists.newArrayList("A2*"));
        patternSet.setIncludes(Lists.newArrayList("*"));

        Set<Rule> actual = selector.selectRules(patternSet);
        assertThat(actual).contains(new A1Rule(), new A3Rule(), new BRule());
        assertThat(actual).hasSize(3);
    }

    @Test
    public void testRulesSelectionWithExcludeAllPattern() {
        PatternSet patternSet = new PatternSet();
        patternSet.setExcludes(Lists.newArrayList("*"));
        patternSet.setIncludes(Lists.newArrayList("*"));

        assertThat(selector.selectRules(patternSet)).isEmpty();
    }

    @Test
    public void testRulesSelectionCombinedWithPatternExcludeAll() {
        PatternSet patternSet = new PatternSet();
        patternSet.setExcludes(Lists.newArrayList("*"));

        assertThat(selector.selectRules(patternSet, "all")).isEmpty();
    }

    @Test
    public void testRulesSelectionCombinedWithPatternExcludeAs() {
        PatternSet patternSet = new PatternSet();
        patternSet.setExcludes(Lists.newArrayList("A*"));

        assertThat(selector.selectRules(patternSet, "all")).containsExactly(new BRule());
    }
}
