package com.lewisd.maven.lint;

import java.util.Map;
import java.util.Set;

public class RuleInvoker {
	
	private final RuleModelProvider modelProvider;

	public RuleInvoker(final RuleModelProvider modelProvider) {
		this.modelProvider = modelProvider;
	}

	public void invokeRule(final Rule rule, final ResultCollector resultCollector) {
		final Set<String> requiredModels = rule.getRequiredModels();
		final Map<String, Object> models = modelProvider.getModels(requiredModels);
		rule.invoke(models, resultCollector);
	}
	

}
