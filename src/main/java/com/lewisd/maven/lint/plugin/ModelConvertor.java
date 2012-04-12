package com.lewisd.maven.lint.plugin;

import org.apache.maven.project.MavenProject;

public interface ModelConvertor {

	MavenProject convertProject(MavenProject oldProject);

}
