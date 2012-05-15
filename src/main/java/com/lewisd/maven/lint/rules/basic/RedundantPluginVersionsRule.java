package com.lewisd.maven.lint.rules.basic;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.springframework.beans.factory.annotation.Autowired;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.model.ExtPlugin;
import com.lewisd.maven.lint.rules.AbstractReduntantVersionRule;
import com.lewisd.maven.lint.util.ExpressionEvaluator;
import com.lewisd.maven.lint.util.ModelUtil;

public class RedundantPluginVersionsRule extends AbstractReduntantVersionRule {

	@Autowired
	public RedundantPluginVersionsRule(ExpressionEvaluator expressionEvaluator, ModelUtil modelUtil) {
		super(expressionEvaluator, modelUtil);
	}

	@Override
	protected void addRequiredModels(Set<String> requiredModels) {
	}
	
	@Override
	public String getIdentifier() {
		return "RedundantPluginVersion";
	}
	
	@Override
	public String getDescription() {
		return "Plugin versions should be set in one place, and not overridden without changing the version. " +
				"If, for example, <pluginManagement> sets a version, and <plugins> somewhere overrides it, " +
				"but with the same version, this can make version upgrades more difficult, due to the repetition.";
	}

	public void invoke(MavenProject mavenProject, final Map<String, Object> models, final ResultCollector resultCollector) {
		Model originalModel = mavenProject.getOriginalModel();
		Collection<Plugin> plugins = expressionEvaluator.getPath(originalModel, "build/plugins");
		Collection<Plugin> managedPlugins = expressionEvaluator.getPath(originalModel, "build/pluginManagement/plugins");

		Map<String, Plugin> managedPluginsByManagementKey = modelUtil.mapById(managedPlugins);
		
		for (final Plugin plugin : plugins) {
			Plugin managedDependency = managedPluginsByManagementKey.get(plugin.getId());
			if (managedDependency != null) {
				checkForRedundantVersions(mavenProject, resultCollector, plugin, managedDependency, "Plugin", "in pluginManagement");
			}
			
			List<ExtPlugin> inheritedPlugins = modelUtil.findInheritedPlugins(mavenProject, plugin);
			for (final ExtPlugin inheritedPlugin : inheritedPlugins) {
				// only check the first inherited one that has a version set.
				if (inheritedPlugin.getVersion() != null) {
					checkForRedundantVersions(mavenProject, resultCollector, plugin, inheritedPlugin, "Plugin", "is inherited from " + inheritedPlugin.getMavenProject().getId());
					break;
				}
			}
		}
		
		for (final Plugin plugin : managedPlugins) {
			List<ExtPlugin> inheritedPlugins = modelUtil.findInheritedPlugins(mavenProject, plugin);
			for (final ExtPlugin inheritedPlugin : inheritedPlugins) {
				// only check the first inherited one that has a version set.
				if (inheritedPlugin.getVersion() != null) {
					checkForRedundantVersions(mavenProject, resultCollector, plugin, inheritedPlugin, "Managed plugin", "is inherited from " + inheritedPlugin.getMavenProject().getId());
					break;
				}
			}
		}

	}

	
}
