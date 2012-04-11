package com.lewisd.maven.lint.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;

import com.lewisd.maven.lint.ModelBuilder;

public class VersionPropertiesModelBuilder implements ModelBuilder {

	private static final String MAVEN_PROJECT = "mavenProject";

	public Set<String> getRequiredModels() {
		return Collections.singleton(MAVEN_PROJECT);
	}

	public Object buildModel(final Map<String, Object> models) {
		final Map<Object, VersionProperty> versionPropertyByObject = new HashMap<Object, VersionProperty>();
		
		final MavenProject mavenProject = (MavenProject) models.get(MAVEN_PROJECT);
		final Model originalModel = mavenProject.getOriginalModel();
		
		final List<Dependency> originalDependencies = originalModel.getDependencies();
		addDependencyVersionsIfContainsProperty(versionPropertyByObject, originalDependencies);
		
		Build build = originalModel.getBuild();
		if (build != null) {
			List<Plugin> plugins = build.getPlugins();
			for (Plugin plugin : plugins) {
				addVersionIfContainsProperty(versionPropertyByObject, plugin, plugin.getVersion());

				List<Dependency> dependencies = plugin.getDependencies();
				addDependencyVersionsIfContainsProperty(versionPropertyByObject, dependencies);
			}
		}

		return versionPropertyByObject;
	}

	private void addDependencyVersionsIfContainsProperty(final Map<Object, VersionProperty> versionPropertyByObject, final List<Dependency> dependencies) {
		for (final Dependency dependency : dependencies) {
			addVersionIfContainsProperty(versionPropertyByObject, dependency, dependency.getVersion());
		}
	}

	private void addVersionIfContainsProperty( final Map<Object, VersionProperty> versionPropertyByObject, final Object object, final String version) {
		if (version != null && version.contains("${")) {
			versionPropertyByObject.put(object, new VersionProperty(version));
		}
	}

	public String getModelId() {
		return "versionProperties";
	}

}
