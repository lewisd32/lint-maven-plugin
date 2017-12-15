/*
 * The copyright of this file belongs to Feedzai. The file cannot be
 * reproduced in whole or in part, stored in a retrieval system,
 * transmitted in any form, or by any means electronic, mechanical,
 * photocopying, or otherwise, without the prior permission of the owner.
 *
 * © 2017 Feedzai, Strictly Confidential
 */
package com.lewisd.maven.lint.rules.basic;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.rules.AbstractRule;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.project.MavenProject;

import java.util.List;
import java.util.Map;

public class PluginVersioningByPropertiesRule extends AbstractRule {
    @Override
    public void invoke(final MavenProject mavenProject, final Map<String, Object> models, final ResultCollector resultCollector) {
        validateBuild(mavenProject, resultCollector, mavenProject.getOriginalModel().getBuild());
    }

    private void validateBuild(final MavenProject mavenProject, final ResultCollector resultCollector, final BuildBase build) {
        if (build != null) {
            final PluginManagement pluginManagement = build.getPluginManagement();

            if (pluginManagement != null) {
                validateDependencies(mavenProject, resultCollector, pluginManagement.getPlugins());
            }

            validateDependencies(mavenProject, resultCollector, build.getPlugins());
        }
    }

    private void validateDependencies(final MavenProject mavenProject, final ResultCollector resultCollector, final List<Plugin> plugins) {
        for (final Plugin plugin : plugins) {
            final String version = plugin.getVersion();
            if (version != null && !version.startsWith("${")) {
                resultCollector.addViolation(mavenProject, this, String.format("The plugin '%s' is not using a version property.", plugin.getArtifactId()), plugin
                        .getLocation("version"));
            }
        }
    }

    @Override
    public String getIdentifier() {
        return "PluginVersioningByProperties";
    }

    @Override
    public String getDescription() {
        return "";
    }
}
