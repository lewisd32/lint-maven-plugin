package com.lewisd.maven.lint.model;

import org.apache.maven.project.MavenProject;

public class ObjectWithPath<T> {

    private final T object;
    private final String path;
    private final MavenProject project;

    public ObjectWithPath(final T object, final MavenProject project, final String path) {
        if (path == null) {
            throw new IllegalArgumentException("path cannot be null");
        }
        this.object = object;
        this.project = project;
        this.path = path;
    }

    public T getObject() {
        return object;
    }

    public MavenProject getProject() {
        return project;
    }

    public String getPath() {
        return path;
    }

}
