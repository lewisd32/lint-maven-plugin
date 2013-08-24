package com.lewisd.maven.lint.rules;

import com.lewisd.maven.lint.CachingModelFactory;
import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.ResultCollectorImpl;
import com.lewisd.maven.lint.RuleInvoker;
import com.lewisd.maven.lint.ViolationSuppressorImpl;
import com.lewisd.maven.lint.rules.basic.ExecutionIdRule;
import org.apache.maven.model.InputSource;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3ReaderEx;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.FileInputStream;
import java.io.IOException;

public class RuleInvokerWithPomRule implements TestRule {

    private String filename;
    private RuleInvoker ruleInvoker;
    private ResultCollector resultCollector = new ResultCollectorImpl(new ViolationSuppressorImpl());

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                before(description);
                base.evaluate();
            }
        };
    }

    public ResultCollector getResultCollector() {
        return resultCollector;
    }

    private void before(Description description) throws IOException, XmlPullParserException {
        initFilename(description);
        ruleInvoker = getRuleInvokerWithPom(filename);
    }

    protected RuleInvoker getRuleInvokerWithPom(String filename) throws IOException, XmlPullParserException {
        Model mavenModelFromPom = getMavenModelFromPom(filename);
        MavenProject mavenProject = new MavenProject(mavenModelFromPom);
        mavenProject.setOriginalModel(mavenModelFromPom);
        return new RuleInvoker(mavenProject, new CachingModelFactory());
    }

    private Model getMavenModelFromPom(String filename) throws IOException, XmlPullParserException {
        InputSource inputSource = new InputSource();
        inputSource.setLocation(filename);
        return new MavenXpp3ReaderEx().read(new FileInputStream(filename), true, inputSource);
    }

    private void initFilename(Description description) {
        final POM pom = description.getAnnotation(POM.class);
        if (null == pom) {
            throw new IllegalStateException("need to specify a pom to test on ( use " + POM.class + " annotation)");
        } else {
            filename = pom.value();
        }
    }

    public void invoke(ExecutionIdRule rule) {
        ruleInvoker.invokeRule(rule,resultCollector);
    }
}
