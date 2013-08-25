package com.lewisd.maven.lint.rules.opensource;

import com.lewisd.maven.lint.rules.AbstractRuleTest;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.Test;

import java.io.IOException;

import static com.lewisd.maven.lint.rules.MavenProjectUtil.POM_XML_END;
import static com.lewisd.maven.lint.rules.MavenProjectUtil.POM_XML_START;


public class MissingDeveloperInformationRuleTest extends AbstractRuleTest<MissingDeveloperInformationRule> {

    @Override
    public MissingDeveloperInformationRule getRule() {
        return new MissingDeveloperInformationRule();
    }

    @Test
    public void shouldFailOnMissingSection() throws IOException, XmlPullParserException {
        String pomXML = POM_XML_START + POM_XML_END;

        invokeRuleWithPom(pomXML);

        violationAssert().violates(MissingDeveloperInformationRule.class);
    }

    @Test
    public void shouldFailOnMissingNameAndId() throws IOException, XmlPullParserException {
        String pomXML = POM_XML_START +
                "<developers>\n" +
                "   <developer>\n" +
                "   </developer>\n" +
                "</developers>\n" +
                POM_XML_END;

        invokeRuleWithPom(pomXML);

        violationAssert().violates(MissingDeveloperInformationRule.class).withMessage("missing <name/> entry in <developer/> section");
        violationAssert().violates(MissingDeveloperInformationRule.class).withMessage("missing <id/> entry in <developer/> section");
    }
    @Test
    public void shouldFailOnInvalidEmail() throws IOException, XmlPullParserException {
        String pomXML = POM_XML_START +
                "<developers>\n" +
                "    <developer>\n" +
                "      <email>test@test.de</email>\n" +
                "    </developer>\n" +
                "    <developer>\n" +
                "      <email>test.de</email>\n" +
                "    </developer>\n" +
                "</developers>\n" +
                POM_XML_END;

        invokeRuleWithPom(pomXML);

        violationAssert().violates(MissingDeveloperInformationRule.class).withMessage("not valid <email/> entry in <developer/> section");
    }
}
