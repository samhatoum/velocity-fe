package com.hatoum.jaibus;


import java.util.Enumeration;

import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;

public class SerialTest implements SerialPortEventListener {

	private static final String IBUS_APP_NAME = "Jaibus";

	private static final int BAUD = 4800;

	private static final int DATA_BITS = SerialPort.DATABITS_8;

	private static final int STOP_BITS = SerialPort.STOPBITS_1;

	private static final int PARITY = SerialPort.PARITY_EVEN;

	private static final int FLOW_CONTROL = SerialPort.FLOWCONTROL_NONE;

	private static final int INPUT_BUFFER_SIZE = 512;

	private static final int OUTPUT_BUFFER_SIZE = 256;

	private static final int RECEIVE_THRESHOLD = 1;

	private static final int TIMEOUT = 1000;

	private SerialPort sPort;

	public static void main(String[] args) throws InterruptedException {
		new SerialTest().open("COM1");
		/*
		 * int n = 0; while (true) { System.err.println("cycle " + ++n);
		 * Thread.sleep(1000); }
		 */
	}

	public boolean open(String portName) {
		
		Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();

		if (portIdentifiers.hasMoreElements()) {
			while (portIdentifiers.hasMoreElements()) {
				System.err.println(portIdentifiers.nextElement());
			}
		} else {
			System.err.println("No ports found");
		}

		try {
			CommPortIdentifier portId = CommPortIdentifier
					.getPortIdentifier(portName);

			sPort = (SerialPort) portId.open(IBUS_APP_NAME, TIMEOUT);

			sPort.setSerialPortParams(BAUD, DATA_BITS, STOP_BITS, PARITY);

			sPort.enableReceiveTimeout(TIMEOUT);

			sPort.enableReceiveThreshold(RECEIVE_THRESHOLD);

			sPort.setFlowControlMode(FLOW_CONTROL);

			sPort.setInputBufferSize(INPUT_BUFFER_SIZE);

			sPort.setOutputBufferSize(OUTPUT_BUFFER_SIZE);

			sPort.addEventListener(this);

		} catch (Exception e) {
			System.err.println(e.getStackTrace());
			return false;
		}
		return true;
	}

	public void serialEvent(SerialPortEvent arg0) {
		System.err.println(arg0);
	}
}