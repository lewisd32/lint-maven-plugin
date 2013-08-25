package com.lewisd.maven.lint.rules.opensource;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.rules.AbstractRule;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.InputSource;
import org.apache.maven.project.MavenProject;

import java.util.Map;
import java.util.Set;

public class MissingInceptionYearRule extends AbstractRule {

    public MissingInceptionYearRule() {
        super(null, null);
    }

    @Override
    protected void addRequiredModels(Set<String> requiredModels) {
    }

    @Override
    public String getIdentifier() {
        return "OSSInceptionYearRule";
    }

    @Override
    public String getDescription() {
        return "For better understanding the project the inception year of your project is required.";
    }

    @Override
    public void invoke(MavenProject mavenProject, Map<String, Object> models, ResultCollector resultCollector) {
        final String inceptionYear = mavenProject.getInceptionYear();

        if (StringUtils.isEmpty(inceptionYear)) {
            resultCollector.addViolation(mavenProject, this, "missing <inceptionYear/> information", getEmptyLocation(mavenProject));
        }else if (!inceptionYear.matches("\\d{4}")){
            InputLocation location = mavenProject.getOriginalModel().getLocation("inceptionYear");
            resultCollector.addViolation(mavenProject, this, "format of <inceptionYear/> information is wrong, only 4 digits allowed", location);
        }
    }

    private InputLocation getEmptyLocation(MavenProject mavenProject) {
        final InputSource source = new InputSource();
        source.setLocation(mavenProject.getOriginalModel().getPomFile() + "");
        return new InputLocation(0, 0, source);
    }
}
