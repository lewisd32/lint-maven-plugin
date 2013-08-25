package com.lewisd.maven.lint;

public class ViolationSuppressorTestImpl implements ViolationSuppressor {
    @Override
    public boolean isSuppressed(Violation violation) {
        return false;
    }
}
