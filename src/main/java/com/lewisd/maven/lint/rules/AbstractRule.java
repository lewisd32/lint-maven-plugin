package com.lewisd.maven.lint.rules;

import java.util.HashSet;
import java.util.Set;

import com.lewisd.maven.lint.Rule;
import com.lewisd.maven.lint.util.ExpressionEvaluator;
import com.lewisd.maven.lint.util.ModelUtil;

public abstract class AbstractRule implements Rule {

	protected static final String VERSION_PROPERTIES = "versionProperties";
	protected static final String MAVEN_PROJECT = "mavenProject";
	
	protected ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator();
	protected ModelUtil modelUtil = new ModelUtil();

	public Set<String> getRequiredModels() {
		final Set<String> requiredModels = new HashSet<String>();
		addRequiredModels(requiredModels);
		return requiredModels;
	}
	
	protected abstract void addRequiredModels(Set<String> requiredModels);
	

}
