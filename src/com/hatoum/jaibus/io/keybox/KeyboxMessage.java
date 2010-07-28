package com.hatoum.jaibus.io.keybox;

import com.hatoum.jaibus.io.Device;
import com.hatoum.jaibus.io.Message;

public class KeyboxMessage implements Message {

	String content;

	public Device getDestinationDevice() {
		// do nothing
		return null;
	}

	public void setDestinationDevice(Device destinationDevice) {
		// do nothing
	}

	public Device getSourceDevice() {
		// do nothing
		return null;
	}

	public void setSourceDevice(Device sourceDevice) {
		// do nothing
	}

	public Message setMessageContent(String messageContent) {
		content = messageContent;
		return this;
	}

	public String getMessageContent() {
		return content;
	}

	public boolean equals(Message message) {
		if (!(message instanceof KeyboxMessage)) {
			return false;
		}
		return content.equals(message.getMessageContent());
	}

	public String toString() {
		return content;
	}
}
