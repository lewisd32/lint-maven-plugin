package com.lewisd.maven.lint.rules.opensource;

import com.lewisd.maven.lint.rules.AbstractRuleTest;
import org.junit.Test;

import static com.lewisd.maven.lint.rules.MavenProjectUtil.POM_XML_END;
import static com.lewisd.maven.lint.rules.MavenProjectUtil.POM_XML_START;


public class MissingUrlRuleTest extends AbstractRuleTest<MissingUrlRule> {

    @Override
    public MissingUrlRule getRule() {
        return new MissingUrlRule();
    }

    @Test
    public void shouldFailOnMissingSection() throws Exception {
        String pomXML = POM_XML_START + POM_XML_END;

        invokeRuleWithPom(pomXML);

        violationAssert().violates(MissingUrlRule.class);
    }

    @Test
    public void shouldFailOnMissingEntry() throws Exception {
        String pomXML = POM_XML_START +
                "<url></url>\n" +
                POM_XML_END;

        invokeRuleWithPom(pomXML);

        violationAssert().violates(MissingUrlRule.class).withMessage("missing <url/> information");
    }
}
