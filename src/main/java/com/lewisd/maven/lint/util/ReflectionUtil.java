package com.lewisd.maven.lint.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ReflectionUtil {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object callMethod(Object modelObject, String methodName, Class[] argTypes, Object[] args) {
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
