package com.hatoum.jaibus.io.ibus;

import java.util.Arrays;

import com.hatoum.jaibus.io.Device;
import com.hatoum.jaibus.io.Message;

public class IBusMessage implements Message {

	static final int SOURCE_POS = 0;

	static final int DESTINATION_POS = 1;

	static final int LENGTH_POS = 2;

	static final int MESSAGE_POS = 3;

	private static final int PREPARED_MESSAGE_INDEX = 4;

	static final int MAX_MESSAGE_LENGTH = 255;

	public static final IBusMessage WARNING_MESSAGE = new IBusMessage();
	static {
		WARNING_MESSAGE.message = "INTERNAL WARNING MESSAGE".getBytes();
	}

	private Device sourceDevice;

	private Device destinationDevice;

	private byte[] message;

	private byte length;

	private byte xOrChecksum;

	private IBusMessage() {
	}

	public IBusMessage(Device sourceDevice, Device destinationDevice,
			byte[] message) {
		this.sourceDevice = sourceDevice;
		this.destinationDevice = destinationDevice;
		this.message = message;
		calculateLength();
		calculateChecksum();
	}

	public IBusMessage(byte[] rawMessage) {
		decode(rawMessage);
	}

	private void decode(byte[] rawMessage) {
		sourceDevice = IBusDevice.getDeviceByID(rawMessage[SOURCE_POS]);
		destinationDevice = IBusDevice
				.getDeviceByID(rawMessage[DESTINATION_POS]);
		length = rawMessage[LENGTH_POS];
		Byte[] bytes = null;
		Arrays.asList(rawMessage[LENGTH_POS]).subList(MESSAGE_POS,
				rawMessage.length - 2).toArray(bytes);
		for (int i = 0; i < bytes.length; i++) {
			message[i] = bytes[i];
		}

		// rawMessage;
		// checksum = rawMessage[rawMessage.length-1];
	}

	public Device getSourceDevice() {
		return sourceDevice;
	}

	public void setSourceDevice(Device sourceDevice) {
		this.sourceDevice = sourceDevice;
	}

	public Device getDestinationDevice() {
		return destinationDevice;
	}

	public String getMessageContent() {
		return new String(message);
	}

	public void setDestinationDevice(Device destinationDevice) {
		this.destinationDevice = destinationDevice;
		prepareMessage();
	}

	public Message setMessageContent(String messageContent) {
		message = messageContent.getBytes();
		return this;
	}

	public byte[] getPreparedMessage() {
		prepareMessage();
		byte[] toSend = new byte[PREPARED_MESSAGE_INDEX + message.length];
		toSend[SOURCE_POS] = sourceDevice.getID().byteValue();
		toSend[DESTINATION_POS] = destinationDevice.getID().byteValue();
		toSend[LENGTH_POS] = length;
		for (int eachByte = 0; eachByte < message.length; eachByte++)
			toSend[eachByte + MESSAGE_POS] = message[eachByte];
		toSend[toSend.length - 1] = xOrChecksum;
		return toSend;
	}

	private void prepareMessage() {
		calculateLength();
		calculateChecksum();
	}

	private void calculateChecksum() {
		xOrChecksum = 0;
		if (xOrChecksum == 0) {
		}
	}

	private void calculateLength() {
		if (sourceDevice != null && destinationDevice != null) {
			length = new Integer(message.length + 2).byteValue();
		}
	}

	public static IBusMessage parse(String eachMessage) {
		IBusMessage iBusMessage = new IBusMessage();
		// TODO
		return iBusMessage;
	}

	public boolean equals(Message message) {
		return false;
	}
}
