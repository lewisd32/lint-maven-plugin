package com.lewisd.maven.lint.rules.opensource;

import com.google.common.collect.Lists;
import com.lewisd.maven.lint.Violation;
import com.lewisd.maven.lint.rules.AbstractRule;
import com.lewisd.maven.lint.rules.POM;
import com.lewisd.maven.lint.rules.RuleInvokerWithPom;
import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.PluginParameterExpressionEvaluator;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class OpensourceRulesIT {

    private final static DefaultLog LOG = new DefaultLog(new ConsoleLogger(1, "test"));
    private final static String CONFIG_LOCATION = "config/maven_lint.xml";

    @Rule
    public RuleInvokerWithPom invokerWithPom = new RuleInvokerWithPom();

    @BeforeClass
    public static void beforeAllTest() {
        GenericApplicationContext applicationContext = new GenericApplicationContext();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ClassPathResource classPathResource = new ClassPathResource(CONFIG_LOCATION, classLoader);
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(
            applicationContext);
        xmlBeanDefinitionReader.loadBeanDefinitions(classPathResource);

        applicationContext.getBeanFactory().registerSingleton("LOG", LOG);
        applicationContext.getBeanFactory().registerResolvableDependency(PluginParameterExpressionEvaluator.class, mock(PluginParameterExpressionEvaluator.class));
        applicationContext.refresh();
    }

    public void invokeRule(AbstractRule rule) {
        invokerWithPom.getRuleInvoker().invokeRule(rule, invokerWithPom.getResultCollector());
    }

    @Test
    @POM("src/test/resources/it-opensource-violations.xml")
    public void test() throws IOException, XmlPullParserException {

        List<? extends AbstractRule> rules = Lists.newArrayList(new MissingCIManagementInformationRule(), new MissingIssueManagementInformationRule(), new MissingUrlRule(), new MissingInceptionYearRule(), new MissingLicenseRule(), new MissingDeveloperInformationRule());

        for (AbstractRule rule : rules) {
            invokeRule(rule);
        }

        assertThat(getViolations()).isEmpty();
    }

    private List<Violation> getViolations() {
        return invokerWithPom.getResultCollector().getViolations();
    }
}
