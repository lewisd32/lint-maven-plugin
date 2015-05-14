package com.lewisd.maven.lint.rules.basic;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.rules.AbstractRule;
import com.lewisd.maven.lint.util.ExpressionEvaluator;
import com.lewisd.maven.lint.util.ModelUtil;
import org.apache.maven.model.InputLocation;
import org.apache.maven.project.MavenProject;
import org.jdom2.Document;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.jdom2.filter.Filters;
import org.jdom2.located.LocatedElement;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class RedundantDependencyVersionsJDOMRule extends AbstractRule {

	@Autowired
	public RedundantDependencyVersionsJDOMRule(ExpressionEvaluator expressionEvaluator, ModelUtil modelUtil) {
		super(expressionEvaluator, modelUtil);
	}

	@Override
	protected void addRequiredModels(Set<String> requiredModels) {
		requiredModels.add("jdom2");
	}

	@Override
	public String getIdentifier() {
		return "RedundantDepVersion";
	}

	@Override
	public String getDescription() {
		return "Dependency versions should be set in one place, and not overridden without changing the version. " +
				"If, for example, <dependencyManagement> sets a version, and <dependencies> somewhere overrides it, " +
				"but with the same version, this can make version upgrades more difficult, due to the repetition.";
	}

	public void invoke(MavenProject mavenProject, final Map<String, Object> models, final ResultCollector resultCollector) {

		final Document document = (Document) models.get("jdom2");

		final XPathFactory xpathFactory = XPathFactory.instance();
		final Filter<LocatedElement> filter = Filters.fclass(LocatedElement.class);
		Namespace mavenNamespace = Namespace.getNamespace("m", "http://maven.apache.org/POM/4.0.0");
		final XPathExpression<LocatedElement> dependenciesXpath = xpathFactory.compile("/m:project/m:dependencies/m:dependency", filter, null, mavenNamespace);
		final XPathExpression<LocatedElement> managedDependenciesXpath = xpathFactory.compile("/m:project/m:dependencyManagement/m:dependency", filter, null, mavenNamespace);

		final List<LocatedElement> dependencies = dependenciesXpath.evaluate(document);
		final List<LocatedElement> managedDependencies = managedDependenciesXpath.evaluate(document);




//		Model originalModel = mavenProject.getOriginalModel();
//		Collection<Dependency> dependencies = expressionEvaluator.getPath(originalModel, "dependencies");
//		Collection<Dependency> managedDependencies = expressionEvaluator.getPath(originalModel, "dependencyManagement/dependencies");
//
//		Map<String, Dependency> managedDependenciesByManagementKey = modelUtil.mapByManagementKey(managedDependencies);
//
//		for (final Dependency dependency : dependencies) {
//			Dependency managedDependency = managedDependenciesByManagementKey.get(dependency.getManagementKey());
//			if (managedDependency != null) {
//				checkForRedundantVersions(mavenProject, resultCollector, dependency, managedDependency, "Dependency", "in dependencyManagement");
//			}
//
//			ExtDependency inheritedDependency = modelUtil.findInheritedDependency(mavenProject, dependency);
//			if (inheritedDependency != null) {
//				checkForRedundantVersions(mavenProject, resultCollector, dependency, inheritedDependency, "Dependency", "is inherited from " + inheritedDependency.getMavenProject().getId());
//			}
//		}
//
//		for (final Dependency managedDependency : managedDependencies) {
//			ExtDependency inheritedDependency = modelUtil.findInheritedDependency(mavenProject, managedDependency);
//			if (inheritedDependency != null) {
//				checkForRedundantVersions(mavenProject, resultCollector, managedDependency, inheritedDependency, "Managed dependency", "is inherited from " + inheritedDependency.getMavenProject().getId());
//			}
//		}

	}

	protected void checkForRedundantVersions(final MavenProject mavenProject,
			final ResultCollector resultCollector, final Object modelObject,
			final Object inheritedModelObject, final String dependencyDescription, final String inheritedDescription) {
		final String version = modelUtil.getVersion(modelObject);
		final String inheritedVersion = modelUtil.getVersion(inheritedModelObject);
		// both have a version, but if they're different, that might be ok.
		// But if they're the same, then one is redundant.
		if (version != null && inheritedVersion != null && inheritedVersion.equals(version)) {
			final InputLocation location = modelUtil.getLocation(modelObject, "version");
			resultCollector.addViolation(mavenProject, this, dependencyDescription + " '" + modelUtil.getKey(modelObject) +
					"' has same version (" + version + ") as " + inheritedDescription, location);
		}
	}


}
