package com.hatoum.velocity.framework;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.sf.json.JSONObject;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.hatoum.velocity.Resources;
import com.hatoum.velocity.Utilities;
import com.hatoum.velocity.framework.comms.CommModule;

public class PluginManager {

	private static Logger logger = Logger.getLogger(PluginManager.class
			.getName());

	private static final String OBJECT_UPDATE_GRANUALITY = "objectUpdateFrequency";

	public static final String OK = "OK";

	public static final String SCOPE = "::";

	private static PluginManager instance = new PluginManager();

	private Map<String, IPlugin> pluginInstances;

	private Map<String, Method> pluginMethods;

	private Map<String, Class[]> methodsParameterTypes;

	private boolean initialised;

	private Map<String, IAccessBean> accessBeans;

	private Map<String, Integer> accessBeansHashcodes;

	private Map<String, Socket> sockets;

	private AccessBeansUpdater accessBeansUpdater;

	public long updateFrequency;

	public static PluginManager getInstance() {
		return instance;
	}

	private PluginManager() {
		updateFrequency = Resources.getIntProperty(OBJECT_UPDATE_GRANUALITY);
		logger.info("Created a PluginManager instance");
		if (instance != null) {
			String msg = "PluginManager constructor called  and instance "
					+ "already instentiated";
			logger.severe(msg);
			throw new IllegalStateException(msg);
		}
	}

	public void refresh() {
		if (initialised) {
			return;
		}
		logger.info("(re)initialising plugin manager");
		initialised = true;
		pluginInstances = new HashMap<String, IPlugin>();
		pluginMethods = new HashMap<String, Method>();
		methodsParameterTypes = new HashMap<String, Class[]>();
		accessBeans = new HashMap<String, IAccessBean>();
		accessBeansHashcodes = new HashMap<String, Integer>();
		sockets = new HashMap<String, Socket>();

		try {
			List<String> registeredPlugins = Resources.getRegisteredPlugins();
			for (String eachPlugin : registeredPlugins) {
				registerPlugin(Class.forName(eachPlugin));
			}
		} catch (ClassNotFoundException e) {
			logger.severe(Utilities.getLog(e));
		}

		accessBeansUpdater = new AccessBeansUpdater();
		accessBeansUpdater.start();
	}

	public Object[] getPlugins() {
		return pluginInstances.keySet().toArray();
	}

	private void registerPlugin(Class pluginClass) {
		try {
			String pluginName = pluginClass.getSimpleName();

			// get the plugin instance from the plugin itself
			Object pluginInstance = getPluginInstance(pluginName, pluginClass);

			// register its access object
			registerPluginAccessObject(pluginName, pluginInstance);

			// register its methods
			registerPluginMethods(pluginName, pluginInstance);

		} catch (SecurityException e) {
			logger.warning(Utilities.getLog(e));
		} catch (NoSuchMethodException e) {
			logger.warning(Utilities.getLog(e));
		} catch (IllegalArgumentException e) {
			logger.warning(Utilities.getLog(e));
		} catch (IllegalAccessException e) {
			logger.warning(Utilities.getLog(e));
		} catch (InvocationTargetException e) {
			logger.warning(Utilities.getLog(e));
		}
	}

	@SuppressWarnings("unchecked")
	private Object getPluginInstance(String pluginName, Class pluginClass)
			throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		// create an instance from the getInstance static method
		Method getInstanceMethod = pluginClass.getMethod("getInstance");
		Object pluginInstance = getInstanceMethod.invoke(null, (Object[]) null);

		pluginInstances.put(pluginName, (IPlugin) pluginInstance);

