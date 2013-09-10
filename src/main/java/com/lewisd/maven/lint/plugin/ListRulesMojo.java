package com.lewisd.maven.lint.plugin;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import com.google.common.collect.Maps;
import com.lewisd.maven.lint.rules.AbstractRule;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.util.Collection;
import java.util.Map;

/**
 * Perform checks on the POM, and fail the build if violations are found.
 */
@Mojo(name = "list", threadSafe = true, requiresProject = true)
public class ListRulesMojo extends AbstractContextMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {

        init();

        Collection<AbstractRule> rules = getContext().getBeansOfType(AbstractRule.class).values();

        Map<String, AbstractRule> name2ruleMap = Maps.newTreeMap();
        for (AbstractRule rule : rules) {
            name2ruleMap.put(rule.getIdentifier(), rule);
        }

        StringBuilder buffer = new StringBuilder();
        for (Map.Entry<String, AbstractRule> entry : name2ruleMap.entrySet()) {
            AbstractRule rule = entry.getValue();
            buffer.
                    append("- ").
                    append(rule.getIdentifier()).
                    append("\n\n").
                    append(formatAsBlock(rule)).
                    append("\n\n");
        }

        System.out.println(buffer.toString().replaceFirst("\n$", ""));
    }

    private String formatAsBlock(AbstractRule rule) {
        final String description = rule.getDescription();
        String[] words = description.split("\\ +");
        StringBuilder lines = new StringBuilder("\t");

        int maxLength = 80;
        int count = 0;
        for (String word : words) {
            if ((count + word.length() + 1) <= maxLength) {
                if (count > 0) {
                    lines.append(' ');
                    count++;
                }
            } else {
                count = 0;
                lines.append("\n\t");
            }
            lines.append(word);
            count += word.length();
        }

        return lines.toString();
    }
}
