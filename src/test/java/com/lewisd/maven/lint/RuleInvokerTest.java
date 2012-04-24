package com.lewisd.maven.lint;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.maven.project.MavenProject;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class RuleInvokerTest {
	
	private final Mockery mockery = new Mockery();

	@Test
	public void shouldInvokeRuleWithRequiredModels() {
		final MavenProject mavenProject = new MavenProject();
		final ModelFactory modelFactory = mockery.mock(ModelFactory.class);
		final ResultCollector resultCollector = mockery.mock(ResultCollector.class);
		final Rule rule = mockery.mock(Rule.class);
		
		final Set<String> requiredModels = new HashSet<String>();
		final Map<String,Object> models = new HashMap<String,Object>();
		
		RuleInvoker ruleInvoker = new RuleInvoker(mavenProject, modelFactory);
		
		mockery.checking(new Expectations()
		{{
			one(rule).getRequiredModels();
			will(returnValue(requiredModels));
			
			one(modelFactory).getModels(mavenProject, requiredModels);
			will(returnValue(models));
			
			one(rule).invoke(mavenProject, models, resultCollector);
		}});

		
		ruleInvoker.invokeRule(rule, resultCollector);
	}

}
