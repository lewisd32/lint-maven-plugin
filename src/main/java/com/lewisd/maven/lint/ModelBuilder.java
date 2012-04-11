package com.lewisd.maven.lint;

import java.util.Map;
import java.util.Set;

public interface ModelBuilder {

	Set<String> getRequiredModels();
	
	Object buildModel(Map<String,Object> models);

	String getModelId();
	
}
