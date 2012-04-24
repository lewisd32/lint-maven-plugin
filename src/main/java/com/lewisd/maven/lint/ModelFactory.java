package com.lewisd.maven.lint;

import java.util.Map;
import java.util.Set;

import org.apache.maven.project.MavenProject;

public interface ModelFactory {

	void addModelBuilder(ModelBuilder modelBuilder);

	Map<String, Object> getModels(MavenProject mavenProject, Set<String> requiredModels);
	
}
