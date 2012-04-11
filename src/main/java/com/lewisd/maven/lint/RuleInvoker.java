package com.lewisd.maven.lint;

import java.util.Map;
import java.util.Set;

import org.apache.maven.project.MavenProject;

public class RuleInvoker {
	
	private final RuleModelProvider modelProvider;
	private final MavenProject mavenProject;

	public RuleInvoker(final MavenProject mavenProject, final RuleModelProvider modelProvider) {
		this.mavenProject = mavenProject;
		this.modelProvider = modelProvider;
	}

	public void invokeRule(final Rule rule, final ResultCollector resultCollector) {
		final Set<String> requiredModels = rule.getRequiredModels();
		final Map<String, Object> models = modelProvider.getModels(requiredModels);
		rule.invoke(mavenProject, models, resultCollector);
	}
	

}
