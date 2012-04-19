package com.lewisd.maven.lint.rules.basic;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.springframework.beans.factory.annotation.Autowired;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.model.ExtDependency;
import com.lewisd.maven.lint.rules.AbstractReduntantVersionRule;
import com.lewisd.maven.lint.util.ExpressionEvaluator;
import com.lewisd.maven.lint.util.ModelUtil;

public class RedundantDependencyVersionsRule extends AbstractReduntantVersionRule {

	@Autowired
	public RedundantDependencyVersionsRule(ExpressionEvaluator expressionEvaluator, ModelUtil modelUtil) {
		super(expressionEvaluator, modelUtil);
	}

	@Override
	protected void addRequiredModels(Set<String> requiredModels) {
	}
	
	@Override
	public String getIdentifier() {
		return "REDUNDANTDEPVERSION";
	}

	public void invoke(MavenProject mavenProject, final Map<String, Object> models, final ResultCollector resultCollector) {
		Model originalModel = mavenProject.getOriginalModel();
		Collection<Dependency> dependencies = expressionEvaluator.getPath(originalModel, "dependencies");
		Collection<Dependency> managedDependencies = expressionEvaluator.getPath(originalModel, "dependencyManagement/dependencies");

		Map<String, Dependency> managedDependenciesByManagementKey = modelUtil.mapByManagementKey(managedDependencies);
		
		for (final Dependency dependency : dependencies) {
			Dependency managedDependency = managedDependenciesByManagementKey.get(dependency.getManagementKey());
			if (managedDependency != null) {
				checkForRedundantVersions(mavenProject, resultCollector, dependency, managedDependency, "Dependency", "in dependencyManagement");
			}
			
			ExtDependency inheritedDependency = modelUtil.findInheritedDependency(mavenProject, dependency);
			if (inheritedDependency != null) {
				checkForRedundantVersions(mavenProject, resultCollector, dependency, inheritedDependency, "Dependency", "is inherited from " + inheritedDependency.getMavenProject().getId());
			}
		}
		
		for (final Dependency managedDependency : managedDependencies) {
			ExtDependency inheritedDependency = modelUtil.findInheritedDependency(mavenProject, managedDependency);
			if (inheritedDependency != null) {
				checkForRedundantVersions(mavenProject, resultCollector, managedDependency, inheritedDependency, "Managed dependency", "is inherited from " + inheritedDependency.getMavenProject().getId());
			}
		}

	}

	
}
