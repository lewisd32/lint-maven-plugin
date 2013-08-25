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


import com.lewisd.maven.lint.ModelFactory;
import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.Rule;
import com.lewisd.maven.lint.RuleInvoker;
import com.lewisd.maven.lint.report.ReportWriter;
import com.lewisd.maven.lint.report.summary.SummaryReportWriter;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


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
     * @parameter property="project"
     * @required
     * @readonly
     */
	private MavenProject project;

    /**
     * The root spring config location.
     *
     * @parameter property="maven-lint.config.location" default-value="config/maven_lint.xml"
     * @required
     */
	private String configLocation;

    /**
     * Fail the build when there are violations.
     *
     * @parameter property="maven-lint.failOnViolation" default-value="true"
     */
    private boolean failOnViolation;

    /**
     * Specifies the path and filename to save the summary report to. A value of '-' (the default) will write the report to standard out.
     *
     * @parameter property="maven-lint.output.file.summary" default-value="-"
     */
    private File summaryOutputFile;

    /**
     * Specifies the path and filename to save the XML report to.
     *
     * @parameter property="maven-lint.output.file.xml" default-value="${project.build.directory}/maven-lint-result.xml"
     */
    private File xmlOutputFile;

    /**
     * Specifies the path and filename to save the HTML report to.
     *
     * @parameter property="maven-lint.output.file.html" default-value="${project.build.directory}/maven-lint-result.html"
     */
    private File htmlOutputFile;

    /**
     * Comma-separates list of output reports to generate.<br/>
     * Supported reports are:<br/>
     * 	summary (written to standard out, or file specified by summaryOutputFile)<br/>
     *  xml (written to file specified by xmlOutputFile)<br/>
     *  html (written to file specified by xmlOutputFile) (NOT YET IMPLEMENTED)
     *
     * @parameter property="maven-lint.output.reports" default-value="summary,xml"
     */
    private String outputReports;

	private GenericApplicationContext applicationContext;

	private URLClassLoader classLoader;

    public void execute() throws MojoExecutionException, MojoFailureException {
        initConfig();
        ResultCollector resultCollector = executeRules();
        List<String> outputReportList = generateReports(resultCollector);
		if (failOnViolation && resultCollector.hasViolations()) {
            throw new MojoFailureException(generateErrorMessage(outputReportList));
		}
	}

    private List<String> generateReports(ResultCollector resultCollector) throws MojoExecutionException {
        List<String> outputReportList = new LinkedList<String>();
        if (!outputReports.trim().isEmpty()) {
            for (String report : outputReports.trim().split(",")) {
                outputReportList.add(report);
                getLog().info("Writing " + report + " report");
                ReportWriter reportWriter = applicationContext.getBean(report + "ResultWriter", ReportWriter.class);
                final File outputFile = getOutputFileForReport(report);
                getLog().debug("Writing to " + outputFile.getPath());
                try {
                    reportWriter.writeResults(project, resultCollector.getViolations(), outputFile);
                } catch (IOException e) {
                    throw new MojoExecutionException("Error while writing " + report + " report", e);
                }
            }
        }
        return outputReportList;
    }

    private ResultCollector executeRules() throws MojoExecutionException {
        ResultCollector resultCollector = applicationContext.getBean(ResultCollector.class);
        ModelFactory modelFactory = applicationContext.getBean(ModelFactory.class);
        try {

            RuleInvoker ruleInvoker = new RuleInvoker(project, modelFactory);
            Collection<Rule> rules = getRules();

            for (Rule rule : rules) {
                getLog().debug("Running rule " + rule.getIdentifier());
                ruleInvoker.invokeRule(rule, resultCollector);
            }

        } catch (Exception e) {
            throw new MojoExecutionException("Error while performing check", e);
        }
        return resultCollector;
    }

    private URLClassLoader createNewClassloaderWithTestClasspaths() throws DependencyResolutionRequiredException, MalformedURLException {
        List<String> testClasspathElements = project.getTestClasspathElements();
        URL[] testUrls = new URL[testClasspathElements.size()];
        for (int i = 0; i < testClasspathElements.size(); i++) {
            String element = testClasspathElements.get(i);
            testUrls[i] = new File(element).toURI().toURL();
        }
        return  new URLClassLoader(testUrls, Thread.currentThread().getContextClassLoader());
    }

    private void initConfig() throws MojoExecutionException {
        try {
            classLoader = createNewClassloaderWithTestClasspaths();
        } catch (DependencyResolutionRequiredException e) {
            throw new MojoExecutionException("Failed to initialize lint-maven-plugin", e);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to initialize lint-maven-plugin", e);
        }
        initializeSpringContext();
    }

    private void initializeSpringContext() {
        applicationContext = new GenericApplicationContext();
        ClassPathResource classPathResource = new ClassPathResource(configLocation, classLoader);
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(applicationContext);
        xmlBeanDefinitionReader.loadBeanDefinitions(classPathResource);

        applicationContext.getBeanFactory().registerSingleton("log", getLog());

        applicationContext.refresh();
    }

    protected String generateErrorMessage(List<String> outputReportList)
			throws MojoExecutionException {
		final StringBuilder message = new StringBuilder("[LINT] Violations found. ");


		if (outputReportList.isEmpty()) {
			message.append("No output reports have been configured.  Please see documentation regarding the outputReports configuration parameter.");
		} else {
			final boolean wroteSummaryToConsole;
			final ArrayList<String> remainingReports = new ArrayList<String>(outputReportList);
			if (outputReportList.contains("summary") && SummaryReportWriter.isConsole(summaryOutputFile) ) {
				wroteSummaryToConsole = true;
				message.append("For more details, see error messages above");
				remainingReports.remove("summary");
			} else {
				wroteSummaryToConsole = false;
				message.append("For more details");
			}
			if (remainingReports.isEmpty() ) {
				message.append(".");
			} else {
				if (wroteSummaryToConsole) {
					message.append(", or ");
				} else {
					message.append(" see ");
				}
				message.append("results in ");
				if (remainingReports.size() == 1) {
					final File outputFile = getOutputFileForReport(remainingReports.get(0));
					message.append(outputFile.getAbsolutePath());
				} else {
					message.append("one of the following files: ");
					boolean first = true;
					for (final String report : remainingReports) {
						if (!first) {
							message.append(", ");
						}
						final File outputFile = getOutputFileForReport(report);
						message.append(outputFile.getAbsolutePath());
						first = false;
					}
				}
			}
		}
		return message.toString();
	}

	private File getOutputFileForReport(String report)
			throws MojoExecutionException {
		final File outputFile;
		if ("summary".equals(report)) {
			outputFile = summaryOutputFile;
		} else if ("xml".equals(report)) {
			outputFile = xmlOutputFile;
		} else if ("html".equals(report)) {
			outputFile = htmlOutputFile;
		} else {
			throw new MojoExecutionException("Unsupported report: '" + report + "'");
		}
		return outputFile;
	}

	private Collection<Rule> getRules() {
        return applicationContext.getBeansOfType(Rule.class).values();
	}

}
