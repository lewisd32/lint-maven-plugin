package com.lewisd.maven.lint.rules.opensource;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.rules.AbstractRule;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.maven.model.Developer;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.InputSource;
import org.apache.maven.project.MavenProject;

import java.util.List;
import java.util.Map;

public class MissingDeveloperInformationRule extends AbstractRule {
    @Override
    public String getIdentifier() {
        return "OSSDevelopersSectionRule";
    }

    @Override
    public String getDescription() {
        return "The users/developers need to know where to get active bugs and to report new ones to.";
    }

    @Override
    public void invoke(MavenProject mavenProject, Map<String, Object> models, ResultCollector resultCollector) {
        List<Developer> developers = mavenProject.getDevelopers();

        if (developers.isEmpty()) {
            InputLocation location = getEmptyLocation(mavenProject);
            resultCollector.addViolation(mavenProject, this, "missing <developers/> section", location);
        } else {
            for (Developer developer : developers) {
                if (StringUtils.isEmpty(developer.getId())) {
                    resultCollector.addViolation(mavenProject, this, "missing <id/> entry in <developer/> section", developer.getLocation(""));
                }
                if (StringUtils.isEmpty(developer.getName())) {
                    resultCollector.addViolation(mavenProject, this, "missing <name/> entry in <developer/> section", developer.getLocation(""));
                }
                if (!StringUtils.isEmpty(developer.getEmail()) && !EmailValidator.getInstance().isValid(developer.getEmail())) {
                    resultCollector.addViolation(mavenProject, this, "not valid <email/> entry in <developer/> section", developer.getLocation(""));
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
