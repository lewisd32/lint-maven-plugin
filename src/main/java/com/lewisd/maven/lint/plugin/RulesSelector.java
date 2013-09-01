package com.lewisd.maven.lint.plugin;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lewisd.maven.lint.Rule;
import org.apache.maven.model.PatternSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static com.lewisd.maven.lint.plugin.PatternSetUtil.convertGlobsToRegex;

public class RulesSelector {
    private final Set<Rule> availableRules;

    public RulesSelector(Set<Rule> availableRules) {
        this.availableRules = availableRules;
    }

    public Set<Rule> selectRules(String... onlyRunRules) {
        Set<String> ruleNames = Sets.newHashSet(onlyRunRules);
        Set<Rule> selectedRules = Sets.newHashSet();

        if (ruleNames.contains("all")) {
            selectedRules.addAll(Sets.newHashSet(availableRules));
        } else {
            Set<String> rulesNotMatched = Sets.newHashSet();
            Set<String> rulesMatched = Sets.newHashSet();
            for (String ruleName : ruleNames) {
                for (Rule rule : availableRules) {
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

    public Set<Rule> selectRules(PatternSet rulePatterns) {

        List<Pattern> excludePatterns = convertGlobsToRegex(rulePatterns.getExcludes());
        List<Pattern> includePatterns = convertGlobsToRegex(rulePatterns.getIncludes());

        Set<Rule> excludedRules = createRulesFromPattern(excludePatterns);
        Set<Rule> includedRules = createRulesFromPattern(includePatterns);

        includedRules.removeAll(excludedRules);

        return includedRules;
    }

    public Set<Rule> selectRules(PatternSet rulePatterns, String... onlyRunRules) {

        PatternSet rulePatternsCopy = new PatternSet();

        if (rulePatterns == null) {
            rulePatternsCopy.setExcludes(new ArrayList<String>());
            rulePatternsCopy.setIncludes(Lists.newArrayList("*"));
        } else {
            if (rulePatterns.getIncludes().isEmpty()) {
                rulePatternsCopy.setIncludes(Lists.newArrayList("*"));
            } else {
                rulePatternsCopy.setIncludes(rulePatterns.getIncludes());
            }

            if (rulePatterns.getExcludes().isEmpty()) {
                rulePatternsCopy.setExcludes(new ArrayList<String>());
            } else {
                rulePatternsCopy.setExcludes(rulePatterns.getExcludes());
            }
        }

        Set<Rule> rulesByList = selectRules(onlyRunRules);

        return new RulesSelector(rulesByList).selectRules(rulePatternsCopy);
    }

    private Set<Rule> createRulesFromPattern(List<Pattern> patternList) {
        Set<Rule> ruleSet = Sets.newHashSet();
        for (Rule rule : availableRules) {
            if (matches(patternList, rule.getIdentifier())) {
                ruleSet.add(rule);
            }
        }
        return ruleSet;
    }

    private boolean matches(List<Pattern> patterns, String identifier) {
        for (Pattern pattern : patterns) {
            if (pattern.matcher(identifier).matches()) {
                return true;
            }
        }
        return false;
    }

}
