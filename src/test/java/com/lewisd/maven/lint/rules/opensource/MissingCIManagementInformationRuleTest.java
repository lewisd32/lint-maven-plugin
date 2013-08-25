package com.lewisd.maven.lint.rules.opensource;

import com.lewisd.maven.lint.rules.AbstractRuleTest;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.Test;

import java.io.IOException;

import static com.lewisd.maven.lint.rules.MavenProjectUtil.POM_XML_END;
import static com.lewisd.maven.lint.rules.MavenProjectUtil.POM_XML_START;


public class MissingCIManagementInformationRuleTest  extends AbstractRuleTest<MissingCIManagementInformationRule> {

    @Override
    public MissingCIManagementInformationRule getRule() {
        return new MissingCIManagementInformationRule();
    }

    @Test
    public void shouldFailOnMissingSection() throws IOException, XmlPullParserException {
        String pomXML = POM_XML_START + POM_XML_END;

        invokeRuleWithPom(pomXML);

        violationAssert().violates(MissingCIManagementInformationRule.class);
    }

    @Test
    public void shouldFailOnMissingSystem() throws IOException, XmlPullParserException {
        String pomXML = POM_XML_START +
                "<ciManagement>\n" +
                "</ciManagement>\n" +
                POM_XML_END;

        invokeRuleWithPom(pomXML);

        violationAssert().violates(MissingCIManagementInformationRule.class).withMessage("missing <system/> entry in <ciManagement/> section");
    }

    @Test
    public void shouldFailOnMissingUrl() throws IOException, XmlPullParserException {
        String pomXML = POM_XML_START +
                "<ciManagement>\n" +
                "<system>hudson</system>\n" +
                "</ciManagement>\n" +
                POM_XML_END;

        invokeRuleWithPom(pomXML);

        violationAssert().violates(MissingCIManagementInformationRule.class).withMessage("missing <url/> entry in <ciManagement/> section");
    }
}
