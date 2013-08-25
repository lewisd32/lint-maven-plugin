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
import org.junit.Test;

import java.io.IOException;

import static com.lewisd.maven.lint.rules.MavenProjectUtil.POM_XML_END;
import static com.lewisd.maven.lint.rules.MavenProjectUtil.POM_XML_START;


public class MissingCIManagementInformationRuleTest {

    private ModelUtil modelUtil = new ModelUtil(new ReflectionUtil(), new ExpressionEvaluator());
    private ResultCollector resultCollector = new ResultCollectorImpl(new ViolationSuppressorImpl());

    @Test
    public void shouldFailOnMissingSection() throws IOException, XmlPullParserException {
        String pomXML = POM_XML_START + POM_XML_END;
        MavenProject mavenProject = MavenProjectUtil.getMavenProjectFromXML(pomXML);

        MissingCIManagementInformationRule rule = new MissingCIManagementInformationRule(modelUtil);

        rule.invoke(mavenProject, null, resultCollector);

        ViolationAssert violationAssert = new ViolationAssert(resultCollector);
        violationAssert.violates(MissingCIManagementInformationRule.class);
    }
}
