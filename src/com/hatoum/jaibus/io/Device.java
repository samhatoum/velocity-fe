package com.hatoum.jaibus.io;

public interface Device {

	public Number getID();
	
	public String getName();
	
	public boolean validate(Number id);
}
