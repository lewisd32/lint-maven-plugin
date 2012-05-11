package com.lewisd.maven.lint;

import java.util.List;

import org.apache.maven.model.InputLocation;
import org.apache.maven.project.MavenProject;

public interface ResultCollector {

	void addViolation(MavenProject mavenProject, Rule rule, String message, InputLocation inputLocation);

	boolean hasViolations();

	List<Violation> getViolations();

}
