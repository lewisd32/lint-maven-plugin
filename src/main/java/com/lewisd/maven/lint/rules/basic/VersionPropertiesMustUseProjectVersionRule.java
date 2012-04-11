package com.lewisd.maven.lint.rules.basic;

import java.util.Map;
import java.util.Set;

import org.apache.maven.project.MavenProject;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.model.VersionProperty;
import com.lewisd.maven.lint.rules.AbstractRule;

public class VersionPropertiesMustUseProjectVersionRule extends AbstractRule {
	
	@Override
	protected void addRequiredModels(Set<String> requiredModels) {
		requiredModels.add(VERSION_PROPERTIES);
	}

	public void invoke(MavenProject mavenProject, final Map<String, Object> models, final ResultCollector resultCollector) {
		final Map<Object, VersionProperty> versionPropertyByObject = (Map<Object, VersionProperty>) models.get(VERSION_PROPERTIES);
		
		for (Map.Entry<Object,VersionProperty> entry : versionPropertyByObject.entrySet()) {
			final VersionProperty versionProperty = entry.getValue();
			for (String propertyName : versionProperty.getPropertyNames()) {
				if (propertyName.equals("version")) {
					resultCollector.addViolation(mavenProject, "Use '${project.version}' instead of '${version}'", entry.getKey());
				}
			}
		}
	}

}
