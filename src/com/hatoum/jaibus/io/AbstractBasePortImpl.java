package com.hatoum.jaibus.io;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBasePortImpl implements Port {
	
	protected List<MessageListener> messageListeners;
	
	public AbstractBasePortImpl() {
		messageListeners = new ArrayList<MessageListener>();
	}
	
	protected void notifyListeners(Message message) {
		for (MessageListener messageListener : messageListeners) {
			messageListener.newMessage(message);
		}
	}
	
	public void addMessageListener(MessageListener messageListener) {
		messageListeners.add(messageListener);
	}
}
