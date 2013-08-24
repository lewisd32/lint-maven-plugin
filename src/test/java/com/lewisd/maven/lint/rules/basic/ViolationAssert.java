package com.lewisd.maven.lint.rules.basic;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.Violation;
import com.lewisd.maven.lint.rules.AbstractRule;

import static com.lewisd.maven.lint.rules.AssertHelper.haveViolationAtLine;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * User: lars
 */
public class ViolationAssert {

    private final ResultCollector resultCollector;

    public ViolationAssert(ResultCollector resultCollector) {

        this.resultCollector = resultCollector;
    }

    public ColumnAssert line(int line) {
        assertThat(resultCollector.hasViolations()).describedAs("has violations").isTrue();
        assertThat(resultCollector).is(haveViolationAtLine(line));
        return new ColumnAssert(getViolationAtLine(line));
    }

    private Violation getViolationAtLine(int line) {
        for (Violation violation : resultCollector.getViolations()) {
            if (violation.getInputLocation().getLineNumber() == line) {
                return violation;
            }
        }
        return null;
    }

    public static class ColumnAssert {
        private final Violation violation;

        public ColumnAssert(Violation violation) {
            this.violation = violation;
        }

        public RuleAssert column(int column) {
            assertThat(violation.getInputLocation().getColumnNumber()).describedAs("expected violation to be at column " + column).isEqualTo(column);
            return new RuleAssert(violation);
        }

        public MessageAssert violates(Class<? extends AbstractRule> rule) {
            assertThat(violation.getRule()).describedAs("should violate another rule").isInstanceOf(rule);
            return new MessageAssert(violation);
        }
    }

    public static class RuleAssert {
        private final Violation violation;

        public RuleAssert(Violation violation) {
            this.violation = violation;
        }

        public MessageAssert violates(Class<? extends AbstractRule> rule) {
            assertThat(violation.getRule()).describedAs("should violate another rule").isInstanceOf(rule);
            return new MessageAssert(violation);
        }
    }

    public static class MessageAssert {

        private final Violation violation;

        public MessageAssert(Violation violation) {
            this.violation = violation;
        }

        public void withMessage(String message) {
            assertThat(violation.getMessage()).describedAs("message should be different").isEqualTo(message);
        }
    }
}
