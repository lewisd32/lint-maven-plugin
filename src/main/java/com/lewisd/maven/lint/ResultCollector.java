package com.lewisd.maven.lint;

import org.apache.maven.project.MavenProject;

public interface ResultCollector {

	void writeSummary();

	void addViolation(MavenProject mavenProject, String message, Object object);

	boolean hasViolations();

}
