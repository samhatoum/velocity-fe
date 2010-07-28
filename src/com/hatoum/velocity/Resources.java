package com.hatoum.velocity;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class Resources {

	private static final Logger logger = Logger.getLogger(Resources.class
			.getName());

	private static final boolean RECURSE_PROPERTIES = false;

	private static final String EMPTY = "";

	private static final String PLUGIN_QUALIFIER = "PluginQualifier";

	private static String OS;

	private static boolean isLinux;

	private static boolean isMac;

	private static boolean isWindows;

	private static String resourceLoaderString;

	static {
		String osName = System.getProperty("os.name").toLowerCase();
		isMac = false;
		isWindows = false;
		isLinux = false;
		if (osName.indexOf("win") != -1) {
			OS = "Windows";
			isWindows = true;
		} else if (osName.indexOf("mac") != -1) {
			OS = "Mac";
			isMac = true;
		} else if (osName.indexOf("linux") != -1) {
			OS = "Linux";
			isLinux = true;
		}
		resourceLoaderString = "Loading resources: ";
	}

	private static String separator = File.separator;

	private static String pwd;

	private static Properties properties;

	private static List<String> propertiesFiles;
	static {
		properties = new Properties();
		propertiesFiles = new ArrayList<String>();
		findResourceFiles();
		loadResourceFiles();
	}

	private static void findResourceFiles() {
		try {
			pwd = new File(".").getCanonicalPath();
		} catch (IOException e) {
			logger.severe(Utilities.getLog(e));
		}
		findResourceFiles(pwd);
	}

	private static void findResourceFiles(String directory) {

		File requestedInput = new File(directory);

		requestedInput.listFiles(new FileFilter() {

			public boolean accept(File file) {
				if (file.toString().endsWith(".properties")) {
					try {
						propertiesFiles.add(file.getCanonicalPath());
						resourceLoaderString += file.getCanonicalPath()
								+ "\r\n";
					} catch (IOException e) {
						logger.severe(Utilities.getLog(e));
					}
				}
				return false;
			}
		});
		requestedInput.listFiles(new FileFilter() {

			public boolean accept(File file) {
				if (RECURSE_PROPERTIES && file.isDirectory()) {
					findResourceFiles(file.getAbsolutePath());
				}
				return false;
			}
		});
	}

	private static void loadResourceFiles() {
		try {
			for (String eachPropertiesFile : propertiesFiles) {
				String path = eachPropertiesFile;
				FileInputStream fileInputStream = new FileInputStream(path);
				properties.load(fileInputStream);
			}
		} catch (FileNotFoundException e) {
			logger.severe(Utilities.getLog(e));
			e.printStackTrace();
		} catch (IOException e) {
			logger.severe(Utilities.getLog(e));
			e.printStackTrace();
		}
	}

	public static String getBinDirectory() {
		String bin;
		File dist = new File(pwd + separator + "bin" + separator + "dist");
		if (dist.exists() && dist.isDirectory()) {
			bin = pwd + separator + "bin" + separator + "dist";
		} else {
			bin = pwd + separator + "bin";
		}
		return bin;
	}

	public static String getWorkingDirectory() {
		return pwd;
	}

	public static String getProperty(String property) {

		String value = properties.getProperty(property);
		if (null == value) {
			value = properties.getProperty(property + OS);
		}
		if (value == null) {
			return EMPTY;
		}
		return value.trim();
	}

	public static List<String> getRegisteredPlugins() {
		List<String> build = new ArrayList<String>();
		for (Object eachKey : properties.keySet()) {
			if (((String) eachKey).indexOf(getProperty(PLUGIN_QUALIFIER)) != -1) {
				build.add(getProperty((String) eachKey));
			}
		}
		return build;
	}

	public static boolean getBooleanProperty(String property) {
		return Boolean.parseBoolean(getProperty(property));
	}

	public static int getIntProperty(String property) {
		return Integer.parseInt(getProperty(property));
	}

	public static boolean isWindows() {
		return isWindows;
	}

	public static boolean isLinux() {
		return isLinux;
	}

	public static boolean isMac() {
		return isMac;
	}

	public static Process exec(String[] tokens, boolean addOpenIfMac) {
		try {
			if (isMac && addOpenIfMac) {
				String[] newTokens = new String[tokens.length + 1];
				newTokens[0] = "open";
				for (int i = 0; i < tokens.length; i++) {
					newTokens[i + 1] = tokens[i];
				}
				tokens = newTokens;
			}
			return Runtime.getRuntime().exec(tokens);
		} catch (IOException e) {
			logger.severe(Utilities.getLog(e));
		}
		return null;
	}

	public static void logResourceLoaderString() {
		logger.info(resourceLoaderString);
	}
}