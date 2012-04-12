package com.lewisd.maven.lint;

import org.apache.maven.model.InputLocation;
import org.apache.maven.project.MavenProject;

public class Violation {

	@SuppressWarnings("unused")
	private final MavenProject mavenProject;
	private final String message;
	private final InputLocation inputLocation;

	public Violation(MavenProject mavenProject, String message, InputLocation inputLocation) {
		this.mavenProject = mavenProject;
		this.message = message;
		this.inputLocation = inputLocation;
	}

	@Override
	public String toString() {
		return message + " : " + inputLocation;
	}

}
