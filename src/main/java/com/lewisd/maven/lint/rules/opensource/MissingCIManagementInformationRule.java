package com.lewisd.maven.lint.rules.opensource;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.rules.AbstractRule;
import com.lewisd.maven.lint.util.ModelUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.CiManagement;
import org.apache.maven.model.InputLocation;
import org.apache.maven.project.MavenProject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Set;

public class MissingCIManagementInformationRule extends AbstractRule {

    @Autowired
    public MissingCIManagementInformationRule(ModelUtil modelUtil) {
        super(null, modelUtil);
    }

    @Override
    protected void addRequiredModels(Set<String> requiredModels) {
    }

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
            InputLocation location = new InputLocation(0, 0);
            resultCollector.addViolation(mavenProject, this, "missing <ciManagement/> section", location);
        } else if (StringUtils.isEmpty(management.getSystem())) {
            resultCollector.addViolation(mavenProject, this, "mssing <ciManagement/> section", management.getLocation(""));
        }

    }
}
