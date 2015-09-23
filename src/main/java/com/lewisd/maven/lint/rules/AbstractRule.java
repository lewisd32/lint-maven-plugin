package com.lewisd.maven.lint.rules;

import com.lewisd.maven.lint.Rule;
import com.lewisd.maven.lint.util.ExpressionEvaluator;
import com.lewisd.maven.lint.util.ModelUtil;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractRule implements Rule {

  protected static final String VERSION_PROPERTIES = "versionProperties";

  private final ExpressionEvaluator expressionEvaluator;
  private final ModelUtil           modelUtil;

  protected AbstractRule() {
    this(null, null);
  }

  protected AbstractRule(final ExpressionEvaluator expressionEvaluator,
      final ModelUtil modelUtil) {
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

  public ExpressionEvaluator getExpressionEvaluator() {
    return expressionEvaluator;
  }

  public ModelUtil getModelUtil() {
    return modelUtil;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    AbstractRule rule = (AbstractRule) o;

    if (expressionEvaluator != null ?
        !expressionEvaluator.equals(rule.expressionEvaluator) :
        rule.expressionEvaluator != null) {
      return false;
    }

    return !(modelUtil != null ?
        !modelUtil.equals(rule.modelUtil) :
        rule.modelUtil != null);

  }

  @Override
  public int hashCode() {
    int result =
        expressionEvaluator != null ? expressionEvaluator.hashCode() : 0;
    return 31 * result + (modelUtil != null ? modelUtil.hashCode() : 0);
  }
}
