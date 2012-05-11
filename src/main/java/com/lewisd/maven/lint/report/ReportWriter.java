package com.lewisd.maven.lint.report;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.project.MavenProject;

import com.lewisd.maven.lint.Violation;


public interface ReportWriter {

	void writeResults(MavenProject mavenProject, List<Violation> violations, File outputFile) throws IOException;

}
