package com.hatoum.velocity.framework;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public abstract class AccessBeanBase implements IAccessBean {

	protected static Collection<String> allProperties = Collections
			.synchronizedSet(new HashSet<String>());

	private final static String[] DEFAULT_EXCLUDES = new String[] { "excludes",
			"allProperties" };

	protected Collection<String> excludeSet = Collections
			.synchronizedSet(new HashSet<String>());

	private static final String[] EMPTY_STRING_ARRAY = new String[] {};

	public final String[] getExcludes() {

		if (excludeSet == null || excludeSet.isEmpty()) {
			return DEFAULT_EXCLUDES;
		}

		for (int i = 0; i < DEFAULT_EXCLUDES.length; i++) {
			excludeSet.add(DEFAULT_EXCLUDES[i]);
		}

		String[] exludesArray = excludeSet.toArray(EMPTY_STRING_ARRAY);

		return exludesArray;
	}

	public void include(String property) {
		excludeSet.remove(property);
	}

	public void exclude(String property) {
		excludeSet.add(property);
	}

	public void includeAll() {
		excludeSet.clear();
	}

	public void excludeAll() {
		excludeSet.addAll(getAllProperties());
	}

	public Collection<String> getAllProperties() {
		return allProperties;
	}
}