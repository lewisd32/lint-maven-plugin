package com.lewisd.maven.lint.report.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.maven.project.MavenProject;

import com.lewisd.maven.lint.Results;
import com.lewisd.maven.lint.Violation;
import com.lewisd.maven.lint.report.AbstractReportWriter;
import com.thoughtworks.xstream.XStream;

public class XmlResultWriter extends AbstractReportWriter {

	public void writeResults(final MavenProject mavenProject, final List<Violation> violations, final File outputFile) {
		XStream xstream = new XStream();
		xstream.registerConverter(new ViolationConvertor());
		xstream.registerConverter(new ResultsConvertor());
		xstream.alias("results", Results.class);
		Results results = new Results(violations);
		FileWriter writer = null;
		try {
			writer = createOutputFileWriter(outputFile);
			xstream.toXML(results, writer);
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
