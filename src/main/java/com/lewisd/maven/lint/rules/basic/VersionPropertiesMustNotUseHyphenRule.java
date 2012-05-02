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

public class VersionPropertiesMustNotUseHyphenRule extends AbstractRule {

	@Autowired
	public VersionPropertiesMustNotUseHyphenRule(ExpressionEvaluator expressionEvaluator, ModelUtil modelUtil) {
		super(expressionEvaluator, modelUtil);
	}

	@Override
	protected void addRequiredModels(Set<String> requiredModels) {
		requiredModels.add(VERSION_PROPERTIES);
	}
	
	@Override
	public String getIdentifier() {
		return "VersionPropHyphen";
	}

	public void invoke(MavenProject mavenProject, final Map<String, Object> models, final ResultCollector resultCollector) {
		@SuppressWarnings("unchecked")
		final Map<Object, VersionProperty> versionPropertyByObject = (Map<Object, VersionProperty>) models.get(VERSION_PROPERTIES);
		
		for (Map.Entry<Object,VersionProperty> entry : versionPropertyByObject.entrySet()) {
			final VersionProperty versionProperty = entry.getValue();
			for (String propertyName : versionProperty.getPropertyNames()) {
				if (propertyName.contains("-version")) {
					InputLocation location = modelUtil.getLocation(entry.getKey(), "version");
					resultCollector.addViolation(mavenProject, this, "Version property names must use '.version', not '-version': '" + propertyName + "'", location);
				}
			}
		}
	}
	
}
