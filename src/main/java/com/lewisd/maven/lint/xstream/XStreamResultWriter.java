package com.lewisd.maven.lint.xstream;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.maven.project.MavenProject;

import com.lewisd.maven.lint.ResultWriter;
import com.lewisd.maven.lint.Violation;
import com.thoughtworks.xstream.XStream;

public class XStreamResultWriter implements ResultWriter {

	public void writeResults(final MavenProject mavenProject, final List<Violation> violations, final File outputFile) {
		XStream xstream = new XStream();
		xstream.registerConverter(new ViolationConvertor());
		xstream.registerConverter(new ResultsConvertor());
		xstream.alias("results", Results.class);
		Results results = new Results(violations);
		FileWriter writer = null;
		try {
	        File parentFile = outputFile.getAbsoluteFile().getParentFile();
	        if (!parentFile.exists()) {
	            parentFile.mkdirs();
	        }
			writer = new FileWriter(outputFile);
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
