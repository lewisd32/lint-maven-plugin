package com.lewisd.maven.lint.model;

import com.lewisd.maven.lint.ModelFactory;
import org.apache.maven.project.MavenProject;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.located.LocatedJDOMFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class JDOM2ModelBuilder extends AbstractModelBuilder {

	@Autowired
	public JDOM2ModelBuilder(final ModelFactory modelFactory) {
		super(modelFactory);
	}

	public Set<String> getRequiredModels() {
		return Collections.singleton(MAVEN_PROJECT);
	}

	public Object buildModel(final Map<String, Object> models) {
		LocatedJDOMFactory locatedJDOMFactory = new LocatedJDOMFactory();
		SAXBuilder saxBuilder = new SAXBuilder();
		saxBuilder.setJDOMFactory(locatedJDOMFactory);

		MavenProject mavenProject = (MavenProject) models.get(MAVEN_PROJECT);

		try {
			return saxBuilder.build(mavenProject.getFile());
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
