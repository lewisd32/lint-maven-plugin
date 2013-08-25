package com.lewisd.maven.lint.rules.basic;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.model.ExtPlugin;
import com.lewisd.maven.lint.model.ObjectWithPath;
import com.lewisd.maven.lint.rules.AbstractReduntantVersionRule;
import com.lewisd.maven.lint.util.ExpressionEvaluator;
import com.lewisd.maven.lint.util.ModelUtil;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RedundantPluginVersionsRule extends AbstractReduntantVersionRule {

    @Autowired
    public RedundantPluginVersionsRule(final ExpressionEvaluator expressionEvaluator, final ModelUtil modelUtil) {
        super(expressionEvaluator, modelUtil);
    }

    @Override
    protected void addRequiredModels(final Set<String> requiredModels) {
    }

    @Override
    public String getIdentifier() {
        return "RedundantPluginVersion";
    }

    @Override
    public String getDescription() {
        return "Plugin versions should be set in one place, and not overridden without changing the version. " +
               "If, for example, <pluginManagement> sets a version, and <plugins> somewhere overrides it, " +
               "but with the same version, this can make version upgrades more difficult, due to the repetition.";
    }

    @Override
    public void invoke(final MavenProject mavenProject, final Map<String, Object> models, final ResultCollector resultCollector) {
        final Model originalModel = mavenProject.getOriginalModel();
        final Collection<Plugin> plugins = getExpressionEvaluator().getPath(originalModel, "build/plugins");
        final Collection<Plugin> managedPlugins = getExpressionEvaluator().getPath(originalModel, "build/pluginManagement/plugins");

        final Map<String, Plugin> managedPluginsByManagementKey = getModelUtil().mapById(managedPlugins);

        for (final Plugin plugin : plugins) {
            final Plugin managedDependency = managedPluginsByManagementKey.get(plugin.getId());
            if (managedDependency != null) {
                checkForRedundantVersions(mavenProject, resultCollector,
                                          new ObjectWithPath<Object>(plugin, mavenProject, "build/plugins"),
                                          new ObjectWithPath<Object>(managedDependency, mavenProject, "build/pluginManagement/plugins"),
                                          "Plugin", "in pluginManagement");
            }

            final List<ExtPlugin> inheritedPlugins = getModelUtil().findInheritedPlugins(mavenProject, plugin);
            for (final ExtPlugin inheritedPlugin : inheritedPlugins) {
                // only check the first inherited one that has a version set.
                if (inheritedPlugin.getVersion() != null) {
                    checkForRedundantVersions(mavenProject, resultCollector,
                                              new ObjectWithPath<Object>(plugin, mavenProject, "build/plugins"),
                                              new ObjectWithPath<Object>(inheritedPlugin, mavenProject, null),
                                              "Plugin", "is inherited from " + inheritedPlugin.getMavenProject().getId());
                    break;
                }
            }
        }

        for (final Plugin plugin : managedPlugins) {
            final List<ExtPlugin> inheritedPlugins = getModelUtil().findInheritedPlugins(mavenProject, plugin);
            for (final ExtPlugin inheritedPlugin : inheritedPlugins) {
                // only check the first inherited one that has a version set.
                if (inheritedPlugin.getVersion() != null) {
                    checkForRedundantVersions(mavenProject, resultCollector,
                                              new ObjectWithPath<Object>(plugin, mavenProject, "build/plugins"),
                                              new ObjectWithPath<Object>(inheritedPlugin, mavenProject, null),
                                              "Managed plugin", "is inherited from " + inheritedPlugin.getMavenProject().getId());
                    break;
                }
            }
        }

    }

}
