package com.lewisd.maven.lint.rules.basic;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.rules.AbstractRule;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;

import java.util.List;
import java.util.Map;

public class VersioningOnlyInDependencyManagementRule extends AbstractRule {
    @Override
    public void invoke(final MavenProject mavenProject, final Map<String, Object> models, final ResultCollector resultCollector) {
        final List<Dependency> dependencies = mavenProject.getOriginalModel().getDependencies();
        validateDependencies(mavenProject, resultCollector, dependencies);

        for (final Profile profile : mavenProject.getOriginalModel().getProfiles()) {
            validateDependencies(mavenProject, resultCollector, profile.getDependencies());
        }
    }

    private void validateDependencies(final MavenProject mavenProject, final ResultCollector resultCollector, final List<Dependency> dependencies) {
        for (final Dependency dependency : dependencies) {
            if (StringUtils.isNotBlank(dependency.getVersion())) {
                resultCollector.addViolation(mavenProject, this, "Versioning should be in the dependency management.", dependency.getLocation("version"));
            }
        }
    }

    @Override
    public String getIdentifier() {
        return "VersioningOnlyInDependencyManagement";
    }

    @Override
    public String getDescription() {
        return "The versioning of dependencies should be done in the dependency management section " +
                "and only referred in the modules dependencies." +
                "This checks for versioning within the dependencies sections and the profiles dependencies section.";
    }
}
