package com.lewisd.maven.lint.rules;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.model.Coordinates;
import com.lewisd.maven.lint.model.ObjectWithPath;
import com.lewisd.maven.lint.util.ExpressionEvaluator;
import com.lewisd.maven.lint.util.ModelUtil;

public abstract class AbstractReduntantVersionRule extends AbstractRule {

    private final Logger log = Logger.getLogger(this.getClass());

    private final Set<Coordinates> excludedCoordinates = new HashSet<Coordinates>();

    public AbstractReduntantVersionRule(
                                        final ExpressionEvaluator expressionEvaluator, final ModelUtil modelUtil) {
        super(expressionEvaluator, modelUtil);
    }

    protected void checkForRedundantVersions(final MavenProject mavenProject,
                                             final ResultCollector resultCollector, final ObjectWithPath<? extends Object> object,
                                             final ObjectWithPath<? extends Object> inheritedObject, final String dependencyDescription,
                                             final String inheritedDescription) {

        final Object modelObject = object.getObject();
        final Object resolvedModelObject = tryResolveObject(object);

        if (isExcluded(resolvedModelObject)) {
            return;
        }

        final String version = resolveVersion(modelObject, resolvedModelObject);
        final String inheritedVersion = modelUtil.getVersion(tryResolveObject(inheritedObject));
        // both have a version, but if they're different, that might be ok.
        // But if they're the same, then one is redundant.
        if (version != null && inheritedVersion != null && inheritedVersion.equals(version)) {
            final InputLocation location = modelUtil.getLocation(modelObject, "version");
            resultCollector.addViolation(mavenProject, this, dependencyDescription + " '" + modelUtil.getKey(modelObject) +
                                                             "' has same version (" + version + ") as " + inheritedDescription, location);
        }
    }

    private String resolveVersion(final Object modelObject, final Object resolvedModelObject) {
        final String version = modelUtil.getVersion(modelObject);
        if (version != null && version.contains("${")) {
            return modelUtil.getVersion(resolvedModelObject);
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
        final Coordinates coords = modelUtil.getCoordinates(modelObject);
        for (final Coordinates excludedCoordinate : excludedCoordinates) {
            if (excludedCoordinate.matches(coords)) {
                return true;
            }
        }
        return false;
    }

    private Object tryResolveObject(final ObjectWithPath<? extends Object> objectWithPath) {
        try {
            return resolveObject(objectWithPath);
        } catch (final IllegalStateException e) {
            log.warn(e);
            return objectWithPath.getObject();
        }
    }

    /**
     * This uses the path of an object that was found in the "original model", and tries to find
     * the same object in the "model".  The "model" has had properties replaced with values, so
     * this is how we find the resolved version of the object.
     */
    private Object resolveObject(final ObjectWithPath<? extends Object> objectWithPath) {
        final Object object = objectWithPath.getObject();
        final StringBuilder path = new StringBuilder();
        path.append(objectWithPath.getPath());
        path.append("[");
        path.append("groupId='" + modelUtil.getGroupId(object) + "'");
        path.append(" and artifactId='" + modelUtil.getArtifactId(object) + "'");
        final String type = modelUtil.tryGetType(object);
        if (type != null) {
            path.append(" and type='" + type + "'");
        }
        final String classifier = modelUtil.tryGetClassifier(object);
        if (classifier != null) {
            path.append(" and classifier='" + classifier + "']");
        }
        path.append("]");

        final Model model = objectWithPath.getProject().getModel();
        final Collection<Object> objects = expressionEvaluator.getPath(model, path.toString());
        if (objects.isEmpty()) {
            throw new IllegalStateException("Could not resolve " + object + " using path " + path);
        } else if (objects.size() > 1) {
            throw new IllegalStateException("Found " + objects.size() + " objects using path " + path);
        } else {
            return objects.iterator().next();
        }

    }

}
