package com.hatoum.velocity.framework.comms;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hatoum.velocity.Resources;
import com.hatoum.velocity.Utilities;
import com.hatoum.velocity.Velocity;
import com.hatoum.velocity.framework.PluginManager;

public class CommServer extends Thread {

	private static final String SERVER_PORT = "serverPort";

	private static final String TERMINAL_PORT = "terminalPort";

	private static final String SERVER_RETRY_DELAY = "serverRetryDelay";

	private static final String SERVER_RETRY_AMOUNT = "serverRetryAmount";

	private static Logger logger = Logger.getLogger(CommServer.class.getName());

	private ServerSocket server;

	private ServerSocket terminalSocket;

	private Socket socket;

	// TODO review CommModule structure - too hairy and needless instances are
	// created left right and center
	private List<CommModule> commModules;

	private PluginManager pluginManager;

	private final boolean stayAwakeOnClientClose;

	private int terminalPort;

	private int serverPort;

	private boolean killed;

	public CommServer(boolean stayAwakeOnClientClose) {
		this.stayAwakeOnClientClose = stayAwakeOnClientClose;
		pluginManager = PluginManager.getInstance();
		terminalPort = Resources.getIntProperty(TERMINAL_PORT);
		serverPort = Resources.getIntProperty(SERVER_PORT);
		startServer();
	}

	private void openTerminalSocket() {
		new Thread() {
			@Override
			public void run() {
				try {
					terminalSocket = new ServerSocket(terminalPort);
					logger.log(Level.INFO, "Terminal port opened at: "
							+ terminalPort);
					Socket terminal = terminalSocket.accept();
					if (terminal.isConnected()) {
						logger.info("Terminal socket connection establsihed. "
								+ "Terminating Velocity");
						killServer();
					}
				} catch (IOException e) {
					logger.warning(Utilities.getLog(e));
				}
			}
		}.start();
	}

	private void terminateOtherInstance() {
		try {
			new Socket((String) null, terminalPort);
		} catch (Exception e) {
			logger.warning("Could not close other instance. Terminating\n"
					+ Utilities.getLog(e));
			killServer();
		}
	}

	private void startServer() {
		logger.log(Level.INFO, "Attempting to Start Server");
		boolean retry = true;
		int attempts = Resources.getIntProperty(SERVER_RETRY_AMOUNT);
		while (retry && attempts != 0) {
			try {
				// --- create a new server
				server = new ServerSocket(serverPort);
				commModules = new ArrayList<CommModule>();
				logger.log(Level.INFO, "Server Started on Port: " + serverPort);

				openTerminalSocket();

				Velocity.launchFlash();

				// --- while the server is active...
				while (true) {
					// --- ...listen for new client connections
					if (!server.isClosed()) {
						socket = server.accept();
					} else {
						return;
					}
					if (socket.isConnected()) {
						logger.log(Level.INFO, "connected to "
								+ socket.getRemoteSocketAddress());
					}
					// ensure the plugin manager is initialised or reinitialised
					// (plugins are loaded and ready)
					pluginManager.refresh();
					// send plugin names, and plugin objects to client if needed
					if (commModules.size() == 0) {
						pluginManager.advertisePlugins(socket);
					}
					// start a comm module for this client
					CommModule module = new CommModule(socket, this);
					commModules.add(module);
				}
			} catch (BindException e) {
				logger.info("Server already running on port " + serverPort
						+ ". Attempting to terminate it.");
				terminateOtherInstance();
				logger.info("Termination succesful. Resuming normal operation");
			} catch (SocketException e) {
				if (e.getMessage().indexOf("socket closed") == -1) {
					logger.info(Utilities.getLog(e));
				}
				retry = false;
			} catch (IOException e) {
				logger.severe("Server Error... stopping Server\n"
						+ Utilities.getLog(e));
				retry = false;
			} finally {
				if (!retry) {
					killServer();
				}
			}
			if (!killed) {
				int seconds = Resources.getIntProperty(SERVER_RETRY_DELAY);
				logger.info("Retrying in " + seconds + " seconds");
				try {
					Thread.sleep(seconds * 1000);
				} catch (InterruptedException e) {
					logger.severe(Utilities.getLog(e));
				}
				attempts--;
			}
		}
	}

	protected void removeModule(CommModule module) {
		commModules.remove(module);

		if (commModules.size() == 0) {
			logger.info("no more connected modules, killing all plugins");
			pluginManager.killAllPlugins();
			if (!stayAwakeOnClientClose) {
				killServer();
			}
		}

	}

	// FIXME this kill only calls the media kill, and not other plugins
	public void killServer() {
		if (killed) {
			return;
		}
		try {
			// --- stop the server
			if (commModules != null) {
				for (CommModule eachCommModule : commModules) {
					eachCommModule.kill();
				}
			}
			if (server != null) {
				server.close();
			}
			logger.log(Level.INFO, "Server Stopped");
			killed = true;
		} catch (IOException e) {
			logger.log(Level.INFO, "Error while stopping Server\n"
					+ Utilities.getLog(e));
		}
	}
}
