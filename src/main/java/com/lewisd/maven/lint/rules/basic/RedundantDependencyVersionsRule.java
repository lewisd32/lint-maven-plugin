package com.lewisd.maven.lint.rules.basic;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.InputLocation;
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
		return "RedundantDepVersion";
	}

	@Override
	public String getDescription() {
		return "Dependency versions should be set in one place, and not overridden without changing the version. " +
				"If, for example, <dependencyManagement> sets a version, and <dependencies> somewhere overrides it, " +
				"but with the same version, this can make version upgrades more difficult, due to the repetition.";
	}

	public void invoke(MavenProject mavenProject, final Map<String, Object> models, final ResultCollector resultCollector) {
		Model originalModel = mavenProject.getOriginalModel();
		Collection<Dependency> dependencies = expressionEvaluator.getPath(originalModel, "dependencies");
		Collection<Dependency> managedDependencies = expressionEvaluator.getPath(originalModel, "dependencyManagement/dependencies");

		Map<String, Dependency> managedDependenciesByManagementKey = modelUtil.mapByManagementKey(managedDependencies);
		
		Collection<Dependency> otherDependencies = new LinkedList<Dependency>(dependencies);
		for (final Dependency dependency : dependencies) {
			Dependency managedDependency = managedDependenciesByManagementKey.get(dependency.getManagementKey());
			if (managedDependency != null) {
				checkForRedundantVersions(mavenProject, resultCollector, dependency, managedDependency, "Dependency", "in dependencyManagement");
			}
			
			ExtDependency inheritedDependency = modelUtil.findInheritedDependency(mavenProject, dependency);
			if (inheritedDependency != null) {
				checkForRedundantVersions(mavenProject, resultCollector, dependency, inheritedDependency, "Dependency", "is inherited from " + inheritedDependency.getMavenProject().getId());
			}
			
	        // check for duplication within dependencies (Maven logs a warning for these)
			checkForDuplicateArtifacts(mavenProject, resultCollector, dependency, otherDependencies, "Dependency");
		}

		Collection<Dependency> otherManagedDependencies = new LinkedList<Dependency>(managedDependencies);
		for (final Dependency managedDependency : managedDependencies) {
			ExtDependency inheritedDependency = modelUtil.findInheritedDependency(mavenProject, managedDependency);
			if (inheritedDependency != null) {
				checkForRedundantVersions(mavenProject, resultCollector, managedDependency, inheritedDependency, "Managed dependency", "is inherited from " + inheritedDependency.getMavenProject().getId());
			}
			
			// check for duplication within managed dependencies (Maven logs a warning for these)
			checkForDuplicateArtifacts(mavenProject, resultCollector, managedDependency, otherManagedDependencies, "Managed dependency");
		}

	}

    private void checkForDuplicateArtifacts(MavenProject mavenProject, final ResultCollector resultCollector,
            final Dependency dependency, Collection<Dependency> otherDependencies, String dependencyDescription) {
        for (Iterator<Dependency> i = otherDependencies.iterator(); i.hasNext();) {
            final Dependency otherManagedDependency = i.next();
            
            if (otherManagedDependency.getManagementKey().equals(dependency.getManagementKey())) {
                i.remove();
                if (otherManagedDependency != dependency) {
        	        final String version = modelUtil.getVersion(dependency);
        	        final String otherVersion = modelUtil.getVersion(otherManagedDependency);
        	        final InputLocation location = modelUtil.getLocation(dependency, "version");
        	        final InputLocation otherLocation = modelUtil.getLocation(otherManagedDependency, "version");
        	        if (version != null && otherVersion != null && otherVersion.equals(version)) {
        	            resultCollector.addViolation(mavenProject, this, dependencyDescription + " '" + modelUtil.getKey(dependency) +
        	                    "' is declared multiple times with the same version: " +
        	                    otherLocation.getLineNumber() + ":" + otherLocation.getColumnNumber(), location);
        	        } else {
        	            resultCollector.addViolation(mavenProject, this, dependencyDescription + " '" + modelUtil.getKey(dependency) +
        	                    "' is declared multiple times with different versions (" + version + ", " + otherVersion + ")", location);
        	        }
        	    }
            }
            
        }
    }

	
}
