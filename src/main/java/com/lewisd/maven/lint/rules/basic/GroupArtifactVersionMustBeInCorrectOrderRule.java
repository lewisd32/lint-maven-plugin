package com.lewisd.maven.lint.rules.basic;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.maven.model.InputLocation;
import org.apache.maven.project.MavenProject;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.rules.AbstractRule;

public class GroupArtifactVersionMustBeInCorrectOrderRule extends AbstractRule {
	
	private static final Map<Object, Integer> sortOrder = new HashMap<Object, Integer>();
	
	static {
		sortOrder.put("", 0);
		sortOrder.put("groupId", 1);
		sortOrder.put("artifactId", 2);
		sortOrder.put("classifier", 3);
		sortOrder.put("type", 4);
		sortOrder.put("version", 5);
	}

	@Override
	protected void addRequiredModels(final Set<String> requiredModels) {
	}

	public void invoke(final MavenProject mavenProject, final Map<String, Object> models, final ResultCollector resultCollector) {
		
		final Collection<Object> objectsToCheck = modelUtil.findGAVObjects(mavenProject);
		
		for (final Object object: objectsToCheck) {
			final Map<Object, InputLocation> locations = modelUtil.getLocations(object);
			final SortedMap<InputLocation, Object> sortedLocations = new TreeMap<InputLocation, Object>(new InputLocationMapValueComparator());
			locations.keySet().retainAll(sortOrder.keySet());
			
			for(final Map.Entry<Object, InputLocation> entry : locations.entrySet()) {
				sortedLocations.put(entry.getValue(), entry.getKey());
			}
			
			final SortedMap<Object, InputLocation> expectedLocations = new TreeMap<Object, InputLocation>(new ElementOrderComparator());
			expectedLocations.putAll(locations);

			final Iterator<Entry<Object, InputLocation>> expectedLocationsIterator = expectedLocations.entrySet().iterator();
			final Iterator<Entry<InputLocation, Object>> sortedLocationsIterator = sortedLocations.entrySet().iterator();
			
			while (expectedLocationsIterator.hasNext() && sortedLocationsIterator.hasNext()) {
				final Entry<Object, InputLocation> expectedLocationsEntry = expectedLocationsIterator.next();
				final Entry<InputLocation, Object> sortedLocationsEntry = sortedLocationsIterator.next();
				
				final Object expectedLocationsElement = expectedLocationsEntry.getKey();
				final Object sortedLocationsElement = sortedLocationsEntry.getValue();
				
				if (!expectedLocationsElement.equals(sortedLocationsElement)) {
					resultCollector.addViolation(mavenProject, "Found '" + sortedLocationsElement + "' but was expecting '" + expectedLocationsElement + "'", sortedLocationsEntry.getKey());
					break;
				}
			}
			
		}
	}
	
	private class InputLocationMapValueComparator implements Comparator<InputLocation> {

		@Override
		public int compare(final InputLocation a, final InputLocation b) {
			if (a.getLineNumber() == b.getLineNumber()) {
				return a.getColumnNumber() - b.getColumnNumber();
			} else {
				return a.getLineNumber() - b.getLineNumber();
			}
		}
		
	}
	
	private class ElementOrderComparator implements Comparator<Object> {
		
		@Override
		public int compare(final Object a, final Object b) {
			final int va = sortOrder.get(a);
			final int vb = sortOrder.get(b);
			return va - vb;
		}
		
	}
	
}
