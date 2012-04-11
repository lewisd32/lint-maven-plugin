package com.lewisd.maven.lint;

import org.apache.maven.project.MavenProject;

public class Violation {

	private final MavenProject mavenProject;
	private final String message;
	private final Object object;

	public Violation(MavenProject mavenProject, String message, Object object) {
		this.mavenProject = mavenProject;
		this.message = message;
		this.object = object;
	}

	@Override
	public String toString() {
		return message + " : " + mavenProject + " : " + object;
	}

}
