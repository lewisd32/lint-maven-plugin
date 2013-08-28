package com.lewisd.maven.lint.rules;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.ResultCollectorImpl;
import com.lewisd.maven.lint.Violation;
import com.lewisd.maven.lint.ViolationSuppressorTestImpl;
import com.lewisd.maven.lint.rules.basic.ViolationAssert;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.Before;

import java.io.IOException;
import java.util.List;

public abstract class AbstractRuleTest<T extends AbstractRule> {
    private ResultCollector resultCollector;
    private ViolationAssert violationAssert;

    @Before
    public void setUp() {
        resultCollector = new ResultCollectorImpl(new ViolationSuppressorTestImpl());
        violationAssert = new ViolationAssert(resultCollector);
    }

    protected void invokeRuleWithPom(String pomXML) throws IOException, XmlPullParserException {
        MavenProject mavenProject = MavenProjectUtil.getMavenProjectFromXML(pomXML);

        getRule().invoke(mavenProject, null, resultCollector);
    }

    protected ViolationAssert violationAssert(){
        return violationAssert;
    }

    protected List<Violation> getViolations(){
        return resultCollector.getViolations();
    }

    public abstract T getRule();

}
