package com.hatoum.velocity.destinator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import com.hatoum.velocity.Resources;
import com.hatoum.velocity.framework.IAccessBean;
import com.hatoum.velocity.framework.IPlugin;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Variant;

public class Destinator implements IPlugin {

	public enum Action {
		uninstall, install;
	}

	private static Logger logger = Logger.getLogger(Destinator.class.getName());

	private static Destinator instance;

	private DestinatorAccessBean accessBean;

	private static final String SDK_DLL_PATH = Resources.getBinDirectory()
			+ File.separator + "Destinator" + File.separator + "DestSDK.dll";

	private static final String REGSVR32_COMMAND = "regsvr32";

	private static final String UNINSTALL_SWITCH = "/u";

	private static final String SILENT_SWITCH = "/s";

	private static final String SPACE = " ";

	private static final String EMPTY_STRING = "";

	private static final int FLAG = 0;

	private static final String KEY = "";

	private Destinator() {
		initialiseCOMComponent();
	}

	static public Destinator getInstance() {
		if (instance == null) {
			instance = new Destinator();
		}
		return instance;
	}

	private void initialiseCOMComponent() {
		ComThread.InitSTA();
		registerDLL(Action.install);
		try {
			ActiveXComponent destinator = new ActiveXComponent("DestSDK.Dest");
			destinator.invoke("CreateDestinatorWindow", new Variant[] {
					new Variant(FLAG), new Variant(KEY) });

		} catch (Exception e) {
			ComThread.Release();
			logger.severe(e.toString());
		}
	}

	private static void registerDLL(Action action) {
		final String command = REGSVR32_COMMAND
				+ SPACE
				+ SILENT_SWITCH
				+ (action == Action.uninstall ? UNINSTALL_SWITCH : EMPTY_STRING)
				+ SPACE + SDK_DLL_PATH;
		logger.info((action == Action.uninstall ? "Unr" : "R")
				+ "egistering DestSDK.dll using command [" + command + "]");

		try {
			Process process = Runtime.getRuntime().exec(command);
			InputStream inputStream = process.getInputStream();
			while (inputStream.read() != -1) {
				// do nothing
			}
		} catch (IOException e) {
			logger.severe(e.toString());
		}
	}

	public IAccessBean getAccessBean() {
		return accessBean;
	}

	public void updateBean() {
		// TODO Auto-generated method stub
	}

	public void kill() {
		ComThread.Release();
		registerDLL(Action.uninstall);
	}
}