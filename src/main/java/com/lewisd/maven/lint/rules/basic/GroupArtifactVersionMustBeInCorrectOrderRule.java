package com.lewisd.maven.lint.rules.basic;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.maven.model.Dependency;
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
	protected void addRequiredModels(Set<String> requiredModels) {
	}

	public void invoke(MavenProject mavenProject, final Map<String, Object> models, final ResultCollector resultCollector) {
		
		List<Dependency> dependencies = mavenProject.getOriginalModel().getDependencies();
		
		for (Dependency dependency : dependencies) {
			Map<Object, InputLocation> locations = getLocations(dependency);
			SortedMap<InputLocation, Object> sortedLocations = new TreeMap<InputLocation, Object>(new InputLocationMapValueComparator());
			
			for(Map.Entry<Object, InputLocation> entry : locations.entrySet()) {
				sortedLocations.put(entry.getValue(), entry.getKey());
			}
			
			locations.keySet().retainAll(sortOrder.keySet());
			
			SortedMap<Object, InputLocation> expectedLocations = new TreeMap<Object, InputLocation>(new ElementOrderComparator());
			expectedLocations.putAll(locations);

			Iterator<Entry<Object, InputLocation>> expectedLocationsIterator = expectedLocations.entrySet().iterator();
			Iterator<Entry<InputLocation, Object>> sortedLocationsIterator = sortedLocations.entrySet().iterator();
			
			while (expectedLocationsIterator.hasNext() && sortedLocationsIterator.hasNext()) {
				Entry<Object, InputLocation> expectedLocationsEntry = expectedLocationsIterator.next();
				Entry<InputLocation, Object> sortedLocationsEntry = sortedLocationsIterator.next();
				
				Object expectedLocationsElement = expectedLocationsEntry.getKey();
				Object sortedLocationsElement = sortedLocationsEntry.getValue();
				
				if (!expectedLocationsElement.equals(sortedLocationsElement)) {
					resultCollector.addViolation(mavenProject, "Found '" + sortedLocationsElement + "' but was expecting '" + expectedLocationsElement + "'", sortedLocationsEntry.getKey());
					break;
				}
			}
			
		}
		
		
//		for (Map.Entry<Object,VersionProperty> entry : versionPropertyByObject.entrySet()) {
//			final VersionProperty versionProperty = entry.getValue();
//			for (String propertyName : versionProperty.getPropertyNames()) {
//				if (propertyName.contains("-")) {
//					InputLocation location = getLocation(entry.getKey(), "version");
//					resultCollector.addViolation(mavenProject, "Version property names must not contain a hyphen: '" + propertyName + "'", location);
//				}
//			}
//		}
	}
	
	private class InputLocationMapValueComparator implements Comparator<InputLocation> {

		@Override
		public int compare(InputLocation a, InputLocation b) {
			if (a.getLineNumber() == b.getLineNumber()) {
				return a.getColumnNumber() - b.getColumnNumber();
			} else {
				return a.getLineNumber() - b.getLineNumber();
			}
		}
		
	}
	
	private class ElementOrderComparator implements Comparator<Object> {
		
		@Override
		public int compare(Object a, Object b) {
			int va = sortOrder.get(a);
			int vb = sortOrder.get(b);
			return va - vb;
		}
		
	}
	
}
