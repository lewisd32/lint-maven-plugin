package com.lewisd.maven.lint.plugin;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

public abstract class AbstractContextMojo extends AbstractMojo {

    /**
     * The Maven Project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * The root spring config location.
     *
     * @parameter expression="${maven-lint.config.location}" default-value="config/maven_lint.xml"
     * @required
     */
    private String configLocation;

    private GenericApplicationContext applicationContext;

    private URLClassLoader classLoader;

    protected void initializeConfig() throws DependencyResolutionRequiredException, IOException {

        @SuppressWarnings("rawtypes")
        List testClasspathElements = project.getTestClasspathElements();
        URL[] testUrls = new URL[testClasspathElements.size()];
        for (int i = 0; i < testClasspathElements.size(); i++) {
            String element = (String) testClasspathElements.get(i);
            testUrls[i] = new File(element).toURI().toURL();
        }
        classLoader = new URLClassLoader(testUrls, Thread.currentThread().getContextClassLoader());

        applicationContext = new GenericApplicationContext();
        ClassPathResource classPathResource = new ClassPathResource(configLocation, classLoader);
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(applicationContext);
        xmlBeanDefinitionReader.loadBeanDefinitions(classPathResource);

        applicationContext.getBeanFactory().registerSingleton("log", getLog());

        applicationContext.refresh();
    }
    public GenericApplicationContext getContext() {
        return applicationContext;
    }

    protected void init() throws MojoExecutionException {
        try {
            initializeConfig();
        } catch (DependencyResolutionRequiredException e) {
            throw new MojoExecutionException("Failed to initialize lint-maven-plugin", e);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to initialize lint-maven-plugin", e);
        }
    }
}
