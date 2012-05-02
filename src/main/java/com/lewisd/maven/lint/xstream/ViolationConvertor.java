package com.lewisd.maven.lint.xstream;

import org.apache.maven.model.InputLocation;

import com.lewisd.maven.lint.Violation;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ViolationConvertor implements Converter {

	@Override
	public boolean canConvert(Class type) {
		return type.equals(Violation.class);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		Violation violation = (Violation) source;
		
		writer.startNode("violation");
		
		writer.addAttribute("rule", violation.getRule().getIdentifier());
		
		writer.startNode("message");
		writer.setValue(violation.getMessage());
		writer.endNode();
		
		final InputLocation location = violation.getInputLocation();
		
		writer.startNode("location");
		writer.addAttribute("file", location.getSource().getLocation());
		writer.addAttribute("line", Integer.toString(location.getLineNumber()));
		writer.addAttribute("column", Integer.toString(location.getColumnNumber()));
		writer.endNode();
		
		writer.endNode();
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		throw new UnsupportedOperationException("Reading result files not supported.");
	}

}
