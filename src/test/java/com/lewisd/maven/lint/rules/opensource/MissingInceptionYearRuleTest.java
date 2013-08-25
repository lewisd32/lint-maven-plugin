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
import static org.fest.assertions.api.Assertions.assertThat;


public class MissingInceptionYearRuleTest {

    private ResultCollector resultCollector;
    private ViolationAssert violationAssert;
    private MissingInceptionYearRule rule;

    @Before
    public void setUp() {
        resultCollector = new ResultCollectorImpl(new ViolationSuppressorTestImpl());
        violationAssert = new ViolationAssert(resultCollector);
        rule = new MissingInceptionYearRule();
    }

    @Test
    public void shouldFailOnMissing() throws Exception {
        String pomXML = POM_XML_START + POM_XML_END;
        MavenProject mavenProject = MavenProjectUtil.getMavenProjectFromXML(pomXML);

        rule.invoke(mavenProject, null, resultCollector);

        violationAssert.violates(MissingInceptionYearRule.class);
    }

    @Test
    public void shouldFailOnWrongEntry() throws Exception {
        String pomXML = POM_XML_START +
                "<inceptionYear>201</inceptionYear>\n" +
                POM_XML_END;
        MavenProject mavenProject = MavenProjectUtil.getMavenProjectFromXML(pomXML);

        rule.invoke(mavenProject, null, resultCollector);

        violationAssert.violates(MissingInceptionYearRule.class).withMessage("format of <inceptionYear/> information is wrong, only 4 digits allowed");
    }

    @Test
    public void shouldBeOk() throws Exception {
        String pomXML = POM_XML_START +
                "<inceptionYear>2013</inceptionYear>\n" +
                POM_XML_END;
        MavenProject mavenProject = MavenProjectUtil.getMavenProjectFromXML(pomXML);

        rule.invoke(mavenProject, null, resultCollector);

        assertThat(resultCollector.getViolations()).isEmpty();
    }
}
