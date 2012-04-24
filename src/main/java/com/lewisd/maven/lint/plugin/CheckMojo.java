package com.lewisd.maven.lint.plugin;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;

import com.lewisd.maven.lint.ModelFactory;
import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.Rule;
import com.lewisd.maven.lint.RuleInvoker;

/**
 * Perform checks on the POM, and fail the build if violations are found.
 * 
 * @goal check
 * @phase verify
 * @requiresDependencyResolution test
 * @threadSafe
*/
public class CheckMojo extends AbstractMojo {

	/**
	 * The Maven Project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;
	
	/**
	 * The root spring config location.
	 * 
	 * @parameter expression="${maven.lint.config.location}" default-value="config/maven_lint.xml"
	 * @required
	 */
	private String configLocation;
	
    /**
     * Fail the build when there are violations
     *
     * @parameter expression="${lint.failOnViolation}" default-value="true"
     */
    private boolean failOnViolation;

	private GenericApplicationContext applicationContext;

	private URLClassLoader classLoader;
	
	private void initializeConfig() throws DependencyResolutionRequiredException, IOException {
		
		@SuppressWarnings("rawtypes")
		List testClasspathElements = project.getTestClasspathElements();
		URL[] testUrls = new URL[testClasspathElements.size()];
		for (int i = 0; i < testClasspathElements.size(); i++) {
		  String element = (String) testClasspathElements.get(i);
		  testUrls[i] = new File(element).toURI().toURL();
		}
		classLoader = new URLClassLoader(testUrls, Thread.currentThread().getContextClassLoader());
	
		applicationContext = new GenericApplicationContext();
		ClassPathResource classPathResource = new ClassPathResource(configLocation, classLoader);
		XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(applicationContext);
		xmlBeanDefinitionReader.loadBeanDefinitions(classPathResource);
		
		applicationContext.getBeanFactory().registerSingleton("log", getLog());
		
		applicationContext.refresh();
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			initializeConfig();
		} catch (DependencyResolutionRequiredException e) {
			throw new MojoExecutionException("Failed to initialize lint-maven-plugin", e);
		} catch (IOException e) {
			throw new MojoExecutionException("Failed to initialize lint-maven-plugin", e);
		}
		ResultCollector resultCollector = applicationContext.getBean(ResultCollector.class);
		ModelFactory modelFactory = applicationContext.getBean(ModelFactory.class);
		try {
			
			RuleInvoker ruleInvoker = new RuleInvoker(project, modelFactory);
			Collection<Rule> rules = getRules();
			
			for (Rule rule : rules) {
				ruleInvoker.invokeRule(rule, resultCollector);
			}
			
		} catch (Exception e) {
			throw new MojoExecutionException("Error while performing check", e);
		}
		
		resultCollector.writeSummary();

		if (failOnViolation && resultCollector.hasViolations()) {
			throw new MojoFailureException( "[LINT] Violations found." );
		}
	}

	private Collection<Rule> getRules() {
		Map<String, Rule> rules = applicationContext.getBeansOfType(Rule.class);
		return rules.values();
	}
	
}
