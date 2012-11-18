package com.lewisd.maven.lint.rules;

import java.util.Collection;

import org.apache.maven.model.InputLocation;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.model.ObjectWithPath;
import com.lewisd.maven.lint.util.ExpressionEvaluator;
import com.lewisd.maven.lint.util.ModelUtil;

public abstract class AbstractReduntantVersionRule extends AbstractRule {

    public AbstractReduntantVersionRule(
                                        final ExpressionEvaluator expressionEvaluator, final ModelUtil modelUtil) {
        super(expressionEvaluator, modelUtil);
    }

    protected void checkForRedundantVersions(final MavenProject mavenProject,
                                             final ResultCollector resultCollector, final ObjectWithPath<Object> object,
                                             final ObjectWithPath<Object> inheritedObject, final String dependencyDescription, final String inheritedDescription) {
        final Object modelObject = object.getObject();
        final Object inheritedModelObject = inheritedObject.getObject();

        final String version = resolveVersion(object);
        final String inheritedVersion = resolveVersion(inheritedObject);
        // both have a version, but if they're different, that might be ok.
        // But if they're the same, then one is redundant.
        if (version != null && inheritedVersion != null && inheritedVersion.equals(version)) {
            final InputLocation location = modelUtil.getLocation(modelObject, "version");
            resultCollector.addViolation(mavenProject, this, dependencyDescription + " '" + modelUtil.getKey(modelObject) +
                                                             "' has same version (" + version + ") as " + inheritedDescription, location);
        }
    }

    private String resolveVersion(final ObjectWithPath<Object> objectWithPath) {
        final Object object = objectWithPath.getObject();
        final String version = modelUtil.getVersion(object);
        if (version != null && version.contains("${")) {
            final StringBuilder path = new StringBuilder();
            path.append(objectWithPath.getPath());
            path.append("[");
            path.append("groupId='" + modelUtil.getGroupId(object) + "'");
            path.append(" and artifactId='" + modelUtil.getArtifactId(object) + "'");
            final String type = modelUtil.getType(object);
            if (type != null) {
                path.append(" and type='" + type + "'");
            }
            final String classifier = modelUtil.getClassifier(object);
            if (classifier != null) {
                path.append(" and classifier='" + classifier + "']");
            }
            path.append("]");

            final Model model = objectWithPath.getProject().getModel();
            final Collection<Object> objects = expressionEvaluator.getPath(model, path.toString());
            if (objects.isEmpty()) {
                throw new IllegalStateException("Could not resolve version for " + object + " using path " + path);
            } else if (objects.size() > 1) {
                throw new IllegalStateException("Found " + objects.size() + " objects using path " + path);
            } else {
                return modelUtil.getVersion(objects.iterator().next());
            }
        }
        return version;
    }
}
