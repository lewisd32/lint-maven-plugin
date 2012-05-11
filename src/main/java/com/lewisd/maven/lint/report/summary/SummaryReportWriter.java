package com.lewisd.maven.lint.report.summary;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.springframework.beans.factory.annotation.Autowired;

import com.lewisd.maven.lint.Violation;
import com.lewisd.maven.lint.report.AbstractReportWriter;

public class SummaryReportWriter extends AbstractReportWriter {
	
	private final Log log;
	
	@Autowired
	public SummaryReportWriter(Log log) {
		this.log = log;
	}

	@Override
	public void writeResults(MavenProject mavenProject, List<Violation> violations, File outputFile) throws IOException {
		final Outputter out;
		if ("-".equals(outputFile.getName())) {
			out = new MavenLogOutputter();
		} else {
			out = new FileOutputter(outputFile);
		}
		
		if (violations.isEmpty()) {
			out.write("[LINT] Completed with no violations");
		} else {
			out.write("[LINT] Completed with " + violations.size() + " violations");
			for (final Violation violation : violations) {
				out.write("[LINT] " + violation);
			}
		}
		
		out.close();
	}
	
	private interface Outputter {
		void write(String message);
		void close();		
	}
	
	private class MavenLogOutputter implements Outputter {
		@Override
		public void write(String message) {
			log.info(message);
		}

		@Override
		public void close() {
		}
	}
	
	private class FileOutputter implements Outputter {
		private PrintWriter writer;

		public FileOutputter(File outputFile) throws IOException {
			writer = new PrintWriter(new FileWriter(outputFile));
		}

		@Override
		public void write(String message) {
			writer.println(message);
		}

		@Override
		public void close() {
			writer.close();
		}
	}
}

