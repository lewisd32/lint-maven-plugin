/*
 * The copyright of this file belongs to Feedzai. The file cannot be
 * reproduced in whole or in part, stored in a retrieval system,
 * transmitted in any form, or by any means electronic, mechanical,
 * photocopying, or otherwise, without the prior permission of the owner.
 *
 * © 2017 Feedzai, Strictly Confidential
 */
package com.lewisd.maven.lint.rules.order.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;

public class DependencyManagementConverter implements Converter {
    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final DependencyManagement dependencyManagement = (DependencyManagement) source;

        writer.startNode("dependencies");

        for (final Dependency dependency : dependencyManagement.getDependencies()) {
            context.convertAnother(dependency);
        }

        writer.endNode();
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader hierarchicalStreamReader, final UnmarshallingContext unmarshallingContext) {
        throw new UnsupportedOperationException("Reading is not supported.");
    }

    @Override
    public boolean canConvert(final Class aClass) {
        return DependencyManagement.class.isAssignableFrom(aClass);
    }
}
