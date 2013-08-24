package com.lewisd.maven.lint.rules.basic;

import com.lewisd.maven.lint.rules.AbstractRule;
import com.lewisd.maven.lint.rules.RuleInvokerWithPomRule;
import org.apache.maven.monitor.logging.DefaultLog;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;


public abstract class AbstractRuleTest<T extends AbstractRule> {
    private final static DefaultLog LOG = new DefaultLog(new ConsoleLogger(1, "test"));
    private final static String CONFIG_LOCATION = "config/maven_lint.xml";

    private static GenericApplicationContext applicationContext;

    @Rule
    public RuleInvokerWithPomRule invokerWithPom = new RuleInvokerWithPomRule();

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

    protected final T getRule(Class<T> ruleClazz) {
        return (T) applicationContext.getBean(ruleClazz);
    }

    protected ViolationAssert violationAssert() {
        return new ViolationAssert(invokerWithPom.getResultCollector());
    }

    public abstract T getRule();
}
