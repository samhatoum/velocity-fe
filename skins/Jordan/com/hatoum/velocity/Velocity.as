import mx.utils.Delegate;
import com.hatoum.velocity.JSON;
import mx.controls.TextArea;
class com.hatoum.velocity.Velocity {
	//------------------------------------------------------------------------
	private static var __HOST:String = "localhost";
	private static var __PORT:Number = 4000;
	private static var SCOPE:String = "::";
	private static var SPACE:String = " ";
	private static var PLUGIN_MANAGER:String = "PluginManager";
	//------------------------------------------------------------------------
	private static var __plugins:Array;
	private static var __pluginObjects:Object;
	private static var __debug = true;
	private static var __openConnections = 0;
	private static var __startFrame;
	//------------------------------------------------------------------------
	private var _socket:XMLSocket;
	private var _connected:Boolean;
	private var _handleModel:Boolean;
	private var _pluginName:String;
	//------------------------------------------------------------------------
	// TODO remove this
	//
	private static var myTextArea:TextArea;
	
	public static function setInfo(theText:String):Void {
		myTextArea.text = theText;
	}
	public static function setTextArea(theTextArea:TextArea):Void {
		myTextArea = theTextArea;
	}
	//
	//------------------------------------------------------------------------
	public static function initialize(startFrame:Number):Void {
		if (__debug) {
			trace("initializing");
		}
		__startFrame = startFrame;
		__plugins = new Array();
		// make a plugin manager 
		__plugins[PLUGIN_MANAGER] = new Velocity(PLUGIN_MANAGER);
		__plugins[PLUGIN_MANAGER]._handleModel = true;
		__plugins[PLUGIN_MANAGER].connect();
	}
	//------------------------------------------------------------------------
	private function ready() {
		if (__debug) {
			trace("Connections ready");
		}
		var pluginName:String;
		for (pluginName in __pluginObjects) {
			// populate __plugins 
			__plugins[pluginName] = new Velocity(pluginName);
			// and initialise them
			__plugins[pluginName].connect();
		}
		// the plugin manager has done its deeds
		__plugins[PLUGIN_MANAGER].close();
		gotoAndPlay(__startFrame);
	}
	//------------------------------------------------------------------------
	public function handle(resp:Object):Void {
		if (__debug) {
			trace(_pluginName+": FROM JAVA: "+resp.toString());
		}
		var updateObject:Object;
		// extract parameters to update from the response
		updateObject = JSON.parse(resp.toString());
		// update object
		var parameter:String;
		for (parameter in updateObject) {
			__pluginObjects[_pluginName][parameter] = updateObject[parameter];
		}
	}
	public function handleModel(resp:Object):Void {
		try {
			__pluginObjects = JSON.parse(resp.toString());
		} catch (ex) {
			trace(ex.name+":"+ex.message+":"+ex.at+":"+ex.text);
		}
		ready();
	}
	//------------------------------------------------------------------------	
	private function Velocity(pluginName:String) {
		_pluginName = new String(pluginName);
		if (__debug) {
			trace(_pluginName+": velocity plugin ["+getPluginName()+"] created");
		}
	}
	//------------------------------------------------------------------------	
	public static function getPlugin(plugin:String):Velocity {
		// verify the plugin
		var ref:Velocity = __plugins[plugin];
		if (ref == null) {
			if (__debug) {
				trace("plugin "+ref+" not found.");
			}
		}
		// return the plugin                   
		return ref;
	}
	public static function getAccessBean(pluginName:String):Object {
		return __pluginObjects[pluginName];
	}
	//------------------------------------------------------------------------
	public function invoke(method:String):Void {
		if (__debug) {
			trace(_pluginName+": invoke called, sending: "+_pluginName+SCOPE+method);
		}
		sendMessage(method);
	}
	//------------------------------------------------------------------------
	public function getPluginName():String {
		return _pluginName;
	}
	public function setPluginName(pluginName:String):Void {
		_pluginName = pluginName;
	}
	//------------------------------------------------------------------------	
	private function connect():Void {
		if (__debug) {
			trace(_pluginName+": connecting...");
		}
		_connected = false;
		_socket = new XMLSocket();
		_socket.onConnect = Delegate.create(this, handleConnect);
		_socket.onClose = Delegate.create(this, handleClose);
		if (_handleModel) {
			_socket.onData = Delegate.create(this, handleModel);
		} else {
			_socket.onData = Delegate.create(this, handle);
		}
		if (!_socket.connect(__HOST, __PORT)) {
			if (__debug) {
				trace(_pluginName+": connection failed");
			}
		}
	}
	private function handleConnect(succeeded):Void {
		if (succeeded) {
			_connected = true;
			__openConnections++;
			if (__debug) {
				trace(_pluginName+": connected");
			}
		} else {
			if (__debug) {
				trace(_pluginName+": connection failed");
			}
			_connected = false;
			gotoAndStop(2);
		}
	}
	private function handleClose():Void {
		if (__debug) {
			trace(_pluginName+": connection closed.\n");
		}
		_connected = false;
		unloadMovieNum(0);
		__openConnections--;
		if (__debug) {
			trace("open connections = " + __openConnections);
		}
		// FIXME this doesn't work!
		if (__openConnections == 0) {
			if (__debug) {
				trace("no more open connections. quitting");
			}
			unloadMovieNum(0);
		}
	}
	private function sendMessage(msg:String):Void {
		if (!_connected) {
			if (__debug) {
				trace("cannot send message, not connected.");
			}
		}
		var xmlMessage:XML = new XML();
		xmlMessage.parseXML(_pluginName+SCOPE+msg);
		if (__debug) {
			trace(_pluginName+": sendMessage("+xmlMessage+")");
		}
		_socket.send(xmlMessage);
	}
	private function quit():Void {
		_socket.close();
	}
}