		return pluginInstance;
	}

	private void registerPluginAccessObject(String pluginName,
			Object pluginObject) throws NoSuchMethodException,
			IllegalAccessException, InvocationTargetException {

		// get an instance of the access bean
		Method getAccessBean = pluginObject.getClass().getMethod(
				"getAccessBean", (Class[]) null);

		Object accessBean = getAccessBean.invoke(pluginObject, (Object[]) null);

		accessBeans.put(pluginName, (IAccessBean) accessBean);
		accessBeansHashcodes.put(pluginName, HashCodeBuilder
				.reflectionHashCode(accessBean));
	}

	private void registerPluginMethods(String pluginName, Object pluginInstance) {
		// get all the declared methods and populate the pluginMethods and
		// methodParameterTypes maps, rejecting any methods that do not take
		// purely Strings as parameters
		Method[] methods = pluginInstance.getClass().getDeclaredMethods();
		registeringMehtods: for (Method method : methods) {
			Class<?>[] parameterTypes = method.getParameterTypes();
			for (Class eachClass : parameterTypes) {
				if (!String.class.getSimpleName().equals(
						eachClass.getSimpleName())) {
					// quite outer loop
					continue registeringMehtods;
				}
			}
			String methodKey = makeMethodKey(pluginName, method.getName(),
					parameterTypes);
			pluginMethods.put(methodKey, method);
			methodsParameterTypes.put(methodKey, parameterTypes);
		}
	}

	private Object[] validateParameters(String methodKey, Object[] parameters) {

		Class[] parameterTypes = methodsParameterTypes.get(methodKey);

		// FIXME why null when wrong number of params?

		// check that the parameters are equal
		if (parameters.length != parameterTypes.length) {
			String msg = "number of parameters do not match method parameters";
			logger.info(msg);
			throw new IllegalArgumentException(msg);
		}

		for (int i = 0; i < parameterTypes.length; i++) {
			logger.info("PluginManager:validateParams: " + parameterTypes[i]);
		}

		return null;
	}

	public void invoke(String pluginName, String methodName, Object[] parameters) {
		try {

			String methodKey = makeMethodKey(pluginName, methodName, parameters);

			Method method = pluginMethods.get(methodKey);

			if (method == null) {
				logger.warning("method " + methodKey + " could not be found");
			}

			String build = "";
			for (Object p : parameters)
				build += "[" + p + "]";
			build += "\n";

			logger.info("methodKey = " + methodKey + "] parameters = " + build);

			// get the plugin instance
			IPlugin pluginInstance = pluginInstances.get(pluginName);

			validateParameters(methodKey, parameters);

			// invoke the method with the plugin instance, and parameters
			method.invoke(pluginInstance, parameters);

		} catch (IllegalArgumentException e) {
			logger.warning(Utilities.getLog(e));
		} catch (IllegalAccessException e) {
			logger.warning(Utilities.getLog(e));
		} catch (InvocationTargetException e) {
			logger.warning(Utilities.getLog(e));
		}
	}

	private String makeMethodKey(String pluginName, String methodName,
			Object[] parameters) {
		String key = pluginName;
		key += SCOPE;
		key += methodName;
		// since every method called from flash must be a string, we are
		// guaranteed a unique key per method
		key += parameters.length;
		return key;
	}

	public void killAllPlugins() {
		for (IPlugin eachPlugin : pluginInstances.values()) {
			eachPlugin.kill();
		}
		pluginInstances.clear();
		pluginMethods.clear();
		methodsParameterTypes.clear();
		accessBeans.clear();
		accessBeansHashcodes.clear();
		sockets.clear();
		initialised = false;
	}

	public void advertisePlugins(Socket socket) {
		// send a map, containing plugin names, and their access objects
		JSONObject jsonAccessBeans = JSONObject.fromMap(accessBeans);
		CommModule.send(socket, jsonAccessBeans.toString());
	}

	public void updateAccessObject(IPlugin plugin) {
		String pluginName = plugin.getClass().getSimpleName();
		AccessBeanBase accessBean = (AccessBeanBase) accessBeans
				.get(pluginName);
		// compare the accessBean's hashcode with the last send one
		// FIXME a null object here causes a crash. does the null exist within
		// the bean or the actual bean?
		//
		// INVESTIGATE another bug here. Sometimes when changing directorys from 
		// the FileBrowser, the currentFileStale property is sent in a seperate
		// message to flash, than the rest of the bean. This is synonymous with
		// a bug that doesn't update the view when the directory changes on the
		// flash side, causing the view to be out of sync between flash and java
		int currentHashcode = HashCodeBuilder.reflectionHashCode(accessBean);
		int lastHashcode = accessBeansHashcodes.get(pluginName);

		if (currentHashcode == lastHashcode) {
			// and if nothing's changed, then we don't need to update
			return;
		}

		// otherwise send the new object
		Socket pluginSocket = sockets.get(pluginName);
		try {
			// FIXME concurrentModificationException in JSON library sometimes
			// here
			JSONObject jsonAccessBean = JSONObject.fromObject(accessBean,
					accessBean.getExcludes());

			if (jsonAccessBean.toString().equals("{}")) {
				return;
			}

			// we are good to send an optimised JSON string now
			CommModule.send(pluginSocket, jsonAccessBean.toString());

		} catch (Exception e) {
			logger.severe("Exception in JSON/Sending\n" + Utilities.getLog(e));
		}

		// we can exclude all paramaters now from being transmitted
		accessBean.excludeAll();

		// and track the new hashcode for it
		accessBeansHashcodes.put(pluginName, currentHashcode);
	}

	public void bindSocketToPlugin(String pluginName, Socket socket) {
		sockets.put(pluginName, socket);
	}

	public boolean isSocketBound(String pluginName) {
		return sockets.get(pluginName) != null;
	}

	private class AccessBeansUpdater extends Thread {

		public void run() {
			while (!pluginInstances.isEmpty()) {
				try {
					sleep(updateFrequency);
				} catch (InterruptedException e) {
					logger.warning(Utilities.getLog(e));
				}
				for (IPlugin eachPlugin : pluginInstances.values()) {
					eachPlugin.updateBean();
					String pluginName = eachPlugin.getClass().getSimpleName();
					Socket pluginSocket = sockets.get(pluginName);
					if (pluginSocket == null) {
						continue;
					}
					updateAccessObject(eachPlugin);
				}
			}
		}
	}

}