package com.lewisd.maven.lint;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.maven.project.MavenProject;

public class CachingModelFactory implements ModelFactory {
	
	private final Map<String, ProjectModels> projectModelsByProjectId = new HashMap<String, ProjectModels>();
	private final Map<String, ModelBuilder> modelBuilders = new HashMap<String, ModelBuilder>();

	public CachingModelFactory() {
		
	}
	
	public Map<String, Object> getModels(final MavenProject mavenProject, final Set<String> requiredModels) {
		final ProjectModels projectModels = getProjectModels(mavenProject);
		
		return projectModels.getModels(requiredModels);
	}
	
	private ProjectModels getProjectModels(MavenProject mavenProject) {
		final String id = mavenProject.getId();
		ProjectModels projectModels = projectModelsByProjectId.get(id);
		if (projectModels == null) {
			projectModels = new ProjectModels(mavenProject, Collections.unmodifiableMap(modelBuilders));
			projectModelsByProjectId.put(id, projectModels);
		}
		
		return projectModels;
	}

	public void addModelBuilder(final ModelBuilder modelBuilder) {
		modelBuilders.put(modelBuilder.getModelId(), modelBuilder);
	}

}
