package com.hatoum.velocity.external;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hatoum.velocity.framework.AccessBeanBase;

public class ExternalAccessBean extends AccessBeanBase {

	private Map<String, String> fields;

	private long lastUpdated;

	public ExternalAccessBean() {
		lastUpdated = System.currentTimeMillis();
		fields = Collections.synchronizedMap(new HashMap<String, String>());
	}

	public void setProperty(String property, String value) {
		lastUpdated = System.currentTimeMillis();
		fields.put(property, value);
	}

	public String getProperty(String property) {
		return fields.get(property);
	}

	public String toString() {
		String build = new String();
		for (String key : fields.keySet()) {
			build += "[" + key + "=" + fields.get(key) + "]";
		}
		return build;
	}

	public long getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(long lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Map<String, String> getFields() {
		return fields;
	}

	public void setFields(Map<String, String> fields) {
		this.fields = fields;
	}

	public void reset() {
		fields.clear();
		lastUpdated = -1;
	}

	public List<String> getExcludesList() {
		// TODO Auto-generated method stub
		return null;
	}
}
