package com.lewisd.maven.lint.rules;

import com.lewisd.maven.lint.Rule;
import com.lewisd.maven.lint.util.ExpressionEvaluator;
import com.lewisd.maven.lint.util.ModelUtil;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractRule implements Rule {

	protected static final String VERSION_PROPERTIES = "versionProperties";
	protected static final String MAVEN_PROJECT = "mavenProject";

    private final ExpressionEvaluator expressionEvaluator;
    private final ModelUtil modelUtil;

	protected AbstractRule(final ExpressionEvaluator expressionEvaluator, final ModelUtil modelUtil) {
		this.expressionEvaluator = expressionEvaluator;
		this.modelUtil = modelUtil;
	}

	public Set<String> getRequiredModels() {
		final Set<String> requiredModels = new HashSet<String>();
		addRequiredModels(requiredModels);
		return requiredModels;
	}

    public ExpressionEvaluator getExpressionEvaluator() {
        return expressionEvaluator;
    }

    public ModelUtil getModelUtil() {
        return modelUtil;
    }

	protected abstract void addRequiredModels(Set<String> requiredModels);



}
