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


import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.lewisd.maven.lint.ModelFactory;
import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.Rule;
import com.lewisd.maven.lint.RuleInvoker;
import com.lewisd.maven.lint.report.ReportWriter;
import com.lewisd.maven.lint.report.summary.SummaryReportWriter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.IOException;
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
public class CheckMojo extends AbstractContextMojo {

    /**
     * Fail the build when there are violations.
     *
     * @parameter expression="${maven-lint.failOnViolation}" default-value="true"
     */
    private boolean failOnViolation;

    /**
     * Specifies the path and filename to save the summary report to. A value of '-' (the default) will write the report to standard out.
     *
     * @parameter expression="${maven-lint.output.file.summary}" default-value="-"
     * @readonly
     */
    private File summaryOutputFile;

    /**
     * Specifies the path and filename to save the XML report to.
     *
     * @parameter expression="${maven-lint.output.file.xml}" default-value="${project.build.directory}/maven-lint-result.xml"
     */
    private File xmlOutputFile;

    /**
     * Specifies the path and filename to save the HTML report to.
     *
     * @parameter expression="${maven-lint.output.file.html}" default-value="${project.build.directory}/maven-lint-result.html"
     */
    private File htmlOutputFile;

    /**
     * Comma-separates list of output reports to generate.<br/>
     * Supported reports are:<br/>
     * summary (written to standard out, or file specified by summaryOutputFile)<br/>
     * xml (written to file specified by xmlOutputFile)<br/>
     * html (written to file specified by xmlOutputFile) (NOT YET IMPLEMENTED)
     *
     * @parameter expression="${maven-lint.output.reports}" default-value="summary,xml"
     */
    private String outputReports;

    /**
     * @parameter expression="${maven-lint.rules}" default-value="all"
     */
    private String[] rules;

    public void execute() throws MojoExecutionException, MojoFailureException {

        init();

        ResultCollector resultCollector = getContext().getBean(ResultCollector.class);

        executeRules(resultCollector);

        List<String> outputReports = fillOutputReports(resultCollector);

        if (failOnViolation && resultCollector.hasViolations()) {
            final String message = generateErrorMessage(outputReports);
            throw new MojoFailureException(message);
        }
    }

    private List<String> fillOutputReports(ResultCollector resultCollector) throws MojoExecutionException {
        List<String> outputReportList = new LinkedList<String>();
        if (!outputReports.trim().isEmpty()) {
            for (String report : outputReports.trim().split(",")) {
                outputReportList.add(report);
                getLog().info("Writing " + report + " report");
                ReportWriter reportWriter = getContext().getBean(report + "ResultWriter", ReportWriter.class);
                final File outputFile = getOutputFileForReport(report);
                getLog().debug("Writing to " + outputFile.getPath());
                try {
                    reportWriter.writeResults(getProject(), resultCollector.getViolations(), outputFile);
                } catch (IOException e) {
                    throw new MojoExecutionException("Error while writing " + report + " report", e);
                }
            }
        }
        return outputReportList;
    }

    private void executeRules(ResultCollector resultCollector) throws MojoExecutionException {
        ModelFactory modelFactory = getContext().getBean(ModelFactory.class);
        RuleInvoker ruleInvoker = new RuleInvoker(getProject(), modelFactory);
        RulesSelector rulesSelector = new RulesSelector(getRules());

        for (Rule rule : rulesSelector.selectRule(rules)) {
            executeRule(resultCollector, ruleInvoker, rule);
        }
    }

    private void executeRule(ResultCollector resultCollector, RuleInvoker ruleInvoker, Rule rule) throws MojoExecutionException {
        getLog().debug("Running rule " + rule.getIdentifier());
        try {
            ruleInvoker.invokeRule(rule, resultCollector);
        } catch (Exception e) {
            throw new MojoExecutionException("Error while performing check", e);
        }
    }

    @VisibleForTesting
    String generateErrorMessage(List<String> outputReportList)
            throws MojoExecutionException {
        final StringBuffer message = new StringBuffer("[LINT] Violations found. ");


        if (outputReportList.isEmpty()) {
            message.append("No output reports have been configured.  Please see documentation regarding the outputReports configuration parameter.");
        } else {
            final boolean wroteSummaryToConsole;
            final ArrayList<String> remainingReports = new ArrayList<String>(outputReportList);
            if (outputReportList.contains("summary") && SummaryReportWriter.isConsole(summaryOutputFile)) {
                wroteSummaryToConsole = true;
                message.append("For more details, see error messages above");
                remainingReports.remove("summary");
            } else {
                wroteSummaryToConsole = false;
                message.append("For more details");
            }
            if (remainingReports.isEmpty()) {
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

    private List<Rule> getRules() {
        final Collection<Rule> ruleCollection = getContext().getBeansOfType(Rule.class).values();
        return Lists.newArrayList(ruleCollection);
    }
}
