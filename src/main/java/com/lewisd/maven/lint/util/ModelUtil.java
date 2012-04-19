package com.lewisd.maven.lint.util;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.project.MavenProject;
import org.springframework.beans.factory.annotation.Autowired;

import com.lewisd.maven.lint.model.ExtDependency;
import com.lewisd.maven.lint.model.ExtPlugin;

public class ModelUtil {

	private static final Object[] EMPTY_OBJECT_ARRAY = new Object[] {};
	@SuppressWarnings("rawtypes")
	private static final Class[] EMPTY_CLASS_ARRAY = new Class[] {};
	private final ReflectionUtil reflectionUtil;
	private final ExpressionEvaluator expressionEvaluator;

	@Autowired
	public ModelUtil(ReflectionUtil reflectionUtil, ExpressionEvaluator expressionEvaluator) {
		this.reflectionUtil = reflectionUtil;
		this.expressionEvaluator = expressionEvaluator;
	}
	
	/*
	 * TODO: This reflection nonsense is all a bit rubish.  Let's add useful interfaces to the ExtPlugin, ExtDependency, etc
	 * classes, and create instances of those instead.
	 */

	public InputLocation getLocation(Object modelObject, Object key) {
		String methodName = "getLocation";
		return (InputLocation) reflectionUtil.callMethod(modelObject, methodName, new Class[] {Object.class}, new Object[] {key});
	}
	
	public String getVersion(Object modelObject) {
		String methodName = "getVersion";
		return (String) reflectionUtil.callMethod(modelObject, methodName, EMPTY_CLASS_ARRAY, EMPTY_OBJECT_ARRAY);
	}
	
	public String getArtifactId(Object modelObject) {
		String methodName = "getArtifactId";
		return (String) reflectionUtil.callMethod(modelObject, methodName, EMPTY_CLASS_ARRAY, EMPTY_OBJECT_ARRAY);
	}
	
	public String getGroupId(Object modelObject) {
		String methodName = "getGroupId";
		return (String) reflectionUtil.callMethod(modelObject, methodName, EMPTY_CLASS_ARRAY, EMPTY_OBJECT_ARRAY);
	}

	public String getType(Object modelObject) {
		String methodName = "getType";
		return (String) reflectionUtil.callMethod(modelObject, methodName, EMPTY_CLASS_ARRAY, EMPTY_OBJECT_ARRAY);
	}
	
	public String getClassifier(Object modelObject) {
		String methodName = "getClassifier";
		return (String) reflectionUtil.callMethod(modelObject, methodName, EMPTY_CLASS_ARRAY, EMPTY_OBJECT_ARRAY);
	}

	public String getKey(Object modelObject) {
		
		if (modelObject instanceof Dependency) {
			return ((Dependency)modelObject).getManagementKey();
		} else if (modelObject instanceof Plugin) {
			return ((Plugin)modelObject).getKey();
		} else {
			throw new IllegalArgumentException("Unknown object type: " + modelObject.getClass());
		}
	}
	
	@SuppressWarnings("rawtypes")
	public Map<Object, InputLocation> getLocations(Object modelObject) {
		Class klass = modelObject.getClass();
		Map<Object, InputLocation> locations = getLocations(modelObject, klass);
		
		if (modelObject instanceof Plugin) {
			Plugin plugin = (Plugin) modelObject;
			List<PluginExecution> executions = plugin.getExecutions();
			if (executions != null && !executions.isEmpty()) {
				PluginExecution pluginExecution = executions.get(0);
				locations.put("execution", pluginExecution.getLocation(""));
			}
		} else if (modelObject instanceof Dependency) {
			Dependency dependency = (Dependency) modelObject;
			List<Exclusion> exclusions = dependency.getExclusions();
			if (exclusions != null && !exclusions.isEmpty()) {
				Exclusion exclusion = exclusions.get(0);
				locations.put("exclusion", exclusion.getLocation(""));
			}
		}
		
		return locations;
	}
	
