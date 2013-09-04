package com.lewisd.maven.lint.plugin;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.regex.Pattern;

public class PatternSetUtil {
    public static String convertGlobToRegex(String glob) {
        StringBuilder sb = new StringBuilder('^');
        for (int i = 0; i < glob.length(); ++i) {
            final char c = glob.charAt(i);
            switch (c) {
                case '*':
                    sb.append(".*");
                    break;
                case '?':
                    sb.append('.');
                    break;
                case '.':
                    sb.append("\\.");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                default:
                    sb.append(c);
            }
        }
        sb.append('$');
        return sb.toString();
    }

    public static List<Pattern> convertGlobsToRegex(List<String> globs) {
        List<Pattern> patterns = Lists.newArrayListWithCapacity(globs.size());

        for (String glob : globs) {
            patterns.add(Pattern.compile(convertGlobToRegex(glob)));
        }

        return patterns;
    }
}
