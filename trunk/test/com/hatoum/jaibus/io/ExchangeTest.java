package com.hatoum.jaibus.io;

import junit.framework.TestCase;

public class ExchangeTest extends TestCase {

	Exchange exchange;

	private Port port = new AbstractBasePortImpl() {
		public void excersise(Message message) {
			notifyListeners(message);
		}
		public void send(Object deviceSpecificDesplayObject) {
			// do nothing
		}
	};

	private Message message = new Message() {

		public boolean equals(Message message) {
			return false;
		}

		public Device getDestinationDevice() {
			return null;
		}

		public String getMessageContent() {
			return null;
		}

		public Device getSourceDevice() {
			return null;
		}

		public void setDestinationDevice(Device destinationDevice) {
		}

		public Message setMessageContent(String messageContent) {
			return null;
		}

		public void setSourceDevice(Device sourceDevice) {
		}
	};

	@Override
	protected void setUp() throws Exception {
		exchange = new Exchange(port);
	}

	@Override
	protected void tearDown() throws Exception {

	}

	public void timlessMessageTest() {

	}

	public void timedMessageTest() {

	}

}
