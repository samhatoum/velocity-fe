package com.hatoum.jaibus.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Exchange implements MessageListener {

	// private static Logger logger =
	// Logger.getLogger(Exchange.class.getName());

	private Port port;

	private List<Message> messages;

	private Map<String, Macro> macros;

	private List<Map<Message, Integer>> macroHashMaps;

	private List<ExchangeListener> exchangeListeners;

	private int largestMacro;

	private long lastTimeMillis;

	public Exchange(Port port) {
		messages = new ArrayList<Message>();
		macros = new HashMap<String, Macro>();
		macroHashMaps = new ArrayList<Map<Message, Integer>>();
		exchangeListeners = new ArrayList<ExchangeListener>();
		lastTimeMillis = System.currentTimeMillis();
		this.port = port;
		port.addMessageListener(this);
	}

	public void newMessage(Message message) {
		processMessage(message);
		TimeFrame delay = new TimeFrame(System.currentTimeMillis()
				- lastTimeMillis);
		messages.add(delay);
		messages.add(message);
		lastTimeMillis = System.currentTimeMillis();
		processMessages();
	}

	private void processMessage(Message message) {
		// TODO this is a test implementation
		port.send(message.getMessageContent());
	}

	private void processMessages() {

		// check if the message list (incoming buffer) is greater than the
		// largest macro, and remove any excess messages from the end if it is
		// as they are not needed for detecting macros
		if (messages.size() > largestMacro) {
			messages = messages.subList(messages.size() - largestMacro,
					messages.size());
		}

		Set<String> actions = macros.keySet();

		// check if the new addition to the messages completes a macro
		for (String eachAction : actions) {

			Macro eachMacro = macros.get(eachAction);

			// ignore this macro if it is larger than collected message list
			if (messages.size() < eachMacro.size()) {
				continue;
			}

			// cycle through the macro's messages backwards, comparing each
			// message with those in the current message que
			for (int eachMessageInMacroID = eachMacro.size() - 1; eachMessageInMacroID >= 0; eachMessageInMacroID--) {

				Message macroMessage = eachMacro.get(eachMessageInMacroID);
				int backwardsOffset = messages.size() - eachMacro.size();
				Message currentMessage = messages.get(eachMessageInMacroID
						+ backwardsOffset);

				// check the equality of these messages and abort if no match
				if (!macroMessage.equals(currentMessage)) {
					break;
				}

				// if this far and this is the last cycle, we have a match
				if (eachMessageInMacroID == 0) {

					// notify exchange listeners
					notifyExchangeListeners(eachAction);

					// flush the messages list
					messages.clear();
				}
			}
		}
	}

	public void addMacro(String action, Macro macro) {
		// hash all the messages in the macro so they can be looked up later
		HashMap<Message, Integer> macroHashMap = new HashMap<Message, Integer>();
		for (int eachMessageIndex = 0; eachMessageIndex < macro.size(); eachMessageIndex++) {
			Message macroMessage = macro.get(eachMessageIndex);
			macroHashMap.put(macroMessage, eachMessageIndex);
		}
		macroHashMaps.add(macroHashMap);

		// keep track of the largest macros
		if (macro.size() > largestMacro) {
			largestMacro = macro.size();
		}

		// and add the macro
		macros.put(action, macro);
	}

	public void addExchangeListener(ExchangeListener exchangeListener) {
		exchangeListeners.add(exchangeListener);
	}

	protected void notifyExchangeListeners(String action) {
		for (ExchangeListener exchangeListener : exchangeListeners) {
			exchangeListener.doAction(action);
		}
	}
}