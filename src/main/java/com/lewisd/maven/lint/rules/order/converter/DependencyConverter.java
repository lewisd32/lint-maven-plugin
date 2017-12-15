package com.lewisd.maven.lint.rules.order.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.apache.maven.model.Dependency;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class DependencyConverter implements Converter {
    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final Dependency dependency = (Dependency) source;

        writer.startNode("dependency");

        writer.startNode("groupId");
        writer.setValue(dependency.getGroupId());
        writer.endNode();

        writer.startNode("artifactId");
        writer.setValue(dependency.getArtifactId());
        writer.endNode();

        if (isNotBlank(dependency.getVersion())) {
            writer.startNode("version");
            writer.setValue(dependency.getVersion());
            writer.endNode();
        }

        if (isNotBlank(dependency.getType()) && !dependency.getType().equals("jar")) {
            writer.startNode("type");
            writer.setValue(dependency.getType());
            writer.endNode();
        }

        if (isNotBlank(dependency.getScope())) {
            writer.startNode("scope");
            writer.setValue(dependency.getScope());
            writer.endNode();
        }

        if (isNotBlank(dependency.getClassifier())) {
            writer.startNode("classifier");
            writer.setValue(dependency.getClassifier());
            writer.endNode();
        }

        writer.endNode();
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader hierarchicalStreamReader, final UnmarshallingContext unmarshallingContext) {
        throw new UnsupportedOperationException("Reading is not supported.");
    }

    @Override
    public boolean canConvert(final Class aClass) {
        return Dependency.class.isAssignableFrom(aClass);
    }
}
