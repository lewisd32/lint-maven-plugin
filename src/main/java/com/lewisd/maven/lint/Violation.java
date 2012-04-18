package com.lewisd.maven.lint;

import org.apache.maven.model.InputLocation;
import org.apache.maven.project.MavenProject;

public class Violation {

	@SuppressWarnings("unused")
	private final MavenProject mavenProject;
	private final String message;
	private final InputLocation inputLocation;
	private final Rule rule;

	public Violation(MavenProject mavenProject, Rule rule, String message, InputLocation inputLocation) {
		this.mavenProject = mavenProject;
		this.rule = rule;
		this.message = message;
		this.inputLocation = inputLocation;
	}

	@Override
	public String toString() {
		return rule.getIdentifier() + ": " + message + " : " + inputLocation.getLineNumber() + ":" + inputLocation.getColumnNumber() + " : " + inputLocation.getSource().getLocation();
	}

	public MavenProject getMavenProject() {
		return mavenProject;
	}

	public String getMessage() {
		return message;
	}

	public InputLocation getInputLocation() {
		return inputLocation;
	}

	public Rule getRule() {
		return rule;
	}

	
}
