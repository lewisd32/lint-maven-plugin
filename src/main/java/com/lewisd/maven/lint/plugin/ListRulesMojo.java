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


import com.lewisd.maven.lint.rules.AbstractRule;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.util.Map;

/**
 * Perform checks on the POM, and fail the build if violations are found.
 *
 * @goal list
 * @threadSafe
 */
public class ListRulesMojo extends AbstractContextMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {

        init();

        StringBuilder buffer = new StringBuilder();

        final Map<String, AbstractRule> beansOfType = getContext().getBeansOfType(AbstractRule.class);

        for (AbstractRule rule : beansOfType.values()) {
            buffer.
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
        StringBuilder lines = new StringBuilder();

        int maxLength = 80;
        int count = 0;
        for (String word : words) {
            if ((count + word.length() + 1) <= maxLength) {
                if (count > 0) {
                    lines.append(' ');
                }
                lines.append(word);
                count += +word.length() + 1;
            } else {
                count = 0;
                lines.append("\n");
            }
        }

        return lines.toString().trim();
    }
}
