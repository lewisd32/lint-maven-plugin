package com.lewisd.maven.lint.rules.basic;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.rules.AbstractRule;
import com.lewisd.maven.lint.util.ExpressionEvaluator;
import com.lewisd.maven.lint.util.ModelUtil;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;

public class GroupArtifactVersionMustBeInCorrectOrderRule extends AbstractRule {

	@SuppressWarnings("rawtypes")
	private final Map<Class, List<String>> expectedOrderByClass = new HashMap<Class, List<String>>();

	@Autowired
	public GroupArtifactVersionMustBeInCorrectOrderRule(final ExpressionEvaluator expressionEvaluator, final ModelUtil modelUtil) {
		super(expressionEvaluator, modelUtil);
	}

	@Override
	protected void addRequiredModels(final Set<String> requiredModels) {
	}

	@Override
	public String getIdentifier() {
		return "GAVOrder";
	}

	@Override
	public String getDescription() {
		return "Maven convention is that the groupId, artifactId, and version elements be listed in that order.  " +
				"Other elements with short, simple content, such as type, scope, classifier, etc, should be before elements " +
				"with longer content, such as configuration, executions, and exclusions, otherwise they can be easily missed, " +
				"leading to confusion";
	}

	@Required
	public void setProjectElementOrder(List<String> expectedOrder) {
		expectedOrderByClass.put(Model.class, expectedOrder);
	}

	@Required
	public void setDependencyElementOrder(List<String> expectedOrder) {
		expectedOrderByClass.put(Dependency.class, expectedOrder);
	}

	@Required
	public void setPluginElementOrder(List<String> expectedOrder) {
		expectedOrderByClass.put(Plugin.class, expectedOrder);
	}

	public void invoke(final MavenProject mavenProject, final Map<String, Object> models, final ResultCollector resultCollector) {

		final Collection<Object> objectsToCheck = getModelUtil().findGAVObjects(mavenProject);

		for (final Object object: objectsToCheck) {
			final List<String> sortOrder = findSortOrder(object.getClass());
			final Map<Object, InputLocation> locations = getModelUtil().getLocations(object);

			// We don't need the location of the outer element for this rule
			locations.remove("");

			// Sort the locations by their location in the file
			final SortedMap<InputLocation, Object> actualOrderedElements = new TreeMap<InputLocation, Object>(new InputLocationMapValueComparator());
			for(final Map.Entry<Object, InputLocation> entry : locations.entrySet()) {
				actualOrderedElements.put(entry.getValue(), entry.getKey());
			}


			final List<Object> expectedOrderElements = new LinkedList<Object>();
			// First, find the elements that do exist, for which we care about the order
			for (final String location : sortOrder) {
				if (locations.containsKey(location)) {
					expectedOrderElements.add(location);
				}
			}
			// Next, find the remaining elements, for which we don't care about the order,
			// so add them in the order they actually are.
			for (final Object location : actualOrderedElements.values()) {
				if (!expectedOrderElements.contains(location)) {
					expectedOrderElements.add(location);
				}
			}

			// Don't expect any elements that aren't actually there
			expectedOrderElements.retainAll(locations.keySet());


			final Iterator<Object> expectedOrderElementIterator = expectedOrderElements.iterator();
			final Iterator<Object> actualOrderedElementsIterator = actualOrderedElements.values().iterator();

			while (expectedOrderElementIterator.hasNext() && actualOrderedElementsIterator.hasNext()) {
				final Object expectedElement = expectedOrderElementIterator.next();
				final Object actualElement = actualOrderedElementsIterator.next();
				if (!expectedElement.equals(actualElement)) {
					resultCollector.addViolation(mavenProject, this, "Found '" + actualElement + "' but was expecting '" + expectedElement + "'", locations.get(actualElement));
					break;
				}
			}

		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<String> findSortOrder(Class<? extends Object> klass) {
		for (Class potentialClass : expectedOrderByClass.keySet()) {
			if (potentialClass.isAssignableFrom(klass)) {
				return expectedOrderByClass.get(potentialClass);
			}
		}
		throw new IllegalStateException("No expected sort order found for class " + klass);
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

}
