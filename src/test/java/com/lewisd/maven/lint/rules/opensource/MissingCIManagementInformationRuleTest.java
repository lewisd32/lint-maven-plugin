package com.lewisd.maven.lint.rules.opensource;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.ResultCollectorImpl;
import com.lewisd.maven.lint.ViolationSuppressorImpl;
import com.lewisd.maven.lint.rules.MavenProjectUtil;
import com.lewisd.maven.lint.rules.basic.ViolationAssert;
import com.lewisd.maven.lint.util.ExpressionEvaluator;
import com.lewisd.maven.lint.util.ModelUtil;
import com.lewisd.maven.lint.util.ReflectionUtil;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.lewisd.maven.lint.rules.MavenProjectUtil.POM_XML_END;
import static com.lewisd.maven.lint.rules.MavenProjectUtil.POM_XML_START;


public class MissingCIManagementInformationRuleTest {

    private ModelUtil modelUtil = new ModelUtil(new ReflectionUtil(), new ExpressionEvaluator());
    private ResultCollector resultCollector;
    private ViolationAssert violationAssert;
    private MissingCIManagementInformationRule rule;

    @Before
    public void setUp() {
        resultCollector = new ResultCollectorImpl(new ViolationSuppressorImpl());
        violationAssert = new ViolationAssert(resultCollector);
        rule = new MissingCIManagementInformationRule(modelUtil);
    }

    @Test
    public void shouldFailOnMissingSection() throws IOException, XmlPullParserException {
        String pomXML = POM_XML_START + POM_XML_END;
        MavenProject mavenProject = MavenProjectUtil.getMavenProjectFromXML(pomXML);

        rule.invoke(mavenProject, null, resultCollector);

        violationAssert.violates(MissingCIManagementInformationRule.class);
    }

    @Test
    public void shouldFailOnMissingSystem() throws IOException, XmlPullParserException {
        String pomXML = POM_XML_START +
                "<ciManagement>\n" +
                "</ciManagement>\n" +
                POM_XML_END;
        MavenProject mavenProject = MavenProjectUtil.getMavenProjectFromXML(pomXML);

        rule.invoke(mavenProject, null, resultCollector);

        violationAssert.violates(MissingCIManagementInformationRule.class).withMessage("missing <system/> entry in <ciManagement/> section");
    }

    @Test
    public void shouldFailOnMissingUrl() throws IOException, XmlPullParserException {
        String pomXML = POM_XML_START +
                "<ciManagement>\n" +
                "<system>hudson</system>\n" +
                "</ciManagement>\n" +
                POM_XML_END;
        MavenProject mavenProject = MavenProjectUtil.getMavenProjectFromXML(pomXML);

        rule.invoke(mavenProject, null, resultCollector);

        violationAssert.violates(MissingCIManagementInformationRule.class).withMessage("missing <url/> entry in <ciManagement/> section");
    }
}