	public Collection<Object> findGAVObjects(final MavenProject mavenProject) {
		final Collection<Object> objects = new LinkedList<Object>();
		
		final Model originalModel = mavenProject.getOriginalModel();
		objects.add(originalModel);
		objects.addAll(expressionEvaluator.getPath(originalModel, "dependencies"));
		objects.addAll(expressionEvaluator.getPath(originalModel, "dependencyManagement/dependencies"));
		objects.addAll(expressionEvaluator.getPath(originalModel, "build/plugins"));
		objects.addAll(expressionEvaluator.getPath(originalModel, "build/plugins/dependencies"));
		objects.addAll(expressionEvaluator.getPath(originalModel, "build/pluginManagement/plugins"));
		objects.addAll(expressionEvaluator.getPath(originalModel, "build/pluginManagement/plugins/dependencies"));
		
		return objects;
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<Object, InputLocation> getLocations(Object modelObject, Class klass) {
		try {
			Field field = klass.getDeclaredField("locations");
			field.setAccessible(true);
			
			Map<Object, InputLocation> locations = new HashMap<Object, InputLocation>();
			locations.putAll((Map<Object, InputLocation>) field.get(modelObject));
			return locations;
		} catch (NoSuchFieldException e) {
			if (klass.getSuperclass() == null) {
				throw new IllegalArgumentException("No 'locations' field found on object of type " + klass, e);
			} else {
				return getLocations(modelObject, klass.getSuperclass());
			}
		} catch (SecurityException e) {
			throw new IllegalArgumentException("Failed to get 'locations' field on object of type " + klass, e);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Failed to get 'locations' field on object of type " + klass, e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Failed to get 'locations' field on object of type " + klass, e);
		}
	}

	public Map<String, Dependency> mapByManagementKey(Collection<Dependency> dependencies) {
		Map<String, Dependency> map = new HashMap<String, Dependency>();
		
		for (final Dependency dependency : dependencies) {
			map.put(dependency.getManagementKey(), dependency);
		}
		
		return map;
	}

	public Map<String, Plugin> mapById(Collection<Plugin> dependencies) {
		Map<String, Plugin> map = new HashMap<String, Plugin>();
		
		for (final Plugin Plugin : dependencies) {
			map.put(Plugin.getId(), Plugin);
		}
		
		return map;
	}

	public ExtDependency findInheritedDependency(final MavenProject mavenProject, final Dependency dependency) {
		final MavenProject parent = mavenProject.getParent();
		
		if (parent != null) {
			final Map<String, Dependency> dependencies = mapByManagementKey(expressionEvaluator.<Dependency>getPath(parent.getOriginalModel(), "dependencies"));
			final Map<String, Dependency> managedDependencies = mapByManagementKey(expressionEvaluator.<Dependency>getPath(parent.getOriginalModel(), "dependencyManagement/dependencies"));

			Dependency parentDependency = dependencies.get(dependency.getManagementKey());
			if (parentDependency != null) {
				return new ExtDependency(parent, parentDependency);
			}
			
			Dependency parentManagedDependency = managedDependencies.get(dependency.getManagementKey());
			if (parentManagedDependency != null) {
				return new ExtDependency(parent, parentManagedDependency);
			}
			
			return findInheritedDependency(parent, dependency);
		}
		
		return null;
	}

	public List<ExtPlugin> findInheritedPlugins(final MavenProject mavenProject,
			final Plugin plugin) {
		final List<ExtPlugin> inheritedPlugins = new LinkedList<ExtPlugin>();
		findInheritedPlugins(inheritedPlugins, mavenProject, plugin);
		return inheritedPlugins;
	}
	
	private void findInheritedPlugins(final List<ExtPlugin> inheritedPlugins, final MavenProject mavenProject,
			final Plugin plugin) {
		final MavenProject parent = mavenProject.getParent();
		
		if (parent != null) {
			final Map<String, Plugin> dependencies = mapById(expressionEvaluator.<Plugin>getPath(parent.getOriginalModel(), "build/plugins"));
			final Map<String, Plugin> managedDependencies = mapById(expressionEvaluator.<Plugin>getPath(parent.getOriginalModel(), "build/pluginManagement/plugins"));

			Plugin parentDependency = dependencies.get(plugin.getId());
			if (parentDependency != null) {
				inheritedPlugins.add(new ExtPlugin(parent, parentDependency));
			}
			
			Plugin parentManagedDependency = managedDependencies.get(plugin.getId());
			if (parentManagedDependency != null) {
				inheritedPlugins.add(new ExtPlugin(parent, parentManagedDependency));
			}
			
			findInheritedPlugins(inheritedPlugins, parent, plugin);
		}
	}
	

}
