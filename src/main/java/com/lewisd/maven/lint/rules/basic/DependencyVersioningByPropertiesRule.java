package com.lewisd.maven.lint.rules.basic;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.rules.AbstractRule;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;

import java.util.List;
import java.util.Map;

public class DependencyVersioningByPropertiesRule extends AbstractRule {
    @Override
    public void invoke(final MavenProject mavenProject, final Map<String, Object> models, final ResultCollector resultCollector) {
        final Model originalModel = mavenProject.getOriginalModel();
        final DependencyManagement dependencyManagement = originalModel.getDependencyManagement();

        if (dependencyManagement != null) {
            validateDependencies(mavenProject, resultCollector, dependencyManagement.getDependencies());
        }

        validateDependencies(mavenProject, resultCollector, originalModel.getDependencies());

        final List<Profile> profiles = originalModel.getProfiles();
        for (final Profile profile : profiles) {
            final DependencyManagement profileDependencyManagement = profile.getDependencyManagement();
            if (profileDependencyManagement != null) {
                validateDependencies(mavenProject, resultCollector, profileDependencyManagement.getDependencies());
            }

            final List<Dependency> profileDependencies = profile.getDependencies();
            if (profileDependencies != null) {
                validateDependencies(mavenProject, resultCollector, profileDependencies);
            }
        }
    }

    private void validateDependencies(final MavenProject mavenProject, final ResultCollector resultCollector, final List<Dependency> dependencies) {
        for (final Dependency dependency : dependencies) {
            final String version = dependency.getVersion();
            if (version != null && !version.startsWith("${")) {
                resultCollector.addViolation(mavenProject, this, String.format("The dependency '%s' is not using a version property.", dependency.getArtifactId()), dependency
                        .getLocation("version"));
            }
        }
    }

    @Override
    public String getIdentifier() {
        return "DependencyVersioningByProperties";
    }

    @Override
    public String getDescription() {
        return "Dependencies should be versioned with properties from the Properties section of the POM." +
                "This checks for versioning within the dependencies and dependency management section " +
                "as well for their respective sections inside the profiles section" +
                "This benefits the project maintainability and legibility.";
    }
}
