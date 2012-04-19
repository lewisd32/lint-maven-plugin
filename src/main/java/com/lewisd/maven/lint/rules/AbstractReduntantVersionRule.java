package com.lewisd.maven.lint.rules;

import org.apache.maven.model.InputLocation;
import org.apache.maven.project.MavenProject;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.util.ExpressionEvaluator;
import com.lewisd.maven.lint.util.ModelUtil;

public abstract class AbstractReduntantVersionRule extends AbstractRule {

	public AbstractReduntantVersionRule(
			ExpressionEvaluator expressionEvaluator, ModelUtil modelUtil) {
		super(expressionEvaluator, modelUtil);
	}
	
	protected void checkForRedundantVersions(final MavenProject mavenProject,
			final ResultCollector resultCollector, final Object modelObject,
			final Object inheritedModelObject, final String dependencyDescription, final String inheritedDescription) {
		final String version = modelUtil.getVersion(modelObject);
		final String inheritedVersion = modelUtil.getVersion(inheritedModelObject);
		// both have a version, but if they're different, that might be ok.
		// But if they're the same, then one is redundant.
		if (version != null && inheritedVersion != null && inheritedVersion.equals(version)) {
			final InputLocation location = modelUtil.getLocation(modelObject, "version");
			resultCollector.addViolation(mavenProject, this, dependencyDescription + " '" + modelUtil.getKey(modelObject) +
					"' has same version (" + version + ") as " + inheritedDescription, location);
		}
	}

}
