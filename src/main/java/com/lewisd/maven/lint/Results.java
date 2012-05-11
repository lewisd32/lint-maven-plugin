package com.lewisd.maven.lint;

import java.util.List;


public class Results {

	private final List<Violation> violations;

	public Results(final List<Violation> violations) {
		this.violations = violations;
	}

	public List<Violation> getViolations() {
		return violations;
	}
	
}
