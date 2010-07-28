package com.hatoum.jaibus.io;

public interface Message {

	public Device getSourceDevice();

	public void setSourceDevice(Device sourceDevice);

	public Device getDestinationDevice();

	public void setDestinationDevice(Device destinationDevice);

	public String getMessageContent();

	public Message setMessageContent(String messageContent);
	
	public boolean equals(Message message);
}
