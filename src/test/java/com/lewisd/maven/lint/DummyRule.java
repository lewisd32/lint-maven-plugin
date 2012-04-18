package com.lewisd.maven.lint;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.maven.project.MavenProject;

public class DummyRule implements Rule {
	
	private final String identifier;
	
	public DummyRule(final String identifier) {
		this.identifier = identifier;
	}

	@Override
	public Set<String> getRequiredModels() {
		return Collections.emptySet();
	}

	@Override
	public void invoke(MavenProject mavenProject, Map<String, Object> models,
			ResultCollector resultCollector) {
		// do nothing
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

}

