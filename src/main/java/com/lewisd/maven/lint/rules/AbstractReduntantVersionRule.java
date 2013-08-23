package com.lewisd.maven.lint.rules;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.model.Coordinates;
import com.lewisd.maven.lint.model.ObjectWithPath;
import com.lewisd.maven.lint.util.ExpressionEvaluator;
import com.lewisd.maven.lint.util.ModelUtil;
import org.apache.log4j.Logger;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractReduntantVersionRule extends AbstractRule {

    private final Logger log = Logger.getLogger(this.getClass());

    private final Set<Coordinates> excludedCoordinates = new HashSet<Coordinates>();

    public AbstractReduntantVersionRule(
                                        final ExpressionEvaluator expressionEvaluator, final ModelUtil modelUtil) {
        super(expressionEvaluator, modelUtil);
    }

    protected void checkForRedundantVersions(final MavenProject mavenProject,
                                             final ResultCollector resultCollector, final ObjectWithPath<Object> object,
                                             final ObjectWithPath<Object> inheritedObject, final String dependencyDescription, final String inheritedDescription) {

        final Object modelObject = object.getObject();
        final Object resolvedModelObject = tryResolveObject(object);

        if (isExcluded(resolvedModelObject)) {
            return;
        }

        final String version = resolveVersion(modelObject, resolvedModelObject);
        final String inheritedVersion = getModelUtil().getVersion(tryResolveObject(inheritedObject));
        // both have a version, but if they're different, that might be ok.
        // But if they're the same, then one is redundant.
        if (version != null && inheritedVersion != null && inheritedVersion.equals(version)) {
            final InputLocation location = getModelUtil().getLocation(modelObject, "version");
            resultCollector.addViolation(mavenProject, this, dependencyDescription + " '" + getModelUtil().getKey(modelObject) +
                                                             "' has same version (" + version + ") as " + inheritedDescription, location);
        }
    }

    private String resolveVersion(final Object modelObject, final Object resolvedModelObject) {
        final String version = getModelUtil().getVersion(modelObject);
        if (version != null && version.contains("${")) {
            return getModelUtil().getVersion(resolvedModelObject);
        }
        return version;
    }

    public void setExcludedCoordinates(final Set<String> coordinates) {
        excludedCoordinates.clear();
        for (final String coordinate : coordinates) {
            excludedCoordinates.add(Coordinates.parse(coordinate));
        }
    }

    private boolean isExcluded(final Object modelObject) {
        final Coordinates coords = getModelUtil().getCoordinates(modelObject);
        for (final Coordinates excludedCoordinate : excludedCoordinates) {
            if (excludedCoordinate.matches(coords)) {
                return true;
            }
        }
        return false;
    }

    private Object tryResolveObject(final ObjectWithPath<Object> objectWithPath) {
        try {
            return resolveObject(objectWithPath);
        } catch (final IllegalStateException e) {
            log.warn(e);
            return objectWithPath.getObject();
        }
    }

    private Object resolveObject(final ObjectWithPath<Object> objectWithPath) {
        final Object object = objectWithPath.getObject();
        final StringBuilder path = new StringBuilder();
        path.append(objectWithPath.getPath());
        path.append("[");
        path.append("groupId='" + getModelUtil().getGroupId(object) + "'");
        path.append(" and artifactId='" + getModelUtil().getArtifactId(object) + "'");
        final String type = getModelUtil().tryGetType(object);
        if (type != null) {
            path.append(" and type='" + type + "'");
        }
        final String classifier = getModelUtil().tryGetClassifier(object);
        if (classifier != null) {
            path.append(" and classifier='" + classifier + "']");
        }
        path.append("]");

        final Model model = objectWithPath.getProject().getModel();
        final Collection<Object> objects = getExpressionEvaluator().getPath(model, path.toString());
        if (objects.isEmpty()) {
            throw new IllegalStateException("Could not resolve " + object + " using path " + path);
        } else if (objects.size() > 1) {
            throw new IllegalStateException("Found " + objects.size() + " objects using path " + path);
        } else {
            return objects.iterator().next();
        }

    }

}
