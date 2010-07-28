package com.hatoum.jaibus.io.ibus;

import gnu.trove.TByteArrayList;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;

import com.hatoum.jaibus.io.AbstractBasePortImpl;
import com.hatoum.jaibus.io.Message;
import com.hatoum.jaibus.io.MessageListener;

public class IBusPort extends AbstractBasePortImpl implements SerialPortEventListener{

	private static final String IBUS_APP_NAME = "Jaibus";

	private static final int IBUS_BAUD = 9600;

	private static final int IBUS_DATA_BITS = SerialPort.DATABITS_8;

	private static final int IBUS_STOP_BITS = SerialPort.STOPBITS_1;

	private static final int IBUS_PARITY = SerialPort.PARITY_EVEN;

	private static final int IBUS_FLOW_CONTROL = SerialPort.FLOWCONTROL_NONE;

	private static final int IBUS_INPUT_BUFFER_SIZE = 512;

	private static final int IBUS_OUTPUT_BUFFER_SIZE = 256;

	private static final int IBUS_RECEIVE_THRESHOLD = 1;

	private static final int IBUS_TIMEOUT = 1000;

	private SerialPort sPort;

	private String portName;

	private InputStream is;

	private OutputStream os;

	private List<TByteArrayList> searches;

	public IBusPort(String portName) {
		this.portName = portName;

		searches = new ArrayList<TByteArrayList>();
		messageListeners = new ArrayList<MessageListener>();
	}

	private void log(Object o) {
		log(o.toString());
	}

	private void log(String message) {
		System.err.println(message);
	}

	public boolean open() {
		CommPortIdentifier portId;

		try {
			portId = CommPortIdentifier.getPortIdentifier(portName);

			sPort = (SerialPort) portId.open(IBUS_APP_NAME, IBUS_TIMEOUT);

			sPort.setSerialPortParams(IBUS_BAUD, IBUS_DATA_BITS,
					IBUS_STOP_BITS, IBUS_PARITY);

			sPort.enableReceiveTimeout(IBUS_TIMEOUT);

			sPort.enableReceiveThreshold(IBUS_RECEIVE_THRESHOLD);

			sPort.setFlowControlMode(IBUS_FLOW_CONTROL);

			sPort.setInputBufferSize(IBUS_INPUT_BUFFER_SIZE);
			is = sPort.getInputStream();

			sPort.setOutputBufferSize(IBUS_OUTPUT_BUFFER_SIZE);
			os = sPort.getOutputStream();

			sPort.addEventListener(this);

		} catch (Exception e) {
			log(e.getStackTrace());
			return false;
		}
		return true;
	}

	public void close() {
		if (sPort != null) {
			try {
				os.close();
				is.close();
			} catch (IOException e) {
				log(e);
			}
			sPort.close();
		}
	}

	public OutputStream getOutputStream() {
		return os;
	}

	public InputStream getInputStream() {
		return is;
	}

	public void serialEvent(SerialPortEvent serialEvent) {
		int eventType = serialEvent.getEventType();

		switch (eventType) {
		// case SerialPortEvent.BI: break;
		// case SerialPortEvent.CD: break;
		// case SerialPortEvent.FE: break;
		// case SerialPortEvent.OE: break;
		// case SerialPortEvent.PE: break;
		// case SerialPortEvent.RI: break;
		// case SerialPortEvent.CTS: break;
		// case SerialPortEvent.DSR: break;
		// case SerialPortEvent.OUTPUT_BUFFER_EMPTY: break;
		case SerialPortEvent.DATA_AVAILABLE:
			parseChar();
			break;
		}
	}

	private void parseChar() {
		try {
			byte b = (byte) is.read();

			// for all active searches {
			for (TByteArrayList search : searches) {

				// every search should have at least 1 byte
				if (search.size() == 0) {
					throw new RuntimeException(
							"Search too short. Should not happen");
				}

				// a search should never get to max length
				else if (search.size() > IBusMessage.MAX_MESSAGE_LENGTH) {
					throw new RuntimeException(
							"Search too long. Should not happen");
				}

				// else if new search, validate destination byte and store it
				else if (search.size() == IBusMessage.DESTINATION_POS) {
					if (!validDevice(b)) {
						// retire this search
						searches.remove(search);
					}
					// add the current byte to the search
					search.add(b);
				}

				// if here, we have source and destination so read length byte
				else if (search.size() == IBusMessage.LENGTH_POS) {
					// add the current byte to the search
					search.add(b);
				}

				// continue to read bytes for this search up to the length
				else if (search.size() >= IBusMessage.MESSAGE_POS
						&& search.size() < search.get(IBusMessage.LENGTH_POS) + 2) {
					// add the current byte to the search
					search.add(b);
				}

				// once the length is read, validate the checksum
				else if (search.size() == search.get(IBusMessage.LENGTH_POS) + 2) {
					// if checksum is correct
					if (validChecksum()) {
						Message iBusMessage = new IBusMessage(search
								.toNativeArray());
						// retire all searches
						searches.clear();
						// and fire event
						notifyListeners(iBusMessage);
						break;
					} else {
						// otherwise retire this search
						searches.remove(search);
					}
				}
			}
		} catch (IOException e) {
			// retire all searches
			searches.clear();
			// fire warning event
			notifyListeners(IBusMessage.WARNING_MESSAGE);
			log(e);
		}
	}

	private boolean validChecksum() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean validDevice(byte b) {
		// TODO Auto-generated method stub
		return false;
	}

	public void send(Object deviceSpecificDesplayObject) {
		// TODO Auto-generated method stub
		
	}
}