package com.lewisd.maven.lint.plugin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.maven.cli.MavenCli;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import static org.fest.assertions.api.Assertions.fail;
import static org.junit.Assert.assertTrue;

public class CheckMojoTest {

	private CheckMojo checkMojo;
	private String xmlFilePath;
	private String htmlFilePath;

	@Before
	public void before() throws Exception {
		final File xmlFile = new File("report.xml");
		final File htmlFile = new File("report.html");

		xmlFilePath = xmlFile.getAbsolutePath();
		htmlFilePath = htmlFile.getAbsolutePath();

		checkMojo = new CheckMojo();
		setField(checkMojo, "summaryOutputFile", new File("-"));
		setField(checkMojo, "xmlOutputFile", xmlFile);
		setField(checkMojo, "htmlOutputFile", htmlFile);
	}

	private void setField(Object object, String fieldname, Object value) throws Exception {
		final Field field = object.getClass().getDeclaredField(fieldname);
		field.setAccessible(true);
		field.set(object, value);
	}

	@Test
	public void testErrorMessageWhenNoReportsConfigured() throws Exception {
		final String expected = "[LINT] Violations found. No output reports have been configured.  Please see documentation regarding the outputReports configuration parameter.";
		final String actual = checkMojo.generateErrorMessage(new ArrayList<String>());
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testErrorMessageForSummaryOnly() throws Exception {
		final String expected = "[LINT] Violations found. For more details, see error messages above.";
		final String actual = checkMojo.generateErrorMessage(Lists.newArrayList("summary"));
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testErrorMessageForSummaryAndOneReport() throws Exception {
		final String expected = "[LINT] Violations found. For more details, see error messages above, or results in " + xmlFilePath;
		final String actual = checkMojo.generateErrorMessage(Lists.newArrayList("summary", "xml"));
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testErrorMessageForSummaryAndTwoReports() throws Exception {
		final String expected = "[LINT] Violations found. For more details, see error messages above, or results in one of the following files: " + xmlFilePath + ", " + htmlFilePath;
		final String actual = checkMojo.generateErrorMessage(Lists.newArrayList("summary", "xml", "html"));
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testErrorMessageForNoSummaryAndOneReport() throws Exception {
		final String expected = "[LINT] Violations found. For more details see results in " + xmlFilePath;
		final String actual = checkMojo.generateErrorMessage(Lists.newArrayList("xml"));
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testErrorMessageForNoSummaryAndTwoReports() throws Exception {
		final String expected = "[LINT] Violations found. For more details see results in one of the following files: " + xmlFilePath + ", " + htmlFilePath;
		final String actual = checkMojo.generateErrorMessage(Lists.newArrayList("xml", "html"));
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testErrorMessageForSummaryFileAndTwoReports() throws Exception {
		final File summaryFile = new File("summary.txt");
		final String summaryFilePath = summaryFile.getAbsolutePath();
		setField(checkMojo, "summaryOutputFile", summaryFile);
		final String expected = "[LINT] Violations found. For more details see results in one of the following files: " + summaryFilePath + ", " + xmlFilePath;
		final String actual = checkMojo.generateErrorMessage(Lists.newArrayList("summary", "xml"));
		Assert.assertEquals(expected, actual);
	}


	// TODO must use current version (currently not working with snapshot)
	@Test
	public void testDefaultProject() throws Exception {
		String pom = "src/test/resources/bugs/issue-4-missing-pluginParameterEvaluation/pom.xml";
		String goal = "com.lewisd:lint-maven-plugin:check";
		File tempFile = new File(".").createTempFile("log", "log");
		tempFile.deleteOnExit();
		File targetDir = new File("src/test/resources/bugs/issue-4-missing-pluginParameterEvaluation/target");
		targetDir.deleteOnExit();
		// everything except the warnings are going into this log
		String[] args = {goal, "-f", pom, "-l", tempFile.getPath(), "-e"};

		PrintStream oldOut = System.out; // remember old System.out
		try {
			ByteArrayOutputStream stdout = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stdout));

			int result = new MavenCli().doMain(args, ".", null, null);

			if (result != 0) {
				String log = FileUtils.readFileToString(tempFile);
				fail("sth went wrong : " + log);
			}

			assertTrue("expected to have some warnings " + stdout.toString(), stdout.toString().contains("WARN "));
		} finally {
			System.setOut(oldOut); // cleanup
		}
	}
}
