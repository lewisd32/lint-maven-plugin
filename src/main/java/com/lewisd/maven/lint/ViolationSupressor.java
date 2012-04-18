package com.lewisd.maven.lint;

import org.apache.maven.model.InputLocation;

public interface ViolationSupressor {

	boolean isSuppressed(Rule rule, InputLocation inputLocation);

}
