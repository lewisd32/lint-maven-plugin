package com.lewisd.maven.lint.rules.opensource;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.ResultCollectorImpl;
import com.lewisd.maven.lint.ViolationSuppressorImpl;
import com.lewisd.maven.lint.rules.basic.ViolationAssert;
import com.lewisd.maven.lint.util.ExpressionEvaluator;
import com.lewisd.maven.lint.util.ModelUtil;
import com.lewisd.maven.lint.util.ReflectionUtil;
import org.apache.maven.model.InputSource;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3ReaderEx;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;


public class MissingCIManagementInformationRuleTest {

    private static final String POM_XML_START = "<project>\n";
    private static final String POM_XML_END = "</project>";

    private ModelUtil modelUtil = new ModelUtil(new ReflectionUtil(), new ExpressionEvaluator());
    private ResultCollector resultCollector = new ResultCollectorImpl(new ViolationSuppressorImpl());

    @Test
    public void shouldFailOnMissingSection() throws IOException, XmlPullParserException {
        String pomXML = POM_XML_START +POM_XML_END;
        MavenProject mavenProject = getMavenProject(pomXML);

        MissingCIManagementInformationRule rule = new MissingCIManagementInformationRule(modelUtil);

        rule.invoke(mavenProject, null, resultCollector);

        ViolationAssert violationAssert = new ViolationAssert(resultCollector);
        violationAssert.violates(MissingCIManagementInformationRule.class);
    }


    protected MavenProject getMavenProject(String pomXML) throws IOException, XmlPullParserException {
        Model mavenModelFromPom = getMavenModelFromPom(pomXML);
        MavenProject mavenProject = new MavenProject(mavenModelFromPom);
        mavenProject.setOriginalModel(mavenModelFromPom);
        return mavenProject;
    }

    private Model getMavenModelFromPom(String pomXML) throws IOException, XmlPullParserException {
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(pomXML.getBytes());
        return new MavenXpp3ReaderEx().read(arrayInputStream, true, new InputSource());
    }
}
