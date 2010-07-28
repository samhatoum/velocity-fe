package com.hatoum.jaibus.io;

public interface Port {
	
	public void addMessageListener(MessageListener messageListener);
	
	public void send(Object deviceSpecificDesplayObject);
}
