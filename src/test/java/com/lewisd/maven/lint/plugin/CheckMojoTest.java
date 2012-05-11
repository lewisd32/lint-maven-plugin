package com.lewisd.maven.lint.plugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

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
	
	
}
