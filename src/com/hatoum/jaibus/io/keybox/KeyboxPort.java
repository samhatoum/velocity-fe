package com.hatoum.jaibus.io.keybox;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.hatoum.jaibus.io.AbstractBasePortImpl;

public class KeyboxPort extends AbstractBasePortImpl {

	JFrame frame;

	JTextField textField;

	JLabel label;

	public KeyboxPort() {
		frame = new JFrame();
		textField = new JTextField();
		label = new JLabel("Empty");

		textField.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				// do nothing
			}

			public void keyReleased(KeyEvent e) {
				// do nothing
			}

			public void keyTyped(KeyEvent e) {
				KeyboxMessage keyboxMessage = new KeyboxMessage();
				keyboxMessage.setMessageContent("" + e.getKeyChar());
				notifyListeners(keyboxMessage);
			}
		});

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(400, 400, 200, 70);
		Container contentPane = frame.getContentPane();
		JPanel panel = new JPanel(new BorderLayout());
		contentPane.add(panel);
		panel.add(label, BorderLayout.NORTH);
		panel.add(textField, BorderLayout.SOUTH);
		frame.setVisible(true);
	}

	public void send(Object deviceSpecificDisplayObject) {
		if (!(deviceSpecificDisplayObject instanceof String)) {
			return;
		}
		label.setText((String) deviceSpecificDisplayObject);
	}
}