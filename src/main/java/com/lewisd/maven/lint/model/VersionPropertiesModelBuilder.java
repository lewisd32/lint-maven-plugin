package com.lewisd.maven.lint.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.maven.project.MavenProject;
import org.springframework.beans.factory.annotation.Autowired;

import com.lewisd.maven.lint.ModelFactory;
import com.lewisd.maven.lint.util.ModelUtil;

public class VersionPropertiesModelBuilder extends AbstractModelBuilder {

	private final ModelUtil modelUtil;

	@Autowired
	public VersionPropertiesModelBuilder(final ModelUtil modelUtil, final ModelFactory modelFactory) {
		super(modelFactory);
		this.modelUtil = modelUtil;
	}
	
	public Set<String> getRequiredModels() {
		return Collections.singleton(MAVEN_PROJECT);
	}

	public Object buildModel(final Map<String, Object> models) {
		final Map<Object, VersionProperty> versionPropertyByObject = new HashMap<Object, VersionProperty>();
		
		final MavenProject mavenProject = (MavenProject) models.get(MAVEN_PROJECT);
		
		final Collection<Object> objectsToCheck = modelUtil.findGAVObjects(mavenProject);
		
		for (final Object object: objectsToCheck) {
			final String version = modelUtil.getVersion(object);

			if (version != null && version.contains("${")) {
				versionPropertyByObject.put(object, new VersionProperty(version));
			}
		}

		return versionPropertyByObject;
	}

	public String getModelId() {
		return "versionProperties";
	}

}
