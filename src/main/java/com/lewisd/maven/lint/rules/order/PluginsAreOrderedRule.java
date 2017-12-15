package com.lewisd.maven.lint.rules.order;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.rules.AbstractRule;
import com.lewisd.maven.lint.util.ExpressionEvaluator;
import com.lewisd.maven.lint.util.ModelUtil;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class PluginsAreOrderedRule extends AbstractRule {

    private static final String PLUGIN_OUT_OF_ORDER = "The plugin '%s' is out of order. Expected '%s'.";

    @Autowired
    public PluginsAreOrderedRule(ExpressionEvaluator expressionEvaluator, ModelUtil modelUtil) {
        super(expressionEvaluator, modelUtil);
    }

    @Override
    public void invoke(final MavenProject mavenProject,
                       final Map<String, Object> models,
                       final ResultCollector resultCollector) {

        final Model originalModel = mavenProject.getOriginalModel();

        if (originalModel.getBuild() != null) {
            final PluginManagement dependencyManagement = originalModel.getBuild().getPluginManagement();

            if (dependencyManagement != null) {
                validatePlugins(mavenProject, resultCollector, dependencyManagement.getPlugins());
            }

            final List<Plugin> plugins = originalModel.getBuild().getPlugins();
            if (plugins != null) {
                validatePlugins(mavenProject, resultCollector, plugins);
            }

        }

        for (final Profile profile : originalModel.getProfiles()) {
            if (profile.getBuild() == null) {
                continue;
            }

            final PluginManagement pluginManagement = profile.getBuild().getPluginManagement();
            if (pluginManagement != null) {
                validatePlugins(mavenProject, resultCollector, pluginManagement.getPlugins());
            }

            final List<Plugin> plugins = profile.getBuild().getPlugins();
            if (plugins != null) {
                validatePlugins(mavenProject, resultCollector, plugins);
            }
        }
    }

    private void validatePlugins(final MavenProject mavenProject,
                                 final ResultCollector resultCollector,
                                 final List<Plugin> plugins) {

        final Comparator<Plugin> comparator = new Comparator<Plugin>() {
            @Override
            public int compare(final Plugin o1, final Plugin o2) {
                return ComparisonChain.start()
                        .compare(o1.getGroupId(), o2.getGroupId())
                        .compare(o1.getArtifactId(), o2.getArtifactId())
                        .result();
            }
        };

        final Ordering<Plugin> byRule = Ordering.from(comparator);

        if (!plugins.isEmpty() && !byRule.isOrdered(plugins)) {
            final List<Plugin> sortedPlugins = byRule.sortedCopy(plugins);

            for (int i = 0; i < plugins.size(); i++) {
                final Plugin plugin = plugins.get(i);
                final Plugin sortedPlugin = sortedPlugins.get(i);

                if (!plugin.equals(sortedPlugin)) {
                    final String message = format(PLUGIN_OUT_OF_ORDER, descript(plugin), descript(sortedPlugin));
                    resultCollector.addViolation(mavenProject, this, message, plugin.getLocation("artifactId"));
                }
            }
        }
    }

    private String descript(final Plugin plugin) {
        return String.format("%s:%s", plugin.getGroupId(), plugin.getArtifactId());
    }

    @Override
    public String getIdentifier() {
        return "PluginsAreOrdered";
    }

    @Override
    public String getDescription() {
        return "Plugins should be ordered alphabetically." +
                "This checks for ordering within the modules build plugins and plugin management," +
                "as well for their respective sections inside the profiles build section.";
    }
}
