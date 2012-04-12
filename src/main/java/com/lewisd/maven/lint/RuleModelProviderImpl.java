package com.lewisd.maven.lint;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.maven.project.MavenProject;

public class RuleModelProviderImpl implements RuleModelProvider {

	private final HashMap<String, Object> models = new HashMap<String, Object>();
	private final HashMap<String, ModelBuilder> modelBuilders = new HashMap<String, ModelBuilder>();

	public RuleModelProviderImpl(MavenProject mavenProject) {
		
		models.put("mavenProject", mavenProject);
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

	public void addModelBuilder(final ModelBuilder modelBuilder) {
		modelBuilders.put(modelBuilder.getModelId(), modelBuilder);
	}

}
