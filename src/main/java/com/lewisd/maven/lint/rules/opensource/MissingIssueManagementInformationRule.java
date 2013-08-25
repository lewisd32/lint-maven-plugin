package com.lewisd.maven.lint.rules.opensource;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.rules.AbstractRule;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.InputSource;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.project.MavenProject;

import java.util.Map;

public class MissingIssueManagementInformationRule extends AbstractRule {
    @Override
    public String getIdentifier() {
        return "OSSIssueManagementSectionRule";
    }

    @Override
    public String getDescription() {
        return "The users/developers need to know where to get active bugs and to report new ones to.";
    }

    @Override
    public void invoke(MavenProject mavenProject, Map<String, Object> models, ResultCollector resultCollector) {
        IssueManagement management = mavenProject.getIssueManagement();

        if (null == management) {
            final InputSource source = new InputSource();
            source.setLocation(mavenProject.getOriginalModel().getPomFile() + "");
            InputLocation location = new InputLocation(0, 0, source);
            resultCollector.addViolation(mavenProject, this, "missing <issueManagement/> section", location);
        } else {
            if (StringUtils.isEmpty(management.getSystem())) {
                resultCollector.addViolation(mavenProject, this, "missing <system/> entry in <issueManagement/> section", management.getLocation(""));
            }
            if (StringUtils.isEmpty(management.getUrl())) {
                resultCollector.addViolation(mavenProject, this, "missing <url/> entry in <issueManagement/> section", management.getLocation(""));
            }
        }
    }
}
