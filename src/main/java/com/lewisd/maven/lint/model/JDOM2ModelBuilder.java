package com.lewisd.maven.lint.model;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.maven.project.MavenProject;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.located.LocatedJDOMFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lewisd.maven.lint.ModelFactory;

public class JDOM2ModelBuilder extends AbstractModelBuilder {

	@Autowired
	public JDOM2ModelBuilder(final ModelFactory modelFactory) {
		super(modelFactory);
	}

	public Set<String> getRequiredModels() {
		return Collections.singleton(MAVEN_PROJECT);
	}

	public Object buildModel(final Map<String, Object> models) {
		final LocatedJDOMFactory locatedJDOMFactory = new LocatedJDOMFactory();
		final SAXBuilder saxBuilder = new SAXBuilder();
		saxBuilder.setJDOMFactory(locatedJDOMFactory);
		
		final MavenProject mavenProject = (MavenProject) models.get(MAVEN_PROJECT);
		
		try {
			Document document = saxBuilder.build(mavenProject.getFile());
			return document;
		} catch (JDOMException e) {
			throw new RuntimeException("Unable to build JDOM2 model", e);
		} catch (IOException e) {
			throw new RuntimeException("Unable to build JDOM2 model", e);
		}
	}

	public String getModelId() {
		return "jdom2";
	}

}
