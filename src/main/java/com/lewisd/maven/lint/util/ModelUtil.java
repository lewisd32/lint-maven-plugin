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
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.springframework.beans.factory.annotation.Autowired;

import com.lewisd.maven.lint.model.Coordinates;
import com.lewisd.maven.lint.model.ExtDependency;
import com.lewisd.maven.lint.model.ExtPlugin;
import com.lewisd.maven.lint.model.ObjectWithPath;

public class ModelUtil {

    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[] {};
    @SuppressWarnings("rawtypes")
    private static final Class[] EMPTY_CLASS_ARRAY = new Class[] {};
    private final ReflectionUtil reflectionUtil;
    private final ExpressionEvaluator expressionEvaluator;
    private final Log log;

    @Autowired
    public ModelUtil(final ReflectionUtil reflectionUtil, final ExpressionEvaluator expressionEvaluator,
                     final Log log) {
        this.reflectionUtil = reflectionUtil;
        this.expressionEvaluator = expressionEvaluator;
        this.log = log;
    }

    /*
     * TODO: This reflection nonsense is all a bit rubish.  Let's add useful interfaces to the ExtPlugin, ExtDependency, etc
     * classes, and create instances of those instead.
     */

    public InputLocation getLocation(final Object modelObject) {
        return getLocation(modelObject, "");
    }

    public InputLocation getLocation(final Object modelObject, final Object key) {
        final String methodName = "getLocation";
        return (InputLocation) reflectionUtil.callMethod(modelObject, methodName, new Class[] { Object.class }, new Object[] { key });
    }

    public String getVersion(final Object modelObject) {
        final String methodName = "getVersion";
        return (String) reflectionUtil.callMethod(modelObject, methodName, EMPTY_CLASS_ARRAY, EMPTY_OBJECT_ARRAY);
    }

    public String getArtifactId(final Object modelObject) {
        final String methodName = "getArtifactId";
        return (String) reflectionUtil.callMethod(modelObject, methodName, EMPTY_CLASS_ARRAY, EMPTY_OBJECT_ARRAY);
    }

    public String getGroupId(final Object modelObject) {
        final String methodName = "getGroupId";
        return (String) reflectionUtil.callMethod(modelObject, methodName, EMPTY_CLASS_ARRAY, EMPTY_OBJECT_ARRAY);
    }

    public String getType(final Object modelObject) {
        final String methodName = "getType";
        return (String) reflectionUtil.callMethod(modelObject, methodName, EMPTY_CLASS_ARRAY, EMPTY_OBJECT_ARRAY);
    }

