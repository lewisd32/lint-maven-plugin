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
import com.google.common.collect.Sets;
import com.lewisd.maven.lint.ModelFactory;
import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.Rule;
import com.lewisd.maven.lint.RuleInvoker;
import com.lewisd.maven.lint.report.ReportWriter;
import com.lewisd.maven.lint.report.summary.SummaryReportWriter;
import org.apache.maven.model.PatternSet;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.apache.maven.plugins.annotations.LifecyclePhase.VERIFY;
import static org.apache.maven.plugins.annotations.ResolutionScope.TEST;

/**
 * Perform checks on the POM, and fail the build if violations are found.
 */
@Mojo(name = "check", defaultPhase = VERIFY, requiresDependencyResolution = TEST, threadSafe = true, requiresProject = true)
public class CheckMojo extends AbstractContextMojo {

    /**
     * Fail the build when there are violations.<br/>
     * default: true<br/>
     * can be overriden by giving -Dmaven-lint.failOnViolation=true|false<br/>
     */
    @Parameter(defaultValue = "true", property = "maven-lint.failOnViolation")
    private boolean failOnViolation;

    /**
     * Specifies the path and filename to save the summary report to. A value of '-' (the default) will write the report to standard out.
     */
    @Parameter(defaultValue = "-", property = "maven-lint.output.file.summary", readonly = true)
    private File summaryOutputFile;

    /**
     * Specifies the path and filename to save the XML report to.<br/>
     * defaultValue: ${project.build.directory}/maven-lint-result.xml<br/>
     * can be overriden by giving -Dmaven-lint.output.file.xml=path<br/>
     */
    @Parameter(property = "maven-lint.output.file.xml", defaultValue = "${project.build.directory}/maven-lint-result.xml")
    private File xmlOutputFile;

    /**
     * Specifies the path and filename to save the HTML report to.
     */
    @Parameter(property = "maven-lint.output.file.html", defaultValue = "${project.build.directory}/maven-lint-result.html")
    private File htmlOutputFile;

    /**
     * Comma-separates list of output reports to generate.<br/>
     * Supported reports are:<br/>
     * summary (written to standard out, or file specified by summaryOutputFile)<br/>
     * xml (written to file specified by xmlOutputFile)<br/>
     * html (written to file specified by xmlOutputFile) (NOT YET IMPLEMENTED)
     */
    @Parameter(property = "maven-lint.output.reports", defaultValue = "summary,xml")
    private String outputReports;

    /**
     * based on patterns you can include and exclude rules<br/>
     * default configuration is<br/>
     * <pre>
     * &lt;rules&gt;
     * &nbsp;&nbsp;&lt;excludes/&gt;
     * &nbsp;&nbsp;&lt;includes&gt;
     * &nbsp;&nbsp;&nbsp;&nbsp;&lt;include&gt;*&lt;/include&gt;
     * &nbsp;&nbsp;&lt;/includes/&gt;
     * &lt;/rules/&gt;
     * </pre>              <br/>
     * hints:<br/>
     * - excludes overrides includes <br/>
     * - onlyRunRules are overriden by these rules<br/>
     */
    @Parameter
    private PatternSet rules;

    /**
     * Comma-separates list of rules to be executed<br/>
     * list of rules can be taken from goal 'list'<br/>
     * default: all<br/>
     * can be overriden by giving -Dmaven-lint.rules=all <br/>
     * hint: can be overriden by &lt;rules/&gt;-section<br/>
     */
    @Parameter(property = "maven-lint.rules", defaultValue = "all",required = true)
    private String[] onlyRunRules;

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

        Set<Rule> rulesToRun = new RulesSelector(getRules()).selectRules(rules, onlyRunRules);

        for (Rule rule : rulesToRun) {
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

    private Set<Rule> getRules() {
        final Collection<Rule> ruleCollection = getContext().getBeansOfType(Rule.class).values();
        return Sets.newHashSet(ruleCollection);
    }
}
