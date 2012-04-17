package com.lewisd.maven.lint;

import org.apache.maven.model.InputLocation;
import org.apache.maven.project.MavenProject;

public interface ResultCollector {

	void writeSummary();

	void addViolation(MavenProject mavenProject, Rule rule, String message, InputLocation inputLocation);

	boolean hasViolations();

	boolean isSuppressed(Rule rule, InputLocation inputLocation);

}
