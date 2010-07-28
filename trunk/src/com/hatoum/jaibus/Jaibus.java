package com.hatoum.jaibus;

import com.hatoum.jaibus.io.Exchange;
import com.hatoum.jaibus.io.ExchangeListener;
import com.hatoum.jaibus.io.Macro;
import com.hatoum.jaibus.io.Port;
import com.hatoum.jaibus.io.TimeFrame;
import com.hatoum.jaibus.io.keybox.KeyboxMacro;
import com.hatoum.jaibus.io.keybox.KeyboxMessage;
import com.hatoum.jaibus.io.keybox.KeyboxPort;

public class Jaibus implements ExchangeListener {

	public Jaibus() {
		Port port = new KeyboxPort();
		Exchange exchange = new Exchange(port);
		exchange.addExchangeListener(this);

		TimeFrame.setVariance(500);

		Macro abcMacro = new KeyboxMacro();
		abcMacro.add(new KeyboxMessage().setMessageContent("a"));
		abcMacro.add(TimeFrame.IGNORE);
		abcMacro.add(new KeyboxMessage().setMessageContent("b"));
		abcMacro.add(TimeFrame.IGNORE);
		abcMacro.add(new KeyboxMessage().setMessageContent("c"));
		exchange.addMacro("abc", abcMacro);

		Macro da1aMacro = new KeyboxMacro();
		da1aMacro.add(new KeyboxMessage().setMessageContent("d"));
		da1aMacro.add(TimeFrame.IGNORE);
		da1aMacro.add(new KeyboxMessage().setMessageContent("a"));
		da1aMacro.add(new TimeFrame(1000));
		da1aMacro.add(new KeyboxMessage().setMessageContent("a"));
		exchange.addMacro("daa", da1aMacro);
	}

	public static void main(String[] args) {
		new Jaibus();
	}

	public void doAction(String action) {
		System.err.println("do action: " + action);
	}
}
