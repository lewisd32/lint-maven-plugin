package com.lewisd.maven.lint;


public interface ViolationSuppressor {

	boolean isSuppressed(Violation violation);

}
