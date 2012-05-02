package com.lewisd.maven.lint;

import java.io.File;
import java.util.List;

import org.apache.maven.project.MavenProject;


public interface ResultWriter {

	void writeResults(MavenProject mavenProject, List<Violation> violations, File outputFile);

}
