package com.lewisd.maven.lint;

import java.util.Map;
import java.util.Set;

public interface Rule {

	Set<String> getRequiredModels();
	
	void invoke(Map<String,Object> models, ResultCollector resultCollector);
}
