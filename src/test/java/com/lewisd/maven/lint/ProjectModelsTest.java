package com.lewisd.maven.lint;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.apache.maven.project.MavenProject;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class ProjectModelsTest {
	
	private static final String MAVEN_PROJECT = "mavenProject";
	private static final String SIMPLE_MODEL = "simpleModel";
	private static final String DEPENDENT_MODEL = "dependentModel";

	private final Mockery mockery = new Mockery();
	
	private final MavenProject mavenProject = new MavenProject();
	private final Map<String, ModelBuilder> modelBuilders = new HashMap<String, ModelBuilder>();
	private final ProjectModels projectModels = new ProjectModels(mavenProject, modelBuilders);
	
	@Before
	public void before() {
		
	}

	@Test
	public void shouldProvideMavenProject() {
		Set<String> requiredModels = new HashSet<String>();
		requiredModels.add(MAVEN_PROJECT);

		Map<String,Object> models = projectModels.getModels(requiredModels);
		
		Assert.assertEquals(mavenProject, models.get(MAVEN_PROJECT));
	}
	
	@Test
	public void shouldProvideRequiredModelsWhenNoneHaveBeenCreated() {
		final SimpleModel simpleModel = new SimpleModel();
		final ModelBuilder simpleModelBuilder = mockery.mock(ModelBuilder.class);
		
		mockery.checking(new Expectations()
		{{
			allowing(simpleModelBuilder).getRequiredModels();
			will(returnValue(Collections.singleton(MAVEN_PROJECT)));
			
			one(simpleModelBuilder).buildModel(with(new ModelMapMatcher(MAVEN_PROJECT, MavenProject.class)));
			will(returnValue(simpleModel));
		}});
		
		modelBuilders.put(SIMPLE_MODEL, simpleModelBuilder);
		
		Set<String> requiredModels = new HashSet<String>();
		requiredModels.add(SIMPLE_MODEL);

		Map<String,Object> models = projectModels.getModels(requiredModels);
		
		Assert.assertEquals(simpleModel, models.get(SIMPLE_MODEL));
	}

	@Test
	public void shouldProvideRequiredModelsWhenAlreadyCreated() {
		final SimpleModel simpleModel = new SimpleModel();
		final ModelBuilder simpleModelBuilder = mockery.mock(ModelBuilder.class);
		
		mockery.checking(new Expectations()
		{{
			allowing(simpleModelBuilder).getRequiredModels();
			will(returnValue(Collections.singleton(MAVEN_PROJECT)));

			one(simpleModelBuilder).buildModel(with(new ModelMapMatcher(MAVEN_PROJECT, MavenProject.class)));
			will(returnValue(simpleModel));
		}});
		
		modelBuilders.put(SIMPLE_MODEL, simpleModelBuilder);
		
		Set<String> requiredModels = new HashSet<String>();
		requiredModels.add(SIMPLE_MODEL);

		// get models once, creating them
		projectModels.getModels(requiredModels);
		
		// get models again
		Map<String,Object> models = projectModels.getModels(requiredModels);
		
		Assert.assertEquals(simpleModel, models.get(SIMPLE_MODEL));
	}

	@Test
	public void shouldProvideDependentRequiredModels() {
		final SimpleModel simpleModel = new SimpleModel();
		final DependentModel dependentModel = new DependentModel();
		final ModelBuilder simpleModelBuilder = mockery.mock(ModelBuilder.class, "simpleModelBuilder");
		final ModelBuilder dependentModelBuilder = mockery.mock(ModelBuilder.class, "dependentModelBuilder");
		
		mockery.checking(new Expectations()
		{{
			allowing(simpleModelBuilder).getRequiredModels();
			will(returnValue(Collections.singleton(MAVEN_PROJECT)));
			
			allowing(dependentModelBuilder).getRequiredModels();
			will(returnValue(Collections.singleton(SIMPLE_MODEL)));
			
			one(simpleModelBuilder).buildModel(with(new ModelMapMatcher(MAVEN_PROJECT, MavenProject.class)));
			will(returnValue(simpleModel));

			one(dependentModelBuilder).buildModel(with(new ModelMapMatcher(SIMPLE_MODEL, SimpleModel.class)));
			will(returnValue(dependentModel));
		}});
		
		modelBuilders.put(SIMPLE_MODEL, simpleModelBuilder);
		modelBuilders.put(DEPENDENT_MODEL, dependentModelBuilder);
		
		Set<String> requiredModels = new HashSet<String>();
		requiredModels.add(DEPENDENT_MODEL);

		Map<String,Object> models = projectModels.getModels(requiredModels);
		
		Assert.assertEquals(dependentModel, models.get(DEPENDENT_MODEL));
	}

	class SimpleModel {
	}
	
	class DependentModel {
	}
	
	@SuppressWarnings("rawtypes")
	class ModelMapMatcher extends BaseMatcher<Map<String,Object>> {
		
		private final String key;
		private final Class klass;

		public ModelMapMatcher(String key, Class klass) {
			this.key = key;
			this.klass = klass;
		}

		@SuppressWarnings("unchecked")
		public boolean matches(Object arg) {
			Map<String,Object> map = (Map<String, Object>) arg;
			Object object = map.get(key);
			return (object != null && klass.isAssignableFrom(object.getClass()));
		}

		public void describeTo(Description description) {
			description.appendText("Map containing a '" + key + "' key with value of type " + klass);
		}
		
	}
}
