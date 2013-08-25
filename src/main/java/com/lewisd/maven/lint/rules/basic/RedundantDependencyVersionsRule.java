package com.lewisd.maven.lint.rules.basic;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.model.ExtDependency;
import com.lewisd.maven.lint.model.ObjectWithPath;
import com.lewisd.maven.lint.rules.AbstractReduntantVersionRule;
import com.lewisd.maven.lint.util.ExpressionEvaluator;
import com.lewisd.maven.lint.util.ModelUtil;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class RedundantDependencyVersionsRule extends AbstractReduntantVersionRule {

    @Autowired
    public RedundantDependencyVersionsRule(final ExpressionEvaluator expressionEvaluator, final ModelUtil modelUtil) {
        super(expressionEvaluator, modelUtil);
    }

    @Override
    protected void addRequiredModels(final Set<String> requiredModels) {
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
        final Collection<Dependency> dependencies = getExpressionEvaluator().getPath(originalModel, "dependencies");
        final Collection<Dependency> managedDependencies = getExpressionEvaluator().getPath(originalModel, "dependencyManagement/dependencies");

        final Map<String, Dependency> managedDependenciesByManagementKey = getModelUtil().mapByManagementKey(managedDependencies);

        for (final Dependency dependency : dependencies) {
            final Dependency managedDependency = managedDependenciesByManagementKey.get(dependency.getManagementKey());
            if (managedDependency != null) {
                checkForRedundantVersions(mavenProject, resultCollector,
                                          new ObjectWithPath<Object>(dependency, mavenProject, "dependencies"),
                                          new ObjectWithPath<Object>(managedDependency, mavenProject, "dependencyManagement/dependencies"),
                                          "Dependency", "in dependencyManagement");
            }

            final ExtDependency inheritedDependency = getModelUtil().findInheritedDependency(mavenProject, dependency);
            if (inheritedDependency != null) {
                checkForRedundantVersions(mavenProject, resultCollector,
                                          new ObjectWithPath<Object>(dependency, mavenProject, "dependencies"),
                                          new ObjectWithPath<Object>(inheritedDependency, inheritedDependency.getMavenProject(), null),
                                          "Dependency", "is inherited from " + inheritedDependency.getMavenProject().getId());
            }
        }

        for (final Dependency managedDependency : managedDependencies) {
            final ExtDependency inheritedDependency = getModelUtil().findInheritedDependency(mavenProject, managedDependency);
            if (inheritedDependency != null) {
                checkForRedundantVersions(mavenProject, resultCollector,
                                          new ObjectWithPath<Object>(managedDependency, mavenProject, "dependencyManagement/dependencies"),
                                          new ObjectWithPath<Object>(inheritedDependency, inheritedDependency.getMavenProject(), null),
                                          "Managed dependency", "is inherited from " + inheritedDependency.getMavenProject().getId());
            }
        }

    }

}
