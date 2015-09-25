package com.lewisd.maven.lint;

import org.apache.maven.model.InputLocation;
import org.apache.maven.project.MavenProject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;

public class ResultCollectorImpl implements ResultCollector {

	private final List<Violation> violations = new LinkedList<Violation>();
	private final ViolationSuppressor violationSuppressor;

	@Autowired
	public ResultCollectorImpl(final ViolationSuppressor violationSuppressor) {
		this.violationSuppressor = violationSuppressor;
	}

	public void addViolation(final MavenProject mavenProject, final Rule rule, final String message, final InputLocation inputLocation) {
		Violation violation = new Violation(mavenProject, rule, message, inputLocation);
		if (!violationSuppressor.isSuppressed(violation)) {
			violations.add(violation);
		}
	}

	public boolean hasViolations() {
		return !violations.isEmpty();
	}

	public List<Violation> getViolations() {
		return violations;
	}

}
