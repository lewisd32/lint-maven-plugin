package com.lewisd.maven.lint.rules;

import com.lewisd.maven.lint.rules.basic.ViolationAssert;
import org.apache.maven.monitor.logging.DefaultLog;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;


public abstract class AbstractRuleIT<T extends AbstractRule> {
    private final static DefaultLog LOG = new DefaultLog(new ConsoleLogger(1, "test"));
    private final static String CONFIG_LOCATION = "config/maven_lint.xml";

    private static GenericApplicationContext applicationContext;

    @Rule
    public RuleInvokerWithPom invokerWithPom = new RuleInvokerWithPom();

    @BeforeClass
    public static void beforeEachTest() {
        applicationContext = new GenericApplicationContext();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ClassPathResource classPathResource = new ClassPathResource(CONFIG_LOCATION, classLoader);
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(applicationContext);
        xmlBeanDefinitionReader.loadBeanDefinitions(classPathResource);

        applicationContext.getBeanFactory().registerSingleton("LOG", LOG);
        applicationContext.refresh();
    }

    public void invokeRule() {
        invokerWithPom.getRuleInvoker().invokeRule(getRule(), invokerWithPom.getResultCollector());
    }

    protected final T getRule(Class<T> ruleClazz) {
        return (T) applicationContext.getBean(ruleClazz);
    }

    protected ViolationAssert violationAssert() {
        return new ViolationAssert(invokerWithPom.getResultCollector());
    }

    public abstract T getRule();
}
