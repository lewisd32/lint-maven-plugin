package com.lewisd.maven.lint.rules.opensource;

import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.rules.AbstractRule;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.InputSource;
import org.apache.maven.project.MavenProject;

import java.util.Map;

public class MissingUrlRule extends AbstractRule {
    @Override
    public String getIdentifier() {
        return "OSSUrlSectionRule";
    }

    @Override
    public String getDescription() {
        return "For better understanding the project a link to your project is required.";
    }

    @Override
    public void invoke(MavenProject mavenProject, Map<String, Object> models, ResultCollector resultCollector) {
        final String url = mavenProject.getUrl();

        if (StringUtils.isEmpty(url)) {
            resultCollector.addViolation(mavenProject, this, "missing <url/> information", getEmptyLocation(mavenProject));
        }
    }

    private InputLocation getEmptyLocation(MavenProject mavenProject) {
        final InputSource source = new InputSource();
        source.setLocation(mavenProject.getOriginalModel().getPomFile() + "");
        return new InputLocation(0, 0, source);
    }
}
