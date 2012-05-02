package com.lewisd.maven.lint.xstream;

import java.util.List;

import com.lewisd.maven.lint.Violation;

public class Results {

	private final List<Violation> violations;

	public Results(final List<Violation> violations) {
		this.violations = violations;
	}

	public List<Violation> getViolations() {
		return violations;
	}
	
}
