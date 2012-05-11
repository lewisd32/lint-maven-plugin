package com.lewisd.maven.lint.report.html;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.maven.project.MavenProject;

import com.lewisd.maven.lint.Violation;
import com.lewisd.maven.lint.report.AbstractReportWriter;

public class HtmlResultWriter extends AbstractReportWriter {

	public void writeResults(final MavenProject mavenProject, final List<Violation> violations, final File outputFile) {
		FileWriter writer = null;
		try {
			writer = createOutputFileWriter(outputFile);
		} catch (IOException e) {
			throw new RuntimeException("Error while writing results to "+ outputFile, e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					throw new RuntimeException("Error while trying to close file "+ outputFile, e);
				}
			}
		}
	}

}
