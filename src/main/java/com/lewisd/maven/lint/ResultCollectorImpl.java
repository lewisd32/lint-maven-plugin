package com.lewisd.maven.lint;

import java.util.LinkedList;
import java.util.List;

import org.apache.maven.model.InputLocation;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.springframework.beans.factory.annotation.Autowired;

public class ResultCollectorImpl implements ResultCollector {

	private final Log log;
	private final List<Violation> violations = new LinkedList<Violation>();
	private ViolationSupressor violationSupressor;

	@Autowired
	public ResultCollectorImpl(final Log log, ViolationSupressor violationSupressor) {
		this.log = log;
		this.violationSupressor = violationSupressor;
	}

	public void writeSummary() {
		if (hasViolations()) {
			log.info("[LINT] Completed with " + violations.size() + " violations");
			for (final Violation violation : violations) {
				log.info("[LINT] " + violation);
			}
		} else {
			log.info("[LINT] Completed with no violations");
		}
	}

	public void addViolation(final MavenProject mavenProject, final Rule rule, final String message, final InputLocation inputLocation) {
		if (!violationSupressor.isSuppressed(rule, inputLocation)) {
			violations.add(new Violation(mavenProject, rule, message, inputLocation));
		}
	}

	public boolean hasViolations() {
		return !violations.isEmpty();
	}

}
