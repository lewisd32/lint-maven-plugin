package com.lewisd.maven.lint;

import java.util.Map;
import java.util.Set;

import org.apache.maven.project.MavenProject;

public interface Rule {

	Set<String> getRequiredModels();
	
	void invoke(MavenProject mavenProject, Map<String,Object> models, ResultCollector resultCollector);
	
	String getIdentifier();
	
	String getDescription();
}
