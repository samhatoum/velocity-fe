package com.hatoum.jaibus.io.keybox;

import com.hatoum.jaibus.io.Device;

public class KeyboxDevice implements Device {

	public boolean validate(Number id) {
		return false;
	}

	public String getName() {
		return this.toString();
	}

	public Number getID() {
		return null;
	}
}