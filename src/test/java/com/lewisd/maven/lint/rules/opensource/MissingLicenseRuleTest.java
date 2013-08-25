package com.lewisd.maven.lint.rules.opensource;

import com.lewisd.maven.lint.rules.AbstractRuleTest;
import org.junit.Test;

import static com.lewisd.maven.lint.rules.MavenProjectUtil.POM_XML_END;
import static com.lewisd.maven.lint.rules.MavenProjectUtil.POM_XML_START;
import static org.fest.assertions.api.Assertions.assertThat;


public class MissingLicenseRuleTest extends AbstractRuleTest<MissingLicenseRule> {

    @Override
    public MissingLicenseRule getRule() {
        return new MissingLicenseRule();
    }

    @Test
    public void shouldFailOnMissingSection() throws Exception {
        String pomXML = POM_XML_START + POM_XML_END;

        invokeRuleWithPom(pomXML);

        violationAssert().violates(MissingLicenseRule.class);
    }

    @Test
    public void shouldFailOnMissingEntry() throws Exception {
        String pomXML = POM_XML_START +
                "<licenses></licenses>\n" +
                POM_XML_END;

        invokeRuleWithPom(pomXML);

        violationAssert().violates(MissingLicenseRule.class).withMessage("missing <licenses/> information");
    }
    @Test
    public void shouldFailWithMissingNameAndUrl() throws Exception {
        String pomXML = POM_XML_START +
                "<licenses>\n" +
                "   <license>\n" +
                "   </license>\n" +
                "   <license>\n" +
                "   </license>\n" +
                "</licenses>\n" +
                POM_XML_END;

        invokeRuleWithPom(pomXML);

        violationAssert().violates(MissingLicenseRule.class).withMessage("missing <name> in <license/> information");
        violationAssert().violates(MissingLicenseRule.class).withMessage("missing <url> in <license/> information");

        assertThat(getViolations()).hasSize(4);
    }
}
