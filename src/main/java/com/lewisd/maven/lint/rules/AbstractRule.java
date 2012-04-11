package com.lewisd.maven.lint.rules;

import java.util.HashSet;
import java.util.Set;

import com.lewisd.maven.lint.Rule;

public abstract class AbstractRule implements Rule {

	protected static final String VERSION_PROPERTIES = "versionProperties";
	protected static final String MAVEN_PROJECT = "mavenProject";

	public Set<String> getRequiredModels() {
		final Set<String> requiredModels = new HashSet<String>();
		addRequiredModels(requiredModels);
		return requiredModels;
	}
	
	protected abstract void addRequiredModels(Set<String> requiredModels); 

}
