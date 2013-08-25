package com.lewisd.maven.lint.rules.basic;

import com.google.common.collect.Lists;
import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.Violation;
import com.lewisd.maven.lint.rules.AbstractRule;

import java.util.List;

import static com.lewisd.maven.lint.rules.AssertHelper.haveViolationAtLine;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;

/**
 * User: lars
 */
public class ViolationAssert {

    private final ResultCollector resultCollector;

    public ViolationAssert(ResultCollector resultCollector) {
        this.resultCollector = resultCollector;
    }

    public MessageAssert violates(Class<? extends AbstractRule> rule) {
        final List<Violation> violations = resultCollector.getViolations();
        List<Violation> matchedViolations = Lists.newArrayList();
        for(Violation violation : violations){
            if ( violation.getRule().getClass() == rule){
                matchedViolations.add(violation);
            }
        }

        if ( matchedViolations.isEmpty()){
        fail("expected this rule " + rule + " violated");return null;}else{return new MessageAssert(matchedViolations);}

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

    private static MessageAssert violates(Violation violation, Class<? extends AbstractRule> rule) {
        assertThat(violation.getRule()).describedAs("should violate another rule").isInstanceOf(rule);
        return new MessageAssert(violation);
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
            return ViolationAssert.violates(violation, rule);
        }
    }

    public static class RuleAssert {
        private final Violation violation;

        public RuleAssert(Violation violation) {
            this.violation = violation;
        }

        public MessageAssert violates(Class<? extends AbstractRule> rule) {
            return ViolationAssert.violates(violation, rule);
        }
    }

    public static class MessageAssert {

        private final List<Violation> violations;

        public MessageAssert(Violation violation) {
            this.violations = Lists.newArrayList(violation);
        }

        public MessageAssert(List<Violation> violations) {
            this.violations = violations;
        }

        public void withMessage(String message) {
            List<String> messages = Lists.newArrayList();
            for( Violation violation : violations){
                if(message.equals(violation.getMessage())){
                    return; // ok
                }else{
                    messages.add(violation.getMessage());
                }
            }
            assertThat(messages).describedAs("message should be different").contains(message);
        }
    }
}
