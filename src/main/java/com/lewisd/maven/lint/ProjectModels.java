package com.lewisd.maven.lint;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.maven.project.MavenProject;

public class ProjectModels {
	
	private final Map<String, Object> models = new HashMap<String, Object>();
	private final Map<String, ModelBuilder> modelBuilders;

	public ProjectModels(MavenProject mavenProject, Map<String, ModelBuilder> modelBuilders) {
		models.put("mavenProject", mavenProject);
		this.modelBuilders = modelBuilders;
	}

	public Map<String, Object> getModels(final Set<String> requiredModels) {
		Map<String,Object> ruleModels = new HashMap<String, Object>();

		for (String modelId : requiredModels) {
			Object model = models.get(modelId);
			if (model == null) {
				ModelBuilder modelBuilder = modelBuilders.get(modelId);
				if (modelBuilder == null) {
					throw new IllegalArgumentException("No modelBuilder known for modelId '" + modelId + "'");
				}
				
				Map<String, Object> dependentModels = getModels(modelBuilder.getRequiredModels());
				
				model = modelBuilder.buildModel(dependentModels);
				models.put(modelId, model);
			}
			ruleModels.put(modelId, model);
		}
		return ruleModels;
	}

}
