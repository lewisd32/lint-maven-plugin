package com.lewisd.maven.lint.rules.opensource;

import com.lewisd.maven.lint.rules.AbstractRuleTest;
import org.junit.Test;

import static com.lewisd.maven.lint.rules.MavenProjectUtil.POM_XML_END;
import static com.lewisd.maven.lint.rules.MavenProjectUtil.POM_XML_START;
import static org.fest.assertions.api.Assertions.assertThat;


public class MissingInceptionYearRuleTest  extends AbstractRuleTest<MissingInceptionYearRule> {

    public MissingInceptionYearRule getRule() {
        return new MissingInceptionYearRule();
    }

    @Test
    public void shouldFailOnMissing() throws Exception {
        String pomXML = POM_XML_START + POM_XML_END;

        invokeRuleWithPom(pomXML);

        violationAssert().violates(MissingInceptionYearRule.class);
    }

    @Test
    public void shouldFailOnWrongEntry() throws Exception {
        String pomXML = POM_XML_START +
                "<inceptionYear>201</inceptionYear>\n" +
                POM_XML_END;

        invokeRuleWithPom(pomXML);

        violationAssert().violates(MissingInceptionYearRule.class).withMessage("format of <inceptionYear/> information is wrong, only 4 digits allowed");
    }

    @Test
    public void shouldBeOk() throws Exception {
        String pomXML = POM_XML_START +
                "<inceptionYear>2013</inceptionYear>\n" +
                POM_XML_END;

        invokeRuleWithPom(pomXML);

        assertThat(getViolations()).isEmpty();
    }
}
