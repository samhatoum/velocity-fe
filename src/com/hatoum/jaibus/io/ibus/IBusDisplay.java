package com.hatoum.jaibus.io.ibus;

public class IBusDisplay {

	IBusMessage displayMessage = new IBusMessage(IBusDevice.CD_PLAYER,
			IBusDevice.MULTI_INFORMATION_DISPLAY_BUTTONS, null);

	private byte[] message;

	public void send(Object object) {
		if (!(object instanceof String)) {
			throw new IllegalArgumentException(
					"KeyboxDisplay must be provided with an IBusDisplayObject");
		}
		convert((String) object);
	}

	private void convert(String string) {
		byte[] stringBytes = string.getBytes();
		// TODO convert this message's bytes to the correct ibus format
		message = new byte[stringBytes.length];
		for (int i = 0; i < string.length(); i++) {
			message[i] = stringBytes[i]; // TODO do some operation
		}
	}

}
