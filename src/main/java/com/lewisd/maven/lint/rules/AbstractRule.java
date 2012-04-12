package com.lewisd.maven.lint.rules;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.maven.model.InputLocation;

import com.lewisd.maven.lint.Rule;

public abstract class AbstractRule implements Rule {

	protected static final String VERSION_PROPERTIES = "versionProperties";
	protected static final String MAVEN_PROJECT = "mavenProject";

	public Set<String> getRequiredModels() {
		final Set<String> requiredModels = new HashSet<String>();
		addRequiredModels(requiredModels);
		return requiredModels;
	}
	
	protected abstract void addRequiredModels(Set<String> requiredModels);
	
	protected InputLocation getLocation(Object modelObject, Object key) {
		String methodName = "getLocation";
		return (InputLocation) callMethod(modelObject, methodName, new Class[] {Object.class}, new Object[] {key});
	}
	
	protected String getVersion(Object modelObject) {
		String methodName = "getVersion";
		return (String) callMethod(modelObject, methodName, new Class[] {}, new Object[] {});
	}
	
	protected String getArtifactId(Object modelObject) {
		String methodName = "getArtifactId";
		return (String) callMethod(modelObject, methodName, new Class[] {}, new Object[] {});
	}
	
	protected String getGroupId(Object modelObject) {
		String methodName = "getGroupId";
		return (String) callMethod(modelObject, methodName, new Class[] {}, new Object[] {});
	}

	protected String getType(Object modelObject) {
		String methodName = "getType";
		return (String) callMethod(modelObject, methodName, new Class[] {}, new Object[] {});
	}
	
	protected String getClassifier(Object modelObject) {
		String methodName = "getClassifier";
		return (String) callMethod(modelObject, methodName, new Class[] {}, new Object[] {});
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Map<Object, InputLocation> getLocations(Object modelObject) {
		Class klass = modelObject.getClass();
		try {
			Field field = klass.getDeclaredField("locations");
			field.setAccessible(true);
			
			Map<Object, InputLocation> locations = new HashMap<Object, InputLocation>();
			locations.putAll((Map<Object, InputLocation>) field.get(modelObject));
			return locations;
		} catch (NoSuchFieldException e) {
			throw new IllegalArgumentException("No 'locations' field found on object of type " + klass, e);
		} catch (SecurityException e) {
			throw new IllegalArgumentException("Failed to get 'locations' field on object of type " + klass, e);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Failed to get 'locations' field on object of type " + klass, e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Failed to get 'locations' field on object of type " + klass, e);
		}
	}
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object callMethod(Object modelObject, String methodName, Class[] argTypes, Object[] args) {
		Class klass = modelObject.getClass();
		try {
			Method method = klass.getMethod(methodName, argTypes);
			
			return method.invoke(modelObject, args);
			
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("No '" + methodName + "(" + Arrays.asList(argTypes) + ")' method found on object of type " + klass, e);
		} catch (SecurityException e) {
			throw new IllegalArgumentException("No '" + methodName + "(" + Arrays.asList(argTypes) + ")' method found on object of type " + klass, e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Failed to invoke '" + methodName + "(" + Arrays.asList(argTypes) + ")' method on object of type " + klass, e);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Failed to invoke '" + methodName + "(" + Arrays.asList(argTypes) + ")' method on object of type " + klass, e);
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException("Failed to invoke '" + methodName + "(" + Arrays.asList(argTypes) + ")' method on object of type " + klass, e);
		}
	}

}
