package com.lewisd.maven.lint.rules;

import java.util.HashSet;
import java.util.Set;

import com.lewisd.maven.lint.Rule;
import com.lewisd.maven.lint.util.ExpressionEvaluator;
import com.lewisd.maven.lint.util.ModelUtil;

public abstract class AbstractRule implements Rule {

	protected static final String VERSION_PROPERTIES = "versionProperties";
	protected static final String MAVEN_PROJECT = "mavenProject";
	
	protected final ExpressionEvaluator expressionEvaluator;
	protected final ModelUtil modelUtil;
	
	protected AbstractRule() {
		this(null, null);
	}

	protected AbstractRule(final ExpressionEvaluator expressionEvaluator, final ModelUtil modelUtil) {
		this.expressionEvaluator = expressionEvaluator;
		this.modelUtil = modelUtil;
	}

	public Set<String> getRequiredModels() {
		final Set<String> requiredModels = new HashSet<String>();
		addRequiredModels(requiredModels);
		return requiredModels;
	}
	
	protected void addRequiredModels(Set<String> requiredModels) {
		// do nothing by default
	}
	

}
