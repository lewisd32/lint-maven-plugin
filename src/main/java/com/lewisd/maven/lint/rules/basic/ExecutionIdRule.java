package com.lewisd.maven.lint.rules.basic;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.rules.AbstractReduntantVersionRule;
import com.lewisd.maven.lint.util.ExpressionEvaluator;
import com.lewisd.maven.lint.util.ModelUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.project.MavenProject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class ExecutionIdRule extends AbstractReduntantVersionRule {

    @Autowired
    public ExecutionIdRule(final ExpressionEvaluator expressionEvaluator, final ModelUtil modelUtil) {
        super(expressionEvaluator, modelUtil);
    }

    @Override
    protected void addRequiredModels(final Set<String> requiredModels) {
    }

    @Override
    public String getIdentifier() {
        return "ExecutionId";
    }

    @Override
    public String getDescription() {
        return "Executions should always specify an id, so they can be overridden in child modules, and uniquely identified in build logs.";
    }

    @Override
    public void invoke(final MavenProject mavenProject, final Map<String, Object> models, final ResultCollector resultCollector) {
        final Model originalModel = mavenProject.getOriginalModel();

        final List<Plugin> plugins = new ArrayList<Plugin>();

        getExpressionEvaluator().getPath(originalModel, "/build/plugins/plugin");

        plugins.addAll(getExpressionEvaluator().<Plugin>getPath(originalModel, "/build/plugins"));
        plugins.addAll(getExpressionEvaluator().<Plugin>getPath(originalModel, "/build/pluginManagement/plugins"));
        plugins.addAll(getExpressionEvaluator().<Plugin>getPath(originalModel, "/profiles/build/plugins"));
        plugins.addAll(getExpressionEvaluator().<Plugin>getPath(originalModel, "/profiles/build/pluginManagement/plugins"));

        for (final Plugin plugin : plugins) {
            final Collection<PluginExecution> executions = getExpressionEvaluator().getPath(plugin, "/executions");
            for (final PluginExecution execution : executions) {
                // "default" is what maven seems to use if no id is specified
                if (StringUtils.isEmpty(execution.getId()) || "default".equals(execution.getId())) {
                    final InputLocation location = getModelUtil().getLocation(execution, "");
                    resultCollector.addViolation(mavenProject, this, "Executions must specify an id", location);
                }
            }
        }


    }


}
