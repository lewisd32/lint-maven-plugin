package com.lewisd.maven.lint.rules.basic;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.maven.project.MavenProject;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.Rule;

public class VersionPropertiesMustUsePeriodRule implements Rule {

	private static final String VERSION_PROPERTIES = "versionProperties";
	private static final String MAVEN_PROJECT = "mavenProject";

	public Set<String> getRequiredModels() {
		final Set<String> requiredModels = new HashSet<String>();
		requiredModels.add(VERSION_PROPERTIES);
		requiredModels.add(MAVEN_PROJECT);
		return requiredModels;
	}

	public void invoke(MavenProject mavenProject,
			final Map<String, Object> models, final ResultCollector resultCollector) {
		final Map<Object, String> versionPropertyByObject = (Map<Object, String>) models.get(VERSION_PROPERTIES);
		
		for (Map.Entry<Object,String> entry : versionPropertyByObject.entrySet()) {
			final String version = entry.getValue();
			final int start = version.indexOf("${") + 2;
			final int end = version.indexOf("}", start);
			final String property = version.substring(start, end);
			if (property.contains("-")) {
				resultCollector.addViolation(mavenProject, "Version property names must not contain a hyphen: '" + property + "'", entry.getKey());
			}
		}
	}
	
	

}
