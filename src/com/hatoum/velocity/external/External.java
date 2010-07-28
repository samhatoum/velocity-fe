package com.hatoum.velocity.external;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hatoum.velocity.Resources;
import com.hatoum.velocity.Utilities;
import com.hatoum.velocity.framework.IAccessBean;
import com.hatoum.velocity.framework.IPlugin;

public class External implements IPlugin {

	private static final String EXTERNAL_PORT = "externalPort";

	private static Logger logger = Logger.getLogger(External.class.getName());

	static private int port = -1;

	private static External instance;

	private ExternalAccessBean accessBean;

	private ServerSocket server;

	private boolean isKilled;

	private List<ExternalModule> externalModules;

	private External() {
		if (port == -1) {
			port = Resources.getIntProperty(EXTERNAL_PORT);
		}
		accessBean = new ExternalAccessBean();
		externalModules = new ArrayList<ExternalModule>();
		logger.info("created a External instance. Waiting for connection");
		waitForConnection();
	}

	static public External getInstance() {
		if (instance == null) {
			instance = new External();
		}
		return instance;
	}

	public IAccessBean getAccessBean() {
		return accessBean;
	}

	public static int getPort() {
		return port;
	}

	public static void setPort(int port) {
		External.port = port;
	}

	public void kill() {
		if (isKilled) {
			return;
		}
		try {
			// kill the server
			if (server != null) {
				server.close();
			}
			// kill each external module
			for (ExternalModule eachExternalModule : externalModules) {
				eachExternalModule.kill();
			}
			externalModules.clear();
			externalModules = null;
			instance = null;
			accessBean = null;
			isKilled = true;
		} catch (Exception e) {
			logger.severe(Utilities.getLog(e));
		}
	}

	public void waitForConnection() {
		// don't hog the current thread to wait for a connection from External
		new Thread() {
			public void run() {
				try {
					while (!isKilled) {
						logger.info("External socket server is listening "
								+ "on port " + port);
						server = new ServerSocket(port);
						Socket socket = server.accept();
						if (socket.isConnected()) {
							logger.log(Level.INFO, "External connected to "
									+ socket.getRemoteSocketAddress());
						}

						// create a new ExernalModule and give it the socket
						ExternalModule externalModule = new ExternalModule(
								socket, accessBean);
						externalModules.add(externalModule);
					}
				} catch (IOException e) {
					if (e.getMessage().toUpperCase().indexOf("SOCKET CLOSED") == -1) {
						logger.severe(Utilities.getLog(e));
					}
				}
			}
		}.start();
	}

	public void updateBean() {
	}

	public boolean clientsConnected() {
		return externalModules.size() > 0;
	}

	public int getNumberOfClients() {
		return externalModules.size();
	}
}