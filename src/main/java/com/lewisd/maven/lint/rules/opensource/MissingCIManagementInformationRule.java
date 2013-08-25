package com.lewisd.maven.lint.rules.opensource;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.rules.AbstractRule;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.CiManagement;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.InputSource;
import org.apache.maven.project.MavenProject;

import java.util.Map;

public class MissingCIManagementInformationRule extends AbstractRule {
    @Override
    public String getIdentifier() {
        return "OSSContinuousIntegrationManagementSectionRule";
    }

    @Override
    public String getDescription() {
        return "For better understanding the project a link to the used integration system helps users to trust.";
    }

    @Override
    public void invoke(MavenProject mavenProject, Map<String, Object> models, ResultCollector resultCollector) {
        CiManagement management = mavenProject.getCiManagement();

        if (null == management) {
            final InputSource source = new InputSource();
            source.setLocation(mavenProject.getOriginalModel().getPomFile() + "");
            InputLocation location = new InputLocation(0, 0, source);
            resultCollector.addViolation(mavenProject, this, "missing <ciManagement/> section", location);
        } else {
            if (StringUtils.isEmpty(management.getSystem())) {
                resultCollector.addViolation(mavenProject, this, "missing <system/> entry in <ciManagement/> section", management.getLocation(""));
            }
            if (StringUtils.isEmpty(management.getUrl())) {
                resultCollector.addViolation(mavenProject, this, "missing <url/> entry in <ciManagement/> section", management.getLocation(""));
            }
        }
    }
}
