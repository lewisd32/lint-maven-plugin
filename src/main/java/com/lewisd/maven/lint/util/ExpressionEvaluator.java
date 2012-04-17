package com.lewisd.maven.lint.util;

import java.util.Collection;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;

public class ExpressionEvaluator {
	
	private final ReflectionUtil reflectionUtil;

	@Autowired
	public ExpressionEvaluator(ReflectionUtil reflectionUtil) {
		this.reflectionUtil = reflectionUtil;
	}

	public <T> Collection<T> getPath(Object root, String path) {
		final Collection<T> objects = new LinkedList<T>();
		
		getPath(root, path, objects);
		
		return objects;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <T> void getPath(Object root, String path, Collection<T> objects) {
		final String[] pathParts = path.split("/", 2);
		
		final String element = pathParts[0];
		
		final char firstCharacter = Character.toUpperCase(element.charAt(0));
		
		String methodName = "get" + Character.toString(firstCharacter) + element.substring(1);
		
		T returned = (T) reflectionUtil.callMethod(root, methodName, new Class[] {}, new Object[] {});
		if (returned != null) {
			if (returned instanceof Collection) {
				if (pathParts.length > 1) {
					for(Object object : (Collection)returned) {
						getPath(object, pathParts[1], objects);
					}
				} else {
					objects.addAll((Collection)returned);
				}
			} else {
				if (pathParts.length > 1) {
					getPath(returned, pathParts[1], objects);
				} else {
					objects.add(returned);
				}
			}
		}
	}
	

}
