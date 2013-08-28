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

    protected void initializeConfig() throws DependencyResolutionRequiredException, IOException {

        List<String> testClasspathElements = project.getTestClasspathElements();
        URL[] testUrls = new URL[testClasspathElements.size()];
        for (int i = 0; i < testClasspathElements.size(); i++) {
            String element = testClasspathElements.get(i);
            testUrls[i] = new File(element).toURI().toURL();
        }

        URLClassLoader classLoader = new URLClassLoader(testUrls, Thread.currentThread().getContextClassLoader());
        ClassPathResource classPathResource = new ClassPathResource(configLocation, classLoader);

        applicationContext = new GenericApplicationContext();
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(applicationContext);
        xmlBeanDefinitionReader.loadBeanDefinitions(classPathResource);

        applicationContext.getBeanFactory().registerSingleton("log", getLog());
        applicationContext.refresh();
    }

    protected GenericApplicationContext getContext() {
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

    protected MavenProject getProject() {
        return project;
    }
}
