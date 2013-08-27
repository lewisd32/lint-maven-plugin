package com.lewisd.maven.lint.plugin;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.lewisd.maven.lint.Rule;

import java.util.List;
import java.util.Set;

public class RulesSelector {
    private final List<Rule> rules;

    public RulesSelector(List<Rule> rules) {
        this.rules = rules;
    }

    public Set<Rule> selectRule(String ... ruleCommaList) {
        Set<String> ruleNames = Sets.newHashSet(ruleCommaList);

        Set<Rule> selectedRules = Sets.newHashSet();

        if (ruleNames.contains("all")) {
            selectedRules.addAll(Sets.newHashSet(rules));
        } else {

            Set<String> rulesNotMatched = Sets.newHashSet();
            Set<String> rulesMatched = Sets.newHashSet();
            for (String ruleName : ruleNames) {
                for (Rule rule : rules) {
                    if (rule.getIdentifier().equals(ruleName)) {
                        selectedRules.add(rule);
                        rulesMatched.add(ruleName);
                        rulesNotMatched.remove(ruleName);
                    } else {
                        if (!rulesMatched.contains(ruleName)) {
                            rulesNotMatched.add(ruleName);
                        }
                    }
                }
            }

            if (!rulesNotMatched.isEmpty()) {
                throw new IllegalArgumentException("unsupported rule(s) " + Joiner.on(",").join(rulesNotMatched));
            }
        }

        return selectedRules;
    }
}
