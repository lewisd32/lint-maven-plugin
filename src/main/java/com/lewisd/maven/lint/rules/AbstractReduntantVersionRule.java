package com.lewisd.maven.lint.rules;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.model.Coordinates;
import com.lewisd.maven.lint.model.ObjectWithPath;
import com.lewisd.maven.lint.util.ExpressionEvaluator;
import com.lewisd.maven.lint.util.ModelUtil;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.PluginParameterExpressionEvaluator;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractReduntantVersionRule extends AbstractRule {

    @Autowired
    private Log log;

    private final Set<Coordinates> excludedCoordinates = new HashSet<Coordinates>();
    private final PluginParameterExpressionEvaluator pluginParameterExpressionEvaluator;

    public AbstractReduntantVersionRule(ExpressionEvaluator expressionEvaluator,
                                        ModelUtil modelUtil,
                                        PluginParameterExpressionEvaluator pluginParameterExpressionEvaluator) {
        super(expressionEvaluator, modelUtil);
        this.pluginParameterExpressionEvaluator = pluginParameterExpressionEvaluator;
    }

    protected void checkForRedundantVersions(final MavenProject mavenProject,
                                             final ResultCollector resultCollector,
                                             final ObjectWithPath<?> object,
                                             final ObjectWithPath<?> inheritedObject,
                                             final String dependencyDescription,
                                             final String inheritedDescription) {

        Object modelObject = object.getObject();
        Object resolvedModelObject = tryResolveObject(object);

        if (isExcluded(resolvedModelObject)) {
            return;
        }

        String version = resolveVersion(modelObject, resolvedModelObject);
        String inheritedVersion = modelUtil.getVersion(tryResolveObject(inheritedObject));
        // both have a version, but if they're different, that might be ok.
        // But if they're the same, then one is redundant.
        if (version != null && inheritedVersion != null && inheritedVersion.equals(version)) {
            InputLocation location = modelUtil.getLocation(modelObject, "version");
            String message = dependencyDescription + " '" + modelUtil.getKey(modelObject) + "' has same version (" + version + ") as " + inheritedDescription;
            resultCollector.addViolation(mavenProject, this, message, location);
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

    private Object tryResolveObject(final ObjectWithPath<?> objectWithPath) {
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
    private Object resolveObject(final ObjectWithPath<?> objectWithPath) {
        Object object = objectWithPath.getObject();
        StringBuilder path = new StringBuilder();
        path.append(objectWithPath.getPath());
        path.append("[");
        path.append("groupId='").append(modelUtil.getGroupId(object)).append("'");
        path.append(" and artifactId='").append(modelUtil.getArtifactId(object)).append("'");

        String type = modelUtil.tryGetType(object);
        if (type != null) {
            path.append(" and type='").append(type).append("'");
        } else {
            path.append(" and not(type)");
        }

        String classifier = modelUtil.tryGetClassifier(object);
        if (classifier != null) {
            path.append(" and classifier='").append(classifier).append("'");
        } else {
            path.append(" and not(classifier)");
        }
        path.append("]");

        // evaluate embedded properties
        if (path.toString().contains("${")) {
            try {
                Object evaluate = pluginParameterExpressionEvaluator.evaluate(path.toString());
                path = new StringBuilder(evaluate.toString());
            } catch (ExpressionEvaluationException e) {
                throw new IllegalStateException(e);
            }
        }

        Model model = objectWithPath.getProject().getModel();
        Collection<Object> objects = expressionEvaluator.getPath(model, path.toString());
        if (objects.isEmpty()) {
            throw new IllegalStateException("Could not resolve " + object + " using path " + path);
        } else if (objects.size() > 1) {
            throw new IllegalStateException("Found " + objects.size() + " objects using path " + path);
        } else {
            return objects.iterator().next();
        }

    }

    protected Log getLog() {
        return log;
    }

    private void setLog(final Log log) {
        this.log = log;
    }

}
