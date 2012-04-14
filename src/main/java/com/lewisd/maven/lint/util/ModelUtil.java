package com.lewisd.maven.lint.util;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.project.MavenProject;
import org.springframework.beans.factory.annotation.Autowired;

public class ModelUtil {

	private final ReflectionUtil reflectionUtil;
	private final ExpressionEvaluator expressionEvaluator;

	@Autowired
	public ModelUtil(ReflectionUtil reflectionUtil, ExpressionEvaluator expressionEvaluator) {
		this.reflectionUtil = reflectionUtil;
		this.expressionEvaluator = expressionEvaluator;
	}

	public InputLocation getLocation(Object modelObject, Object key) {
		String methodName = "getLocation";
		return (InputLocation) reflectionUtil.callMethod(modelObject, methodName, new Class[] {Object.class}, new Object[] {key});
	}
	
	public String getVersion(Object modelObject) {
		String methodName = "getVersion";
		return (String) reflectionUtil.callMethod(modelObject, methodName, new Class[] {}, new Object[] {});
	}
	
	public String getArtifactId(Object modelObject) {
		String methodName = "getArtifactId";
		return (String) reflectionUtil.callMethod(modelObject, methodName, new Class[] {}, new Object[] {});
	}
	
	public String getGroupId(Object modelObject) {
		String methodName = "getGroupId";
		return (String) reflectionUtil.callMethod(modelObject, methodName, new Class[] {}, new Object[] {});
	}

	public String getType(Object modelObject) {
		String methodName = "getType";
		return (String) reflectionUtil.callMethod(modelObject, methodName, new Class[] {}, new Object[] {});
	}
	
	public String getClassifier(Object modelObject) {
		String methodName = "getClassifier";
		return (String) reflectionUtil.callMethod(modelObject, methodName, new Class[] {}, new Object[] {});
	}
	
	@SuppressWarnings("rawtypes")
	public Map<Object, InputLocation> getLocations(Object modelObject) {
		Class klass = modelObject.getClass();
		Map<Object, InputLocation> locations = getLocations(modelObject, klass);
		
		if (modelObject instanceof Plugin) {
			Plugin plugin = (Plugin) modelObject;
			List<PluginExecution> executions = plugin.getExecutions();
			if (executions != null && !executions.isEmpty()) {
				PluginExecution pluginExecution = executions.get(0);
				locations.put("execution", pluginExecution.getLocation(""));
			}
		} else if (modelObject instanceof Dependency) {
			Dependency dependency = (Dependency) modelObject;
			List<Exclusion> exclusions = dependency.getExclusions();
			if (exclusions != null && !exclusions.isEmpty()) {
				Exclusion exclusion = exclusions.get(0);
				locations.put("exclusion", exclusion.getLocation(""));
			}
		}
		
		return locations;
	}
	
	public Collection<Object> findGAVObjects(final MavenProject mavenProject) {
		final Collection<Object> objects = new LinkedList<Object>();
		
		final Model originalModel = mavenProject.getOriginalModel();
		objects.add(originalModel);
		objects.addAll(expressionEvaluator.getPath(originalModel, "dependencies"));
		objects.addAll(expressionEvaluator.getPath(originalModel, "dependencyManagement/dependencies"));
		objects.addAll(expressionEvaluator.getPath(originalModel, "build/plugins"));
		objects.addAll(expressionEvaluator.getPath(originalModel, "build/plugins/dependencies"));
		objects.addAll(expressionEvaluator.getPath(originalModel, "build/pluginManagement/plugins"));
		objects.addAll(expressionEvaluator.getPath(originalModel, "build/pluginManagement/plugins/dependencies"));
		
		return objects;
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<Object, InputLocation> getLocations(Object modelObject, Class klass) {
		try {
			Field field = klass.getDeclaredField("locations");
			field.setAccessible(true);
			
			Map<Object, InputLocation> locations = new HashMap<Object, InputLocation>();
			locations.putAll((Map<Object, InputLocation>) field.get(modelObject));
			return locations;
		} catch (NoSuchFieldException e) {
			if (klass.getSuperclass() == null) {
				throw new IllegalArgumentException("No 'locations' field found on object of type " + klass, e);
			} else {
				return getLocations(modelObject, klass.getSuperclass());
			}
		} catch (SecurityException e) {
			throw new IllegalArgumentException("Failed to get 'locations' field on object of type " + klass, e);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Failed to get 'locations' field on object of type " + klass, e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Failed to get 'locations' field on object of type " + klass, e);
		}
	}
	

}
