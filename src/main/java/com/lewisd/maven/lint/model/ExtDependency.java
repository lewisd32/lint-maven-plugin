package com.lewisd.maven.lint.model;

import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.InputLocation;
import org.apache.maven.project.MavenProject;

public class ExtDependency extends Dependency {
	
	private static final long serialVersionUID = -7220146254987838098L;
	
	private final Dependency dependency;
	private final MavenProject mavenProject;

	public ExtDependency(final MavenProject mavenProject, final Dependency dependency) {
		this.mavenProject = mavenProject;
		this.dependency = dependency;
	}

	
	public MavenProject getMavenProject() {
		return mavenProject;
	}
	
	
	// ---- Generated delegate methods
	
	public int hashCode() {
		return dependency.hashCode();
	}

	public boolean equals(Object obj) {
		return dependency.equals(obj);
	}

	public void addExclusion(Exclusion exclusion) {
		dependency.addExclusion(exclusion);
	}

	public Dependency clone() {
		return dependency.clone();
	}

	public String getArtifactId() {
		return dependency.getArtifactId();
	}

	public String getClassifier() {
		return dependency.getClassifier();
	}

	public List<Exclusion> getExclusions() {
		return dependency.getExclusions();
	}

	public String getGroupId() {
		return dependency.getGroupId();
	}

	public InputLocation getLocation(Object key) {
		return dependency.getLocation(key);
	}

	public String getOptional() {
		return dependency.getOptional();
	}

	public String getScope() {
		return dependency.getScope();
	}

	public String getSystemPath() {
		return dependency.getSystemPath();
	}

	public String getType() {
		return dependency.getType();
	}

	public String getVersion() {
		return dependency.getVersion();
	}

	public void removeExclusion(Exclusion exclusion) {
		dependency.removeExclusion(exclusion);
	}

	public void setArtifactId(String artifactId) {
		dependency.setArtifactId(artifactId);
	}

	public void setClassifier(String classifier) {
		dependency.setClassifier(classifier);
	}

	public void setExclusions(List<Exclusion> exclusions) {
		dependency.setExclusions(exclusions);
	}

	public void setGroupId(String groupId) {
		dependency.setGroupId(groupId);
	}

	public void setLocation(Object key, InputLocation location) {
		dependency.setLocation(key, location);
	}

	public void setOptional(String optional) {
		dependency.setOptional(optional);
	}

	public void setScope(String scope) {
		dependency.setScope(scope);
	}

	public void setSystemPath(String systemPath) {
		dependency.setSystemPath(systemPath);
	}

	public void setType(String type) {
		dependency.setType(type);
	}

	public void setVersion(String version) {
		dependency.setVersion(version);
	}

	public boolean isOptional() {
		return dependency.isOptional();
	}

	public void setOptional(boolean optional) {
		dependency.setOptional(optional);
	}

	public String toString() {
		return dependency.toString();
	}

	public String getManagementKey() {
		return dependency.getManagementKey();
	}

}
