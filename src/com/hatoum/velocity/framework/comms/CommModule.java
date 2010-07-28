package com.hatoum.velocity.framework.comms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hatoum.velocity.Utilities;
import com.hatoum.velocity.framework.PluginManager;

public class CommModule {

	private static Logger logger = Logger.getLogger(CommModule.class.getName());

	Socket socket;

	BufferedReader in;

	PrintWriter out;

	MessageMonitor messageMonitor;

	PluginManager pluginManager;

	private final CommServer server;

	private static boolean killAllPluginsInvoked;

	public CommModule(Socket socket, CommServer server) {

		this.server = server;
		this.socket = socket;
		messageMonitor = new MessageMonitor();

		try {
			in = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);

			pluginManager = PluginManager.getInstance();

			messageMonitor.start();

		} catch (IOException e) {
			logger.warning(Utilities.getLog(e));
		}
	}

	protected void kill() {
		logger.info("Killing comm module");
		// since we have no lnk
		if (!killAllPluginsInvoked) {
			killAllPluginsInvoked = true;
			pluginManager.killAllPlugins();
		}
		try {
			out.close();
			if (socket != null)
				socket.close();
			in.close();
		} catch (SocketException e) {
			if (e.getMessage().indexOf("socket closed") != -1) {
				logger.warning(Utilities.getLog(e));
			}
		} catch (IOException e) {
			logger.warning(Utilities.getLog(e));
		} finally {
			server.removeModule(this);
		}
	}

	public static boolean send(Socket socket, String data) {
		PrintWriter writer = null;
		try {
			OutputStream outputStream = socket.getOutputStream();
			writer = new PrintWriter(outputStream, true);
		} catch (SocketException e) {
			if (e.getMessage().indexOf("Socket is closed") == -1) {
				logger.warning(Utilities.getLog(e));
			}
			return false;
		} catch (IOException e) {
			return false;
		}
		logger.info("sending: " + data);
		writer.print(data + '\0');
		writer.flush();
		if (writer.checkError()) {
			return false;
		}
		return true;
	}

	private class MessageMonitor extends Thread {

		private static final int MAX_TOKENS = 2;

		private static final int PLUGIN_NAME_INDEX = 0;

		private static final int METHOD_AND_PARAMETERS_INDEX = 1;

		private static final String COMMA = ",";

		private final static String AMP = "&amp;";

		private final static String LT = "&lt;";

		private final static String GT = "&gt;";

		private final static String APOS = "&apos;";

		private final static String QUOT = "&quot;";

		private static final String AMPERSAND = "&";

		private static final String LESS_THAN = "<";

		private static final String GREATER_THAN = ">";

		private static final String QUOTE = "\"";

		private static final String APOSTRAPHE = "'";

		private static final String UNDEFINED = "undefined";

		public MessageMonitor() {
			setName("CommModule message monitor");
		}

		/**
		 * Thread run method. Monitors incoming messages.
		 */
		public void run() {
			logger.log(Level.INFO, "monitoring stream");
			try {
				// read the incoming stream
				char charBuffer[] = new char[1];
				while (in.read(charBuffer, 0, 1) != -1) {

					String line = readLine(charBuffer);

					// validate the line
					String[] tokens;
					if ((tokens = validate(line)) == null)
						continue;

					// get the target plugin name
					String pluginName = tokens[PLUGIN_NAME_INDEX];

					// perform a late binding of the socket to the plugin
					if (!pluginManager.isSocketBound(pluginName)) {
						logger.info("late binding " + pluginName);
						pluginManager.bindSocketToPlugin(pluginName, socket);
					}

					// get the target method name
					String methodName = getMethodName(tokens);

					// and the method parameters, if any (if not, empty array)f
					String[] parameters = getParameters(tokens);

					// bit of a hack: the method call above returns null if the
					// parameters contian the keyword "undefined"
					if (parameters == null) {
						continue;
					}

					escapeParameters(parameters);

					// send message to plugin via plugin manager
					pluginManager.invoke(pluginName, methodName, parameters);
				}
			} catch (IOException e) {
				logger.log(Level.INFO, "Client caused a read error " + e
						+ " : " + e.getMessage()
						+ " and has been disconnected." + "\n"
						+ Utilities.getLog(e));

			} finally {
				kill();
			}
			System.exit(0);
		}

		private void escapeParameters(String[] parameters) {
			for (int i = 0; i < parameters.length; i++) {
				String eachParameter = parameters[i];
				eachParameter = eachParameter.replace(AMP, AMPERSAND);
				eachParameter = eachParameter.replace(LT, LESS_THAN);
				eachParameter = eachParameter.replace(GT, GREATER_THAN);
				eachParameter = eachParameter.replace(QUOT, QUOTE);
				eachParameter = eachParameter.replace(APOS, APOSTRAPHE);
				parameters[i] = eachParameter;
			}
		}

		private String getMethodName(String[] tokens) {
			int spaceIndex = tokens[METHOD_AND_PARAMETERS_INDEX].indexOf(" ");
			String methodName = null;
			if (spaceIndex != -1) {
				methodName = tokens[METHOD_AND_PARAMETERS_INDEX].substring(0,
						spaceIndex);
			} else {
				methodName = tokens[METHOD_AND_PARAMETERS_INDEX];
			}
			return methodName;
		}

		private String[] getParameters(String[] tokens) {
			int spaceIndex = tokens[METHOD_AND_PARAMETERS_INDEX].indexOf(" ");
			if (spaceIndex == -1) {
				return new String[] {};
			}
			String parametersString = tokens[METHOD_AND_PARAMETERS_INDEX]
					.substring(spaceIndex + 1);
			if (parametersString.indexOf(UNDEFINED) != -1) {
				logger.warning("received an undefined parameter");
				return null;
			}
			String[] parameters = parametersString.split(COMMA);
			return parameters;
		}

		private String[] validate(String line) {
			String[] tokens = line.split("::");
			if (tokens.length != MAX_TOKENS) {
				logger.severe("there can only be one scope : " + line);
				return null;
			}
			return tokens;
		}

		private String readLine(char[] charBuffer) throws IOException {
			StringBuffer stringBuffer = new StringBuffer(8192);
			while (charBuffer[0] != '\0') {
				stringBuffer.append(charBuffer[0]);
				in.read(charBuffer, 0, 1);
			}
			String line = stringBuffer.toString();
			return line;
		}
	}
}
