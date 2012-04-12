package com.lewisd.maven.lint;

import java.util.LinkedList;
import java.util.List;

import org.apache.maven.model.InputLocation;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

public class ResultCollectorImpl implements ResultCollector {

	private final Log log;
	private final List<Violation> violations = new LinkedList<Violation>();

	public ResultCollectorImpl(final Log log) {
		this.log = log;
	}

	public void writeSummary() {
		if (hasViolations()) {
			log.info("[LINT] Completed with " + violations.size() + " violations");
			for (Violation violation : violations) {
				log.info("[LINT] " + violation);
			}
		} else {
			log.info("[LINT] Completed with no violations");
		}
	}

	public void addViolation(final MavenProject mavenProject, final String message, final InputLocation inputLocation) {
		violations.add(new Violation(mavenProject, message, inputLocation));
	}

	public boolean hasViolations() {
		return !violations.isEmpty();
	}

}