    public String tryGetType(final Object modelObject) {
        try {
            return getType(modelObject);
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }

    public String getClassifier(final Object modelObject) {
        final String methodName = "getClassifier";
        return (String) reflectionUtil.callMethod(modelObject, methodName, EMPTY_CLASS_ARRAY, EMPTY_OBJECT_ARRAY);
    }

    public String tryGetClassifier(final Object modelObject) {
        try {
            return getClassifier(modelObject);
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }

    public String getKey(final Object modelObject) {

        if (modelObject instanceof Dependency) {
            return ((Dependency) modelObject).getManagementKey();
        } else if (modelObject instanceof Plugin) {
            return ((Plugin) modelObject).getKey();
        } else {
            throw new IllegalArgumentException("Unknown object type: " + modelObject.getClass());
        }
    }

    @SuppressWarnings("rawtypes")
    public Map<Object, InputLocation> getLocations(final Object modelObject) {
        final Class klass = modelObject.getClass();
        final Map<Object, InputLocation> locations = getLocations(modelObject, klass);

        if (modelObject instanceof Plugin) {
            final Plugin plugin = (Plugin) modelObject;
            final List<PluginExecution> executions = plugin.getExecutions();
            if (executions != null && !executions.isEmpty()) {
                final PluginExecution pluginExecution = executions.get(0);
                final InputLocation location = pluginExecution.getLocation("");
                if (location != null) {
                    locations.put("execution", location);
                } else {
                    log.warn("Unable to determine location for " + pluginExecution);
                }
            }
        } else if (modelObject instanceof Dependency) {
            final Dependency dependency = (Dependency) modelObject;
            final List<Exclusion> exclusions = dependency.getExclusions();
            if (exclusions != null && !exclusions.isEmpty()) {
                final Exclusion exclusion = exclusions.get(0);
                final InputLocation location = exclusion.getLocation("");
                if (location != null) {
                    locations.put("exclusion", location);
                } else {
                    log.warn("Unable to determine location for " + exclusion);
                }
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
    private Map<Object, InputLocation> getLocations(final Object modelObject, final Class klass) {
        try {
            final Field field = klass.getDeclaredField("locations");
            field.setAccessible(true);

            final Map<Object, InputLocation> locations = new HashMap();
            final Map<Object, InputLocation> locationsFieldValue = (Map) field.get(modelObject);
            for (Map.Entry<Object, InputLocation> entry : locationsFieldValue.entrySet()) {
                if (entry.getValue() != null) {
                    locations.put(entry.getKey(), entry.getValue());
                } else {
                    log.warn("Unable to determine location for " + entry.getKey());
                }
            }
            return locations;
        } catch (final NoSuchFieldException e) {
            if (klass.getSuperclass() == null) {
                throw new IllegalArgumentException("No 'locations' field found on object of type " + klass, e);
            } else {
                return getLocations(modelObject, klass.getSuperclass());
            }
        } catch (final SecurityException e) {
            throw new IllegalArgumentException("Failed to get 'locations' field on object of type " + klass, e);
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to get 'locations' field on object of type " + klass, e);
        } catch (final IllegalAccessException e) {
            throw new IllegalArgumentException("Failed to get 'locations' field on object of type " + klass, e);
        }
    }

    public Map<String, Dependency> mapByManagementKey(final Collection<Dependency> dependencies) {
        final Map<String, Dependency> map = new HashMap<String, Dependency>();

        for (final Dependency dependency : dependencies) {
            map.put(dependency.getManagementKey(), dependency);
        }

        return map;
    }

    public Map<String, Plugin> mapById(final Collection<Plugin> dependencies) {
        final Map<String, Plugin> map = new HashMap<String, Plugin>();

        for (final Plugin Plugin : dependencies) {
            map.put(Plugin.getId(), Plugin);
        }

        return map;
    }

    public ObjectWithPath<ExtDependency> findInheritedDependency(final MavenProject mavenProject, final Dependency dependency) {
        final MavenProject parent = mavenProject.getParent();

        if (parent != null) {
            final Map<String, Dependency> dependencies = mapByManagementKey(expressionEvaluator.<Dependency>getPath(parent.getOriginalModel(), "dependencies"));
            final Map<String, Dependency> managedDependencies = mapByManagementKey(expressionEvaluator.<Dependency>getPath(parent.getOriginalModel(),
                                                                                                                           "dependencyManagement/dependencies"));

            final Dependency parentDependency = dependencies.get(dependency.getManagementKey());
            if (parentDependency != null) {
                return new ObjectWithPath<ExtDependency>(new ExtDependency(parent, parentDependency), parent, "dependencies");
            }

            final Dependency parentManagedDependency = managedDependencies.get(dependency.getManagementKey());
            if (parentManagedDependency != null) {
                return new ObjectWithPath<ExtDependency>(new ExtDependency(parent, parentManagedDependency), parent, "dependencyManagement/dependencies");
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
            final Map<String, Plugin> managedDependencies = mapById(expressionEvaluator.<Plugin>getPath(parent.getOriginalModel(),
                                                                                                        "build/pluginManagement/plugins"));

            final Plugin parentDependency = dependencies.get(plugin.getId());
            if (parentDependency != null) {
                inheritedPlugins.add(new ExtPlugin(parent, parentDependency));
            }

            final Plugin parentManagedDependency = managedDependencies.get(plugin.getId());
            if (parentManagedDependency != null) {
                inheritedPlugins.add(new ExtPlugin(parent, parentManagedDependency));
            }

            findInheritedPlugins(inheritedPlugins, parent, plugin);
        }
    }

    public Coordinates getCoordinates(final Object modelObject) {
        final String groupId = getGroupId(modelObject);
        final String artifactId = getArtifactId(modelObject);
        final String type = tryGetType(modelObject);
        final String version = getVersion(modelObject);

        return new Coordinates(groupId, artifactId, type, version);
    }

}
