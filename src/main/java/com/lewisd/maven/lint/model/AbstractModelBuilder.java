package com.lewisd.maven.lint.model;

import com.lewisd.maven.lint.ModelBuilder;
import com.lewisd.maven.lint.ModelFactory;

import javax.annotation.PostConstruct;

public abstract class AbstractModelBuilder implements ModelBuilder {

	protected static final String MAVEN_PROJECT = "mavenProject";

	private final ModelFactory modelFactory;

	public AbstractModelBuilder(final ModelFactory modelFactory) {
		this.modelFactory = modelFactory;
	}

	@PostConstruct
	public void init() {
		modelFactory.addModelBuilder(this);
	}


}
