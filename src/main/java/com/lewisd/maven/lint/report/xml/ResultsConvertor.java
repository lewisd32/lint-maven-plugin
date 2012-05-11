package com.lewisd.maven.lint.report.xml;

import com.lewisd.maven.lint.Results;
import com.lewisd.maven.lint.Violation;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ResultsConvertor implements Converter {

	@SuppressWarnings("rawtypes")
	@Override
	public boolean canConvert(Class type) {
		return type.equals(Results.class);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		Results results = (Results) source;
		
		final String status;
		if (results.getViolations().isEmpty()) {
			status = "PASS";
		} else {
			status = "FAIL";
		}
		writer.addAttribute("status", status);
		writer.addAttribute("violations", Integer.toString(results.getViolations().size()));
		
		for (Violation violation : results.getViolations()) {
			context.convertAnother(violation);
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		throw new UnsupportedOperationException("Reading result files not supported.");
	}

}
