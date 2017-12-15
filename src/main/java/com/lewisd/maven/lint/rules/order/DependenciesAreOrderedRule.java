package com.lewisd.maven.lint.rules.order;

import com.google.common.base.Optional;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.rules.AbstractRule;
import com.lewisd.maven.lint.rules.order.converter.DependencyConverter;
import com.lewisd.maven.lint.rules.order.converter.DependencyManagementConverter;
import com.lewisd.maven.lint.util.ExpressionEvaluator;
import com.lewisd.maven.lint.util.ModelUtil;
import com.thoughtworks.xstream.XStream;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public class DependenciesAreOrderedRule extends AbstractRule {

    public static final String DEPENDENCY_OUT_OF_ORDER = "The dependency '%s' is out of order. Expected '%s'.";
    public static final String DEPENDENCY_MANAGEMENT_NOT_ORDERED = "Dependency Management is not ordered.";

    @Autowired
    public DependenciesAreOrderedRule(ExpressionEvaluator expressionEvaluator, ModelUtil modelUtil) {
        super(expressionEvaluator, modelUtil);
    }

    @Override
    public void invoke(final MavenProject mavenProject,
                       final Map<String, Object> models,
                       final ResultCollector resultCollector) {

        final Model originalModel = mavenProject.getOriginalModel();

        final DependencyManagement dependencyManagement = originalModel.getDependencyManagement();
        if (dependencyManagement != null) {
            final boolean errors = validateDependencies(mavenProject, resultCollector, dependencyManagement.getDependencies(), "ordered-dependency-management.xml");
            if (errors) {
                resultCollector.addViolation(mavenProject, this, DEPENDENCY_MANAGEMENT_NOT_ORDERED, dependencyManagement.getLocation(""));
            }
        }

        final List<Dependency> dependencies = originalModel.getDependencies();
        if (dependencies != null) {
            validateDependencies(mavenProject, resultCollector, dependencies, "ordered-dependencies.xml");
        }

        final List<Profile> profiles = originalModel.getProfiles();
        for (final Profile profile : profiles) {
            final DependencyManagement profileDependencyManagement = profile.getDependencyManagement();
            if (profileDependencyManagement != null) {
                validateDependencies(mavenProject, resultCollector, profileDependencyManagement.getDependencies(), String.format("%s-ordered-dependency-management.xml", profile
                        .getId()));
            }

            final List<Dependency> profileDependencies = profile.getDependencies();
            if (profileDependencies != null) {
                validateDependencies(mavenProject, resultCollector, profileDependencies, String.format("%s-ordered-dependencies.xml", profile.getId()));
            }
        }
    }

    private int typeOrderFrom(final Dependency dependency) {
        if (dependency.getType() == null) {
            return -1;
        }
        final List<String> order = asList("jar", "test-jar");
        if (!order.contains(dependency.getType())) {
            return order.size();
        }
        return order.indexOf(dependency.getType());
    }

    private int scopeOrderFrom(final Dependency dependency) {
        if (dependency.getScope() == null) {
            return -1;
        }
        final List<String> order = asList("compile", "provided", "runtime", "test", "system");
        if (!order.contains(dependency.getType())) {
            return order.size();
        }
        return order.indexOf(dependency.getType());
    }

    private boolean validateDependencies(final MavenProject mavenProject,
                                         final ResultCollector resultCollector,
                                         final List<Dependency> dependencies,
                                         final String fileName) {

        final Comparator<Dependency> comparator = new Comparator<Dependency>() {
            @Override
            public int compare(final Dependency o1, final Dependency o2) {
                return ComparisonChain.start()
                        .compare(o1.getGroupId(), o2.getGroupId())
                        .compare(o1.getArtifactId(), o2.getArtifactId())
                        .compare(typeOrderFrom(o1), typeOrderFrom(o2))
                        .compare(scopeOrderFrom(o1), scopeOrderFrom(o2))
                        .result();
            }
        };

        final Ordering<Dependency> byRule = Ordering.from(comparator);

        boolean errors = false;

        if (!dependencies.isEmpty() && !byRule.isOrdered(dependencies)) {
            final List<Dependency> sortedDependencies = byRule.sortedCopy(dependencies);

            for (int i = 0; i < dependencies.size(); i++) {
                final Dependency dependency = dependencies.get(i);
                final Dependency sortedDependency = sortedDependencies.get(i);

                if (!dependency.equals(sortedDependency)) {
                    final String message = format(DEPENDENCY_OUT_OF_ORDER, descript(dependency), descript(sortedDependency));
                    resultCollector.addViolation(mavenProject, this, message, dependency.getLocation("artifactId"));
                    errors = true;
                }
            }

            if (errors) {
                createCorrectionsFile(fileName, mavenProject, sortedDependencies);
            }
        }

        return errors;
    }

    private void createCorrectionsFile(final String fileName, final MavenProject mavenProject, final List<Dependency> sortedDependencies) {
        if (mavenProject.getBasedir() == null) {
            return;
        }
        final File outputFile = new File(String.format("%s/target/%s", mavenProject.getBasedir().getPath(), fileName));
        FileWriter writer = null;
        try {
            writer = new FileWriter(outputFile);

            final XStream stream = new XStream();
            final DependencyManagement orderedDependencyManagement = new DependencyManagement();
            orderedDependencyManagement.setDependencies(sortedDependencies);
            stream.registerConverter(new DependencyConverter());
            stream.registerConverter(new DependencyManagementConverter());
            stream.toXML(orderedDependencyManagement, writer);
        } catch (IOException e) {
            throw new RuntimeException(format("Error while creating ordered dependency management file '%s'.", outputFile), e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    throw new RuntimeException(String.format("Error while trying to close file '%s.", outputFile), e);
                }
            }
        }
    }

    @Override
    public String getIdentifier() {
        return "DependenciesAreOrdered";
    }

    @Override
    public String getDescription() {
        return "Dependencies should be ordered alphabetically." +
                "This checks for ordering within the modules dependencies and dependency management," +
                "as well for their respective sections inside the profiles section.";
    }

    private String descript(Dependency dependency) {
        return String.format("%s:%s:%s:%s",
                dependency.getGroupId(),
                dependency.getArtifactId(),
                Optional.fromNullable(dependency.getType()).or(""),
                Optional.fromNullable(dependency.getScope()).or(""));
    }
}
