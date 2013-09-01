package com.lewisd.maven.lint.plugin;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.maven.model.PatternSet;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lewisd.maven.lint.Rule;

public class RulesSelector {
    private final List<Rule> rules;

    public RulesSelector(List<Rule> rules) {
        this.rules = rules;
    }
    
    public Set<Rule> selectAllRules() {
        return Sets.newHashSet(rules);
    }


    public Set<Rule> selectRules(String... onlyRunRules) {
        Set<String> ruleNames = Sets.newHashSet(onlyRunRules);

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

    public Set<Rule> selectRules(PatternSet rulePatterns) {
        Set<Rule> selectedRules = Sets.newHashSet();
        Set<Rule> excludedRules = Sets.newHashSet();
        
        final List<Pattern> excludePatterns = convertGlobsToRegex(rulePatterns.getExcludes());
        final List<Pattern> includePatterns = convertGlobsToRegex(rulePatterns.getIncludes());
        
        for (Rule rule : rules) {
            if (!excludedRules.contains(rule)) {
                if (matches(excludePatterns, rule.getIdentifier())) {
                    excludedRules.add(rule);
                } else if (includePatterns.isEmpty() || matches(includePatterns, rule.getIdentifier())) {
                    selectedRules.add(rule);
                }
            }
        }
        
        return selectedRules;
    }
    
    private boolean matches(List<Pattern> patterns, String identifier) {
        for (Pattern pattern : patterns) {
            if (pattern.matcher(identifier).matches()) {
                return true;
            }
        }
        return false;
    }

    private List<Pattern> convertGlobsToRegex(List<String> globs) {
        List<Pattern> patterns = Lists.newArrayListWithCapacity(globs.size());
        
        for (String glob : globs) {
            patterns.add(Pattern.compile(convertGlobToRegex(glob)));
        }
        
        return patterns;
    }

    private String convertGlobToRegex(String glob) {
        StringBuilder sb = new StringBuilder();
        sb.append('^');
        for(int i = 0; i < glob.length(); ++i)
        {
            final char c = glob.charAt(i);
            switch(c)
            {
            case '*': sb.append(".*"); break;
            case '?': sb.append('.'); break;
            case '.': sb.append("\\."); break;
            case '\\': sb.append("\\\\"); break;
            default: sb.append(c);
            }
        }
        sb.append('$');
        return sb.toString();
    }

}
