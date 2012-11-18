package com.lewisd.maven.lint.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.jxpath.JXPathContext;

public class ExpressionEvaluator {
	
	@SuppressWarnings("unchecked")
	public <T> Collection<T> getPath(Object root, String path) {
		final List<T> list = new LinkedList<T>();
		final JXPathContext context = JXPathContext.newContext(root);

		for (final Iterator<?> i = context.iterate(path); i.hasNext();) {
			final T next = (T) i.next();
			list.add(next);
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getFirst(Object root, String path) {
		final JXPathContext context = JXPathContext.newContext(root);

		for (final Iterator<?> i = context.iterate(path); i.hasNext();) {
			return (T) i.next();
		}
		return null;
	}
	
}
