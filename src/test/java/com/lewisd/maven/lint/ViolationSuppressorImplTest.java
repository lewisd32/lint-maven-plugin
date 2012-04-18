package com.lewisd.maven.lint;

import static org.junit.Assert.fail;

import org.apache.maven.model.InputLocation;
import org.apache.maven.model.InputSource;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class ViolationSuppressorImplTest {
	
	private static final MavenProject DUMMY_MAVEN_PROJECT = new MavenProject();;
	private static final String DUMMY_MESSAGE = "dummy message";
	private static final Rule RULE = new DummyRule("IDENTIFIER");
	private static final Rule RULE1 = new DummyRule("ID1");
	private static final Rule RULE2 = new DummyRule("ID2");
	private final ViolationSuppressorImpl violationSuppressor = new ViolationSuppressorImpl();
	private InputSource source;
	
	@Before
	public void before() {
		source = new InputSource();
		source.setLocation("target/test-classes/violationsuppressor/pom.xml");
	}

	@Test
	public void shouldFindSuppressionImmediatelyAfterClosingTag() {
		Assert.assertTrue(violationSuppressor.isSuppressed(new Violation(DUMMY_MAVEN_PROJECT, RULE, DUMMY_MESSAGE, new InputLocation(17, 16, source))));
	}

	@Test
	public void shouldFindSuppressionOnNextLine() {
		Assert.assertTrue(violationSuppressor.isSuppressed(new Violation(DUMMY_MAVEN_PROJECT, RULE, DUMMY_MESSAGE, new InputLocation(18, 19, source))));
	}
	
	@Ignore
	@Test
	public void shouldFindSuppressionAfterBlankLine() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void shouldFindSuppressionInMultiLineComment() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void shouldFindSuppressionInMultiLineCommentStartingOnNextLine() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void shouldFindMultipleSuppressionsInSingleComment() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void shouldFindMultipleSuppressionsInMultiLineComment() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void shouldFindMultipleSuppressionsInMultipleComments() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void shouldNotFindSuppressionAfterTooManyClosingTags() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void shouldNotFindSuppressionBeforeViolation() {
		fail("Not yet implemented");
	}
	
}
