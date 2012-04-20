package com.lewisd.maven.lint;

import org.apache.maven.model.InputLocation;
import org.apache.maven.model.InputSource;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ViolationSuppressorImplTest {
	
	private static final MavenProject DUMMY_MAVEN_PROJECT = new MavenProject();;
	private static final String DUMMY_MESSAGE = "dummy message";
	private static final Rule RULE = new DummyRule("IDENTIFIER");
	private static final Rule RULE1 = new DummyRule("RuleID1");
	private static final Rule RULE2 = new DummyRule("RuleID2");
	private final ViolationSuppressorImpl violationSuppressor = new ViolationSuppressorImpl();
	private InputSource source;
	
	@Before
	public void before() {
		source = new InputSource();
		source.setLocation("target/test-classes/violationsuppressor/pom.xml");
	}

	@Test
	public void shouldFindSuppressionImmediatelyAfterClosingTag() {
		Assert.assertEquals("<!-- NoLint:Identifier shouldFindSuppressionImmediatelyAfterClosingTag -->",
				violationSuppressor.findSuppressionComment(new Violation(DUMMY_MAVEN_PROJECT, RULE, DUMMY_MESSAGE, new InputLocation(17, 16, source))));
	}

	@Test
	public void shouldFindSuppressionOnNextLine() {
		Assert.assertEquals("<!-- NOLINT:IDENTIFIER shouldFindSuppressionOnNextLine -->",
				violationSuppressor.findSuppressionComment(new Violation(DUMMY_MAVEN_PROJECT, RULE, DUMMY_MESSAGE, new InputLocation(18, 19, source))));
	}
	
	@Test
	public void shouldFindSuppressionAfterBlankLine() {
		Assert.assertEquals("<!-- NOLINT:IDENTIFIER shouldFindSuppressionAfterBlankLine -->",
				violationSuppressor.findSuppressionComment(new Violation(DUMMY_MAVEN_PROJECT, RULE, DUMMY_MESSAGE, new InputLocation(20, 16, source))));
	}
	
	@Test
	public void shouldFindSuppressionInMultiLineComment() {
		Assert.assertEquals("<!-- foo\n      NOLINT:IDENTIFIER\n      shouldFindSuppressionInMultiLineComment\n      bar -->",
				violationSuppressor.findSuppressionComment(new Violation(DUMMY_MAVEN_PROJECT, RULE, DUMMY_MESSAGE, new InputLocation(25, 16, source))));
	}
	
	@Test
	public void shouldFindSuppressionInMultiLineCommentStartingOnNextLine() {
		Assert.assertEquals("<!-- foo\n      NOLINT:IDENTIFIER\n      shouldFindSuppressionInMultiLineCommentStartingOnNextLine\n      bar -->",
				violationSuppressor.findSuppressionComment(new Violation(DUMMY_MAVEN_PROJECT, RULE, DUMMY_MESSAGE, new InputLocation(29, 19, source))));
	}
	
	@Test
	public void shouldFindMultipleSuppressionsInSingleComment() {
		Assert.assertEquals("<!-- NOLINT:RULEID1 NOLINT:RULEID2 shouldFindMultipleSuppressionsInSingleComment -->",
				violationSuppressor.findSuppressionComment(new Violation(DUMMY_MAVEN_PROJECT, RULE1, DUMMY_MESSAGE, new InputLocation(34, 16, source))));
		Assert.assertEquals("<!-- NOLINT:RULEID1 NOLINT:RULEID2 shouldFindMultipleSuppressionsInSingleComment -->",
				violationSuppressor.findSuppressionComment(new Violation(DUMMY_MAVEN_PROJECT, RULE2, DUMMY_MESSAGE, new InputLocation(34, 16, source))));
	}
	
	@Test
	public void shouldFindMultipleSuppressionsInMultiLineComment() {
		Assert.assertEquals("<!-- NOLINT:RuleID1\n      NOLINT:RuleID2\n      shouldFindMultipleSuppressionsInMultiLineComment -->",
				violationSuppressor.findSuppressionComment(new Violation(DUMMY_MAVEN_PROJECT, RULE1, DUMMY_MESSAGE, new InputLocation(37, 16, source))));
		Assert.assertEquals("<!-- NOLINT:RuleID1\n      NOLINT:RuleID2\n      shouldFindMultipleSuppressionsInMultiLineComment -->",
				violationSuppressor.findSuppressionComment(new Violation(DUMMY_MAVEN_PROJECT, RULE2, DUMMY_MESSAGE, new InputLocation(37, 16, source))));
	}
	
	@Test
	public void shouldFindMultipleSuppressionsInMultipleComments() {
		Assert.assertEquals("<!-- NOLINT:RuleId1 -->",
				violationSuppressor.findSuppressionComment(new Violation(DUMMY_MAVEN_PROJECT, RULE1, DUMMY_MESSAGE, new InputLocation(40, 19, source))));
		Assert.assertEquals("<!-- NOLINT:RuleId2 shouldFindMultipleSuppressionsInMultipleComments -->",
				violationSuppressor.findSuppressionComment(new Violation(DUMMY_MAVEN_PROJECT, RULE2, DUMMY_MESSAGE, new InputLocation(40, 19, source))));
	}
	
	@Test
	public void shouldNotFindSuppressionAfterTooManyClosingTags() {
		Assert.assertNull(violationSuppressor.findSuppressionComment(new Violation(DUMMY_MAVEN_PROJECT, RULE, DUMMY_MESSAGE, new InputLocation(44, 16, source))));
	}	
	
	@Test
	public void shouldNotFindSuppressionBeforeViolation() {
		Assert.assertNull(violationSuppressor.findSuppressionComment(new Violation(DUMMY_MAVEN_PROJECT, RULE, DUMMY_MESSAGE, new InputLocation(50, 19, source))));
	}
	
}
