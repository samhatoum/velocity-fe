package com.hatoum.jaibus.io;

public class TimeFrame implements Message {

	public static final TimeFrame IGNORE = new TimeFrame(-1);

	private long delay;

	private static int variance;

	public long getDelay() {
		return delay;
	}

	public TimeFrame(long delay) {
		this.delay = delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public static void setVariance(int variance) {
		TimeFrame.variance = variance;
	}

	public boolean withinTimeFrame(long startTime, long endTime) {
		if (endTime - startTime < delay + variance) {
			return true;
		} else {
			return false;
		}
	}

	public boolean equals(Message message) {

		if (!(message instanceof TimeFrame)) {
			return false;
		}

		if (this == IGNORE) {
			return true;
		}

		TimeFrame timeFrame = (TimeFrame) message;

		// check for equality of the delays, give or take the variance
		if (delay > timeFrame.getDelay() - variance
				&& delay < timeFrame.getDelay() + variance) {
			return true;
		}

		return false;
	}

	/* The methods below intentially don't do anything */

	public Device getDestinationDevice() {
		return null;
	}

	public String getMessageContent() {
		return "[" + delay + "]";
	}

	public Device getSourceDevice() {
		return null;
	}

	public void setDestinationDevice(Device destinationDevice) {
	}

	public Message setMessageContent(String messageContent) {
		return this;
	}

	public void setSourceDevice(Device sourceDevice) {
	}
}
