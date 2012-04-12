package com.lewisd.maven.lint.util;

import java.util.Collection;
import java.util.LinkedList;

public class ExpressionEvaluator {
	
	private final ReflectionUtil reflectionUtil;

	public ExpressionEvaluator() {
		this(new ReflectionUtil());
	}
	
	public ExpressionEvaluator(ReflectionUtil reflectionUtil) {
		this.reflectionUtil = reflectionUtil;
	}

	public Collection<Object> getPath(Object root, String path) {
		final Collection<Object> objects = new LinkedList<Object>();
		
		getPath(root, path, objects);
		
		return objects;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void getPath(Object root, String path, Collection<Object> objects) {
		final String[] pathParts = path.split("/", 2);
		
		final String element = pathParts[0];
		
		final char firstCharacter = Character.toUpperCase(element.charAt(0));
		
		String methodName = "get" + Character.toString(firstCharacter) + element.substring(1);
		
		Object returned = reflectionUtil.callMethod(root, methodName, new Class[] {}, new Object[] {});
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
