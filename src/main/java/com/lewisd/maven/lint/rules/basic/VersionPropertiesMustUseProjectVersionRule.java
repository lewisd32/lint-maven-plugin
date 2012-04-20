package com.lewisd.maven.lint.rules.basic;

import java.util.Map;
import java.util.Set;

import org.apache.maven.model.InputLocation;
import org.apache.maven.project.MavenProject;
import org.springframework.beans.factory.annotation.Autowired;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.model.VersionProperty;
import com.lewisd.maven.lint.rules.AbstractRule;
import com.lewisd.maven.lint.util.ExpressionEvaluator;
import com.lewisd.maven.lint.util.ModelUtil;

public class VersionPropertiesMustUseProjectVersionRule extends AbstractRule {
	
	@Autowired
	public VersionPropertiesMustUseProjectVersionRule(final ExpressionEvaluator expressionEvaluator, final ModelUtil modelUtil) {
		super(expressionEvaluator, modelUtil);
	}

	@Override
	protected void addRequiredModels(final Set<String> requiredModels) {
		requiredModels.add(VERSION_PROPERTIES);
	}
	
	@Override
	public String getIdentifier() {
		return "VersionProp";
	}

	public void invoke(final MavenProject mavenProject, final Map<String, Object> models, final ResultCollector resultCollector) {
		final Map<Object, VersionProperty> versionPropertyByObject = (Map<Object, VersionProperty>) models.get(VERSION_PROPERTIES);
		
		for (final Map.Entry<Object,VersionProperty> entry : versionPropertyByObject.entrySet()) {
			final VersionProperty versionProperty = entry.getValue();
			for (final String propertyName : versionProperty.getPropertyNames()) {
				if (propertyName.equals("version")) {
					final InputLocation location = modelUtil.getLocation(entry.getKey(), "version");
					resultCollector.addViolation(mavenProject, this, "Use '${project.version}' instead of '${version}'", location);
				}
			}
		}
	}

}
