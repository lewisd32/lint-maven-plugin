package com.lewisd.maven.lint.rules.basic;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.model.ExtDependency;
import com.lewisd.maven.lint.model.ObjectWithPath;
import com.lewisd.maven.lint.rules.AbstractReduntantVersionRule;
import com.lewisd.maven.lint.util.ExpressionEvaluator;
import com.lewisd.maven.lint.util.ModelUtil;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.PluginParameterExpressionEvaluator;
import org.apache.maven.project.MavenProject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Map;

public class RedundantDependencyVersionsRule extends AbstractReduntantVersionRule {

    @Autowired
    public RedundantDependencyVersionsRule(ExpressionEvaluator expressionEvaluator,
                                           ModelUtil modelUtil,
                                           PluginParameterExpressionEvaluator pluginParameterExpressionEvaluator) {
        super(expressionEvaluator, modelUtil, pluginParameterExpressionEvaluator);
    }

    @Override
    public String getIdentifier() {
        return "RedundantDepVersion";
    }

    @Override
    public String getDescription() {
        return "Dependency versions should be set in one place, and not overridden without changing the version. " +
               "If, for example, <dependencyManagement> sets a version, and <dependencies> somewhere overrides it, " +
               "but with the same version, this can make version upgrades more difficult, due to the repetition.";
    }

    @Override
    public void invoke(final MavenProject mavenProject, final Map<String, Object> models, final ResultCollector resultCollector) {
        final Model originalModel = mavenProject.getOriginalModel();
        final Collection<Dependency> dependencies = expressionEvaluator.getPath(originalModel, "dependencies");
        final Collection<Dependency> managedDependencies = expressionEvaluator.getPath(originalModel,
                                                                                       "dependencyManagement/dependencies");

        final Map<String, Dependency> managedDependenciesByManagementKey = modelUtil.mapByManagementKey(
                managedDependencies);

        for (final Dependency dependency : dependencies) {
            final Dependency managedDependency = managedDependenciesByManagementKey.get(dependency.getManagementKey());
            if (managedDependency != null) {
                checkForRedundantVersions(mavenProject, resultCollector,
                                          new ObjectWithPath<Object>(dependency, mavenProject, "dependencies"),
                                          new ObjectWithPath<Object>(managedDependency, mavenProject, "dependencyManagement/dependencies"),
                                          "Dependency", "in dependencyManagement");
            }

            final ObjectWithPath<ExtDependency> inheritedDependency = modelUtil.findInheritedDependency(mavenProject,
                                                                                                        dependency);
            if (inheritedDependency != null) {
                checkForRedundantVersions(mavenProject, resultCollector,
                                          new ObjectWithPath<Object>(dependency, mavenProject, "dependencies"),
                                          inheritedDependency,
                                          "Dependency", "is inherited from " + inheritedDependency.getProject().getId());
            }
        }

        for (final Dependency managedDependency : managedDependencies) {
            final ObjectWithPath<ExtDependency> inheritedDependency = modelUtil.findInheritedDependency(mavenProject,
                                                                                                        managedDependency);
            if (inheritedDependency != null) {
                checkForRedundantVersions(mavenProject, resultCollector,
                                          new ObjectWithPath<Object>(managedDependency, mavenProject, "dependencyManagement/dependencies"),
                                          inheritedDependency,
                                          "Managed dependency", "is inherited from " + inheritedDependency.getProject().getId());
            }
        }

    }

}
