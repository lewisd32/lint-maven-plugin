package com.lewisd.maven.lint.rules.basic;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.model.VersionProperty;
import com.lewisd.maven.lint.rules.AbstractRule;
import com.lewisd.maven.lint.util.ExpressionEvaluator;
import com.lewisd.maven.lint.util.ModelUtil;
import org.apache.maven.model.InputLocation;
import org.apache.maven.project.MavenProject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Set;

public class VersionPropertiesMustUseDotVersionRule extends AbstractRule {

    @Autowired
    public VersionPropertiesMustUseDotVersionRule(final ExpressionEvaluator expressionEvaluator, final ModelUtil modelUtil) {
        super(expressionEvaluator, modelUtil);
    }

    @Override
    protected void addRequiredModels(final Set<String> requiredModels) {
        requiredModels.add(VERSION_PROPERTIES);
    }

    @Override
    public String getIdentifier() {
        return "DotVersionProperty";
    }

    @Override
    public String getDescription() {
        return "The convention is to specify properties used to hold versions as \"some.library.version\", or some-library.version, " +
               "but never some-library-version or some.library-version.";
    }

    @Override
    public void invoke(final MavenProject mavenProject, final Map<String, Object> models, final ResultCollector resultCollector) {
        @SuppressWarnings("unchecked")
        final Map<Object, VersionProperty> versionPropertyByObject = (Map<Object, VersionProperty>) models.get(VERSION_PROPERTIES);

        for (final Map.Entry<Object, VersionProperty> entry : versionPropertyByObject.entrySet()) {
            final VersionProperty versionProperty = entry.getValue();
            for (final String propertyName : versionProperty.getPropertyNames()) {
                if (isVersionProperty(propertyName) && !isAcceptableVersionPropertyName(propertyName)) {
                    final InputLocation location = getModelUtil().getLocation(entry.getKey(), "version");
                    resultCollector.addViolation(mavenProject, this, "Version property names must use '.version', not '-version': '" + propertyName + "'",
                                                 location);
                }
            }
        }
    }

    protected boolean isVersionProperty(final String propertyName) {
        return propertyName.toLowerCase().endsWith("version");
    }

    protected boolean isAcceptableVersionPropertyName(final String propertyName) {
        return propertyName.endsWith(".version") || propertyName.equals("version");
    }

}
