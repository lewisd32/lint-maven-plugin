package com.lewisd.maven.lint.report.html;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.maven.project.MavenProject;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.lewisd.maven.lint.Violation;
import com.lewisd.maven.lint.report.AbstractReportWriter;

public class HtmlResultWriter extends AbstractReportWriter {

	public void writeResults(final MavenProject mavenProject, final List<Violation> violations, final File outputFile) {
		FileWriter writer = null;
		try {
			writer = createOutputFileWriter(outputFile);
			
	        VelocityEngine ve = initializeVelocity();

	        renderHtml(writer, ve, mavenProject, violations);
	        
	        
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

	private VelocityEngine initializeVelocity() {
		VelocityEngine ve = new VelocityEngine();
		
		Properties properties = getVelocityProperties();
		
		ve.init(properties);
		return ve;
	}
	
	private Properties getVelocityProperties() {
		Properties properties = new Properties();
		properties.setProperty("resource.loader", "class");
		properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		return properties;
	}
	
	private VelocityContext getVelocityContext(MavenProject mavenProject, List<Violation> violations) {
		VelocityContext context = new VelocityContext();
		context.put("project", mavenProject);
		context.put("violations", violations);
		return context;
	}

	private void renderHtml(FileWriter writer, VelocityEngine ve, MavenProject mavenProject, List<Violation> violations) {
		Template template = ve.getTemplate( "velocity/html-report.vm" );
		VelocityContext context = getVelocityContext(mavenProject, violations);
		template.merge( context, writer );
	}
	
}
