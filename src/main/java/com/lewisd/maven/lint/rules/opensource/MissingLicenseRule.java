package com.lewisd.maven.lint.rules.opensource;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.rules.AbstractRule;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.InputSource;
import org.apache.maven.model.License;
import org.apache.maven.project.MavenProject;

import java.util.List;
import java.util.Map;

public class MissingLicenseRule extends AbstractRule {
    @Override
    public String getIdentifier() {
        return "OSSLicenseSectionRule";
    }

    @Override
    public String getDescription() {
        return "Each project should be licensed under a specific license so the terms of usage are clear.";
    }

    @Override
    public void invoke(MavenProject mavenProject, Map<String, Object> models, ResultCollector resultCollector) {
        final List<License> licenses = mavenProject.getLicenses();

        if (licenses.isEmpty()) {
            InputLocation location = mavenProject.getOriginalModel().getLocation("licences");
            resultCollector.addViolation(mavenProject, this, "missing <licenses/> information", location == null ? getEmptyLocation(mavenProject) : location);
        } else {
            for (License license : licenses) {
                if (StringUtils.isEmpty(license.getName())) {
                    resultCollector.addViolation(mavenProject, this, "missing <name> in <license/> information", license.getLocation(""));
                }
                if (StringUtils.isEmpty(license.getUrl())) {
                    resultCollector.addViolation(mavenProject, this, "missing <url> in <license/> information", license.getLocation(""));
                }
            }
        }
    }

    private InputLocation getEmptyLocation(MavenProject mavenProject) {
        final InputSource source = new InputSource();
        source.setLocation(mavenProject.getOriginalModel().getPomFile() + "");
        return new InputLocation(0, 0, source);
    }
}
