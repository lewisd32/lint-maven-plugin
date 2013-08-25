package com.lewisd.maven.lint.rules.opensource;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.ResultCollectorImpl;
import com.lewisd.maven.lint.ViolationSuppressorTestImpl;
import com.lewisd.maven.lint.rules.MavenProjectUtil;
import com.lewisd.maven.lint.rules.basic.ViolationAssert;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;

import static com.lewisd.maven.lint.rules.MavenProjectUtil.POM_XML_END;
import static com.lewisd.maven.lint.rules.MavenProjectUtil.POM_XML_START;


public class MissingUrlRuleTest {

    private ResultCollector resultCollector;
    private ViolationAssert violationAssert;
    private MissingUrlRule rule;

    @Before
    public void setUp() {
        resultCollector = new ResultCollectorImpl(new ViolationSuppressorTestImpl());
        violationAssert = new ViolationAssert(resultCollector);
        rule = new MissingUrlRule();
    }

    @Test
    public void shouldFailOnMissingSection() throws Exception {
        String pomXML = POM_XML_START + POM_XML_END;
        MavenProject mavenProject = MavenProjectUtil.getMavenProjectFromXML(pomXML);

        rule.invoke(mavenProject, null, resultCollector);

        violationAssert.violates(MissingUrlRule.class);
    }

    @Test
    public void shouldFailOnMissingEntry() throws Exception {
        String pomXML = POM_XML_START +
                "<url></url>\n" +
                POM_XML_END;
        MavenProject mavenProject = MavenProjectUtil.getMavenProjectFromXML(pomXML);

        rule.invoke(mavenProject, null, resultCollector);

        violationAssert.violates(MissingUrlRule.class).withMessage("missing <url/> information");
    }
}
