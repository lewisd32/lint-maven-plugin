package com.lewisd.maven.lint.model;

import java.util.Arrays;
import java.util.Collections;

import junit.framework.Assert;

import org.junit.Test;

public class VersionPropertyTest {

	@Test
	public void shouldReturnListForMultipleProperties() {
		final VersionProperty versionProperty = new VersionProperty("abc${property1}def${property2}${property3}ghi");
		
		Assert.assertEquals(Arrays.asList(new String[] { "property1", "property2", "property3" }), versionProperty.getPropertyNames());
	}

	@Test
	public void shouldReturnListForSinglePropertyWithTextAround() {
		final VersionProperty versionProperty = new VersionProperty("abc${property1}def");
		
		Assert.assertEquals(Arrays.asList(new String[] { "property1" }), versionProperty.getPropertyNames());
	}
	
	@Test
	public void shouldReturnListForSingleProperty() {
		final VersionProperty versionProperty = new VersionProperty("${property1}");
		
		Assert.assertEquals(Arrays.asList(new String[] { "property1" }), versionProperty.getPropertyNames());
	}
	
	@Test
	public void shouldReturnEmptyListWhenNoProperty() {
		final VersionProperty versionProperty = new VersionProperty("1.0.2");
		
		Assert.assertEquals(Collections.emptyList(), versionProperty.getPropertyNames());
	}
	
	@Test
	public void shouldReturnEmptyListWhenPropertyNotClosed() {
		final VersionProperty versionProperty = new VersionProperty("${property");
		
		Assert.assertEquals(Collections.emptyList(), versionProperty.getPropertyNames());
	}
	
	@Test
	public void shouldReturnListForWeirdness() {
		final VersionProperty versionProperty = new VersionProperty("${property1${property2}");
		
		Assert.assertEquals(Arrays.asList(new String[] { "property1${property2" }), versionProperty.getPropertyNames());
	}
	
}
