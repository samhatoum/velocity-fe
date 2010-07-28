package com.hatoum.velocity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.hatoum.velocity.framework.comms.CommServer;

public class Velocity {

	private static Logger logger = Logger.getLogger(Velocity.class.getName());

	private static final String STARTING_VELOCITY = "\n"
			+ "----------------------------------------------------------------"
			+ "----------------\n"
			+ "Starting Velocity\n"
			+ "----------------------------------------------------------------"
			+ "----------------\n";

	private static final String KILL_MPLAYER_COMMAND = "killMPlayerCommand";

	private static final String KILL_FLASH_PLAYER_COMMAND = "killFlashPlayerCommand";

	private static String separator = File.separator;

	private static boolean flashDevelopmentMode;

	public static void main(String args[]) {

		try {

			setupDebug();

			logger.info(STARTING_VELOCITY);

			Resources.logResourceLoaderString();

			killMPlayerIfRunning();

			killFlashPlayerIfRunning();

			verifyFlashSecurity();

			flashDevelopmentMode = Resources
					.getBooleanProperty("flashDevelopmentMode");

			new CommServer(flashDevelopmentMode);

		} catch (Exception e) {
			logger.severe(Utilities.getLog(e));
		}
	}

	private static void killMPlayerIfRunning() {
		String cmd = Resources.getProperty(KILL_MPLAYER_COMMAND);
		killProcess(cmd, "mplayer");
	}

	private static void killFlashPlayerIfRunning() {
		String cmd = Resources.getProperty(KILL_FLASH_PLAYER_COMMAND);
		killProcess(cmd, "Flash player");
	}

	private static void killProcess(String cmd, String processName) {
		try {
			Process process = Runtime.getRuntime().exec(cmd);
			InputStream inputStream = process.getInputStream();
			int b = 0;
			String input = new String();
			while ((b = inputStream.read()) != -1) {
				char c = (char) b;
				input += c;
			}
			if (input.indexOf("Process does not exist") != -1) {
				logger.info(processName
						+ " not running. Resuming normal launch");
			} else {
				logger.info(processName + " was running and has been killed");
			}
			process.waitFor();
		} catch (IOException e) {
			logger.severe("Error while trying to shut down " + processName
					+ "\n" + Utilities.getLog(e));
		} catch (InterruptedException e) {
			logger.severe("Error while trying to shut down " + processName
					+ "\n" + Utilities.getLog(e));
		}
	}

	private static void setupDebug() throws IOException {
		boolean debug = Resources.getBooleanProperty("debug");

		if (debug) {

			String logToString = Resources.getProperty("logTo");
			if (!"CONSOLE".equals(logToString)) {

				String debugFileName = System.currentTimeMillis() + "_"
						+ "debug.txt";

				Logger rootLogger = Logger.getLogger("");

				Handler[] handlers = rootLogger.getHandlers();
				for (Handler eachHandler : handlers)
					rootLogger.removeHandler(eachHandler);

				File logFileDirectory = new File(logToString + separator
						+ debugFileName).getParentFile();

				if (!logFileDirectory.exists()) {
					logFileDirectory.mkdirs();
				}

				FileHandler fileHandler = new FileHandler(logToString
						+ separator + debugFileName);
				fileHandler.setFormatter(new SimpleFormatter());
				rootLogger.addHandler(fileHandler);
			}
		}
	}

	@SuppressWarnings("unused")
	private static void verifyFlashSecurity() {

		logger.info("verifying flash security");

		// prepare paths
		String flashPlayerTrustPath = Resources
				.getProperty("flashPlayerTrustDirectory");

		// user does not want to specify security settings
		if ("".equals(flashPlayerTrustPath)) {
			logger.info("flashPlayerTrustPath property not found. Skipping "
					+ "flash security check");
			return;
		}

		// add home if specified in the propertiees file
		if (flashPlayerTrustPath.indexOf("%USER_HOME%") != -1) {
			String userHome = System.getProperty("user.home");
			if (userHome.substring(userHome.length() - 1) != separator) {
				flashPlayerTrustPath = userHome + separator
						+ flashPlayerTrustPath.substring(11);
			} else {
				flashPlayerTrustPath = userHome
						+ flashPlayerTrustPath.substring(11);
			}
		}
		String velcoityCfgPath = flashPlayerTrustPath + separator
				+ "velocity.cfg";
		logger.info("resolved velocity.cfg path to " + velcoityCfgPath);

		// make sure FlashPlayerTrust exists
		File flashPlayerTrustFile = new File(flashPlayerTrustPath);
		if (!flashPlayerTrustFile.exists()) {
			logger
					.info("FlashPlayerTrust directory not found. Attempting to create it");
			flashPlayerTrustFile.mkdirs();
		} else if (flashPlayerTrustFile.isFile()) {
			// this shouldn't be true
			logger.severe(flashPlayerTrustPath
					+ " is a file when it should be a directory");
			throw new RuntimeException(flashPlayerTrustPath
					+ " is a file when it should be a directory");
		}

		// delete the security file everytime, since this can run from
		// anywhere
		File velocityCfgFile = new File(velcoityCfgPath);
		if (velocityCfgFile.exists()) {
			if (velocityCfgFile.isDirectory()) {
				// this shouldn't be true
				logger.severe(velcoityCfgPath
						+ " is a directory when it should be a file");
				throw new RuntimeException(velcoityCfgPath
						+ " is a directory when it should be a file");
			} else {
				logger.info("Found an old security file. Removing");
				velocityCfgFile.delete();
			}
		}

		// and recreate it, based ion where we're running from
		try {
			logger.info("Attempting to create new security file");
			velocityCfgFile.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(
					velocityCfgFile));
			// -----------------------------------------------------------------
			// FIXME why did I put fix me here?!
			out.write(Resources.getWorkingDirectory() + separator + "skins\n");
			out.write(separator + "skins");
			// -----------------------------------------------------------------
			out.close();
		} catch (IOException e) {
			logger.severe(Utilities.getLog(e));
		}
	}

	public static void launchFlash() {
		if (flashDevelopmentMode) {
			return;
		}
		logger.info("launching flash");

		String flashLaunchCommand = Resources.getProperty("flashLaunchCommand");
		String[] flashLaunchCommandTokens;
		flashLaunchCommandTokens = flashLaunchCommand.split(" ");

		String build = new String();
		String[] tokens = new String[flashLaunchCommandTokens.length + 1];
		for (int n = 0; n < flashLaunchCommandTokens.length; n++) {
			tokens[n] = flashLaunchCommandTokens[n];
			build += tokens[n] + " ";
		}
		tokens[tokens.length - 1] = Resources.getProperty("skin");
		build += tokens[tokens.length - 1];

		logger.info("Starting flash with [" + build + "]");
		Resources.exec(tokens, true);
	}
}