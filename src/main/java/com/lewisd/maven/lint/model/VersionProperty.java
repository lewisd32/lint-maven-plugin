package com.lewisd.maven.lint.model;

import java.util.LinkedList;
import java.util.List;

public class VersionProperty {

	private final String originalVersion;

	public VersionProperty(String propertyValue) {
		this.originalVersion = propertyValue;
	}
	
	public String getOriginalVersion() {
		return originalVersion;
	}
	
	public List<String> getPropertyNames() {
		List<String> propertyNames = new LinkedList<String>();
		int index = 0;
		while ( (index = originalVersion.indexOf("${", index)) >= 0 ) {
			int endIndex = originalVersion.indexOf("}", index);
			if (endIndex >= 0) {
				String propertyName = originalVersion.substring(index + 2, endIndex);
				propertyNames.add(propertyName);
				index = endIndex;
			} else {
				break;
			}
		}
		return propertyNames;
	}
}