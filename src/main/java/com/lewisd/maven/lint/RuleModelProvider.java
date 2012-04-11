package com.lewisd.maven.lint;

import java.util.Map;
import java.util.Set;

public interface RuleModelProvider {

	public abstract Map<String, Object> getModels(
			final Set<String> requiredModels);

	void addModelBuilder(ModelBuilder modelBuilder);

}