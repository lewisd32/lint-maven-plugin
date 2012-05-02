package com.lewisd.maven.lint;

import java.io.File;

import org.apache.maven.model.InputLocation;
import org.apache.maven.project.MavenProject;

public interface ResultCollector {

	void writeSummary();

	void addViolation(MavenProject mavenProject, Rule rule, String message, InputLocation inputLocation);

	boolean hasViolations();

	void writeResults(File outputFile, MavenProject mavenProject);

}
