package com.lewisd.maven.lint.rules.basic;

import com.google.common.collect.Sets;
import com.lewisd.maven.lint.ResultCollector;
import com.lewisd.maven.lint.rules.AbstractRule;
import com.lewisd.maven.lint.util.ExpressionEvaluator;
import com.lewisd.maven.lint.util.ModelUtil;
import org.apache.commons.jxpath.JXPathBeanInfo;
import org.apache.commons.jxpath.JXPathIntrospector;
import org.apache.commons.jxpath.util.ValueUtils;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;
import org.springframework.beans.factory.annotation.Autowired;

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class ProfileMustOnlyAddModulesRule extends AbstractRule {

    private String patternString = "with-.*";
    private Pattern pattern = Pattern.compile(patternString);
    private final Set<String> allowableDescriptors = Sets.newHashSet("id", "activation", "modules", "source");

    @Autowired
    public ProfileMustOnlyAddModulesRule(final ExpressionEvaluator expressionEvaluator, final ModelUtil modelUtil) {
        super(expressionEvaluator, modelUtil);
    }

    public void setPattern(final String patternString) {
        this.patternString = patternString;
        pattern = Pattern.compile(patternString);
    }

    @Override
    public String getIdentifier() {
        return "ProfileOnlyAddModules";
    }

    @Override
    public String getDescription() {
        return "Profiles who's ids match the pattern " + patternString + " must only add modules to the reactor.";
    }

    @Override
    public void invoke(final MavenProject mavenProject, final Map<String, Object> models, final ResultCollector resultCollector) {
        final Model originalModel = mavenProject.getOriginalModel();
        final Collection<Profile> profiles = getExpressionEvaluator().getPath(originalModel, "/profiles");
        for (final Profile profile : profiles) {
            if (profile.getId() != null && pattern.matcher(profile.getId()).matches()) {
                final JXPathBeanInfo profileBeanInfo = JXPathIntrospector.getBeanInfo(Profile.class);
                final PropertyDescriptor[] propertyDescriptors = profileBeanInfo.getPropertyDescriptors();
                final List<String> disallowedDescriptors = new LinkedList<String>();
                for (final PropertyDescriptor descriptor : propertyDescriptors) {
                    if (!allowableDescriptors.contains(descriptor.getName())) {
                        final Object value = ValueUtils.getValue(profile, descriptor);
                        if (value != null) {
                            if (value instanceof Collection) {
                                if (!((Collection<?>) value).isEmpty()) {
                                    disallowedDescriptors.add(descriptor.getName());
                                }
                            } else if (value instanceof Map) {
                                if (!((Map<?, ?>) value).isEmpty()) {
                                    disallowedDescriptors.add(descriptor.getName());
                                }
                            } else {
                                disallowedDescriptors.add(descriptor.getName());
                            }
                        }
                    }
                }
                if (!disallowedDescriptors.isEmpty()) {
                    final InputLocation location = getModelUtil().getLocation(profile, "");
                    resultCollector.addViolation(mavenProject, this, "Found '" + disallowedDescriptors + "' where only submodules are allowed", location);
                }
            }
        }
    }

}
