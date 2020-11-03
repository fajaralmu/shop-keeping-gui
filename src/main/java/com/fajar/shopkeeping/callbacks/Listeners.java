package com.fajar.shopkeeping.callbacks;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Listeners {
	
	public static KeyListener keyPressedOnlyListener(final GeneralCallback<KeyEvent> ev) {
		return new KeyListener() { 
			@Override
			public void keyTyped(KeyEvent e) { } 
			@Override
			public void keyReleased(KeyEvent e) { }

			@Override
			public void keyPressed(KeyEvent e) {
				 ev.action(e); 
			}
		};
	}

}
