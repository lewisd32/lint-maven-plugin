package com.lewisd.maven.lint.rules.opensource;

import com.lewisd.maven.lint.rules.AbstractRuleTest;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.Test;

import java.io.IOException;

import static com.lewisd.maven.lint.rules.MavenProjectUtil.POM_XML_END;
import static com.lewisd.maven.lint.rules.MavenProjectUtil.POM_XML_START;


public class MissingIssueManagementInformationRuleTest extends AbstractRuleTest<MissingIssueManagementInformationRule> {

    @Override
    public MissingIssueManagementInformationRule getRule() {
        return new MissingIssueManagementInformationRule();
    }

    @Test
    public void shouldFailOnMissingSection() throws IOException, XmlPullParserException {
        String pomXML = POM_XML_START + POM_XML_END;

        invokeRuleWithPom(pomXML);

        violationAssert().violates(MissingIssueManagementInformationRule.class);
    }

    @Test
    public void shouldFailOnMissingSystemAndUrl() throws IOException, XmlPullParserException {
        String pomXML = POM_XML_START +
                "<issueManagement/>\n" +
                POM_XML_END;

        invokeRuleWithPom(pomXML);

        violationAssert().violates(MissingIssueManagementInformationRule.class).withMessage("missing <system/> entry in <issueManagement/> section");
        violationAssert().violates(MissingIssueManagementInformationRule.class).withMessage("missing <url/> entry in <issueManagement/> section");
    }
}
