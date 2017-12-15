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
import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;

import java.util.Map;

public class VersioningOnlyInPluginManagementRule extends AbstractRule {
    @Override
    public void invoke(final MavenProject mavenProject, final Map<String, Object> models, final ResultCollector resultCollector) {
        if (mavenProject.getOriginalModel().getBuild() != null) {
            validatePlugins(mavenProject, resultCollector, mavenProject.getOriginalModel().getBuild());
        }
        for (final Profile profile : mavenProject.getOriginalModel().getProfiles()) {
            validatePlugins(mavenProject, resultCollector, profile.getBuild());
        }
    }

    private void validatePlugins(final MavenProject mavenProject, final ResultCollector resultCollector, BuildBase build) {
        if (build == null) {
            return;
        }
        for (final Plugin plugin : build.getPlugins()) {
            if (StringUtils.isNotBlank(plugin.getVersion())) {
                resultCollector.addViolation(mavenProject, this, "Versioning should be in the plugin management.", plugin.getLocation("version"));
            }
        }
    }

    @Override
    public String getIdentifier() {
        return "VersioningOnlyInPluginManagement";
    }

    @Override
    public String getDescription() {
        return "";
    }
}
