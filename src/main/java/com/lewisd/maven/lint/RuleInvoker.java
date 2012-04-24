package com.lewisd.maven.lint;

import java.util.Map;
import java.util.Set;

import org.apache.maven.project.MavenProject;

public class RuleInvoker {
	
	private final ModelFactory modelFactory;
	private final MavenProject mavenProject;

	public RuleInvoker(final MavenProject mavenProject, final ModelFactory modelFactory) {
		this.mavenProject = mavenProject;
		this.modelFactory = modelFactory;
	}

	public void invokeRule(final Rule rule, final ResultCollector resultCollector) {
		final Set<String> requiredModels = rule.getRequiredModels();
		final Map<String, Object> models = modelFactory.getModels(mavenProject, requiredModels);
		rule.invoke(mavenProject, models, resultCollector);
	}
	

}
