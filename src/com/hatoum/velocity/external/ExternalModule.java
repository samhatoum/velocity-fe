package com.hatoum.velocity.external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.hatoum.velocity.Utilities;

public class ExternalModule {

	private static Logger logger = Logger.getLogger(External.class.getName());

	private BufferedReader in;

	private PrintWriter out;

	private boolean isConnected;

	private ExternalAccessBean accessBean;

	public ExternalModule(Socket socket, ExternalAccessBean accessBean) {
		this.accessBean = accessBean;
		try {
			in = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			logger.severe(Utilities.getLog(e));
		}
		monitorInput();
		isConnected = true;
	}

	public void monitorInput() {
		new Thread() {
			public void run() {
				logger.log(Level.INFO, "monitoring stream");
				try {
					// read the incoming stream
					String line;
					while ((line = in.readLine()) != null) {

						if (line.indexOf("=") == -1) {
							continue;
						}

						// split at the equals
						String[] splitLine = line.split("=");
						String value = null;
						if (splitLine.length > 2) {
							logger
									.warning("found more than one = sign in the"
											+ " com.hatoum.velocity.external plugin field set "
											+ "request. ignoring line: ["
											+ line + "]");
						} else if (splitLine.length == 1) {
							value = "";
						} else {
							value = splitLine[1];
						}

						// set the property in the access bean
						accessBean.setProperty(splitLine[0], value);
					}
				} catch (IOException e) {
					// ignore closed socket exceptions
					if (e.getMessage().toUpperCase().indexOf("SOCKET CLOSED") == -1) {
						logger.log(Level.INFO,
								"Client caused a read error and has been disconnected.\n"
										+ Utilities.getLog(e));
					}
				} finally {
					// FIXME figure out this logic
					kill();
				}
			}
		}.start();
	}

	public void kill() {
		try {
			in.close();
		} catch (IOException e) {
			logger.severe(Utilities.getLog(e));
		}
		out.close();
		isConnected = false;
		accessBean = null;
	}

	public void send(String string) {
		logger.info("sending " + string + " to External");
		out.write(string + "\n");
		out.flush();
	}

	public boolean isConnected() {
		return isConnected;
	}
}
