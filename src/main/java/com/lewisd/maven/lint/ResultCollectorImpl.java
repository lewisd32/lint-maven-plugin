package com.lewisd.maven.lint;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.model.InputLocation;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.springframework.beans.factory.annotation.Autowired;

public class ResultCollectorImpl implements ResultCollector {

	private final Log log;
	private final List<Violation> violations = new LinkedList<Violation>();
	private final ViolationSuppressor violationSuppressor;
	private final ResultWriter resultWriter;

	@Autowired
	public ResultCollectorImpl(final Log log, final ViolationSuppressor violationSuppressor, final ResultWriter resultWriter) {
		this.log = log;
		this.violationSuppressor = violationSuppressor;
		this.resultWriter = resultWriter;
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
	
	public void writeResults(File outputFile, MavenProject mavenProject) {
		resultWriter.writeResults(mavenProject, violations, outputFile);
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

}
