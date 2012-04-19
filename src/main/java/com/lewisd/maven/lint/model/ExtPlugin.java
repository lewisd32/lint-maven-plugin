package com.lewisd.maven.lint.model;

import java.util.List;
import java.util.Map;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.project.MavenProject;

public class ExtPlugin extends Plugin {

	private static final long serialVersionUID = -2014726351280459221L;

	private final MavenProject mavenProject;
	private final Plugin plugin;

	public ExtPlugin(final MavenProject mavenProject, final Plugin plugin) {
		this.mavenProject = mavenProject;
		this.plugin = plugin;
	}

	public MavenProject getMavenProject() {
		return mavenProject;
	}

	
	// ---- Generated delegate methods

	public void addDependency(Dependency dependency) {
		plugin.addDependency(dependency);
	}

	public Object getConfiguration() {
		return plugin.getConfiguration();
	}

	public void addExecution(PluginExecution pluginExecution) {
		plugin.addExecution(pluginExecution);
	}

	public String getInherited() {
		return plugin.getInherited();
	}

	public Plugin clone() {
		return plugin.clone();
	}

	public InputLocation getLocation(Object key) {
		return plugin.getLocation(key);
	}

	public void setConfiguration(Object configuration) {
		plugin.setConfiguration(configuration);
	}

	public void setInherited(String inherited) {
		plugin.setInherited(inherited);
	}

	public String getArtifactId() {
		return plugin.getArtifactId();
	}

	public void setLocation(Object key, InputLocation location) {
		plugin.setLocation(key, location);
	}

	public List<Dependency> getDependencies() {
		return plugin.getDependencies();
	}

	public List<PluginExecution> getExecutions() {
		return plugin.getExecutions();
	}

	public boolean isInherited() {
		return plugin.isInherited();
	}

	public void setInherited(boolean inherited) {
		plugin.setInherited(inherited);
	}

	public void unsetInheritanceApplied() {
		plugin.unsetInheritanceApplied();
	}

	public String getExtensions() {
		return plugin.getExtensions();
	}

	public boolean isInheritanceApplied() {
		return plugin.isInheritanceApplied();
	}

	public Object getGoals() {
		return plugin.getGoals();
	}

	public String getGroupId() {
		return plugin.getGroupId();
	}

	public String getVersion() {
		return plugin.getVersion();
	}

	public void removeDependency(Dependency dependency) {
		plugin.removeDependency(dependency);
	}

	public void removeExecution(PluginExecution pluginExecution) {
		plugin.removeExecution(pluginExecution);
	}

	public void setArtifactId(String artifactId) {
		plugin.setArtifactId(artifactId);
	}

	public void setDependencies(List<Dependency> dependencies) {
		plugin.setDependencies(dependencies);
	}

	public void setExecutions(List<PluginExecution> executions) {
		plugin.setExecutions(executions);
	}

	public void setExtensions(String extensions) {
		plugin.setExtensions(extensions);
	}

	public void setGoals(Object goals) {
		plugin.setGoals(goals);
	}

	public void setGroupId(String groupId) {
		plugin.setGroupId(groupId);
	}

	public void setVersion(String version) {
		plugin.setVersion(version);
	}

	public boolean isExtensions() {
		return plugin.isExtensions();
	}

	public void setExtensions(boolean extensions) {
		plugin.setExtensions(extensions);
	}

	public void flushExecutionMap() {
		plugin.flushExecutionMap();
	}

	public Map<String, PluginExecution> getExecutionsAsMap() {
		return plugin.getExecutionsAsMap();
	}

	public String getId() {
		return plugin.getId();
	}

	public String getKey() {
		return plugin.getKey();
	}

	public boolean equals(Object other) {
		return plugin.equals(other);
	}

	public int hashCode() {
		return plugin.hashCode();
	}

	public String toString() {
		return plugin.toString();
	}

}
