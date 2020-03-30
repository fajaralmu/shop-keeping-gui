package com.fajar.shopkeeping.component;

import javax.swing.JFrame;

public class MyCustomFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4704693551064670453L;
	private static final String MAIN_TITLE = "Shop Keeping::";
	final int width, height;

	public MyCustomFrame(String title, int w, int h) {
		super(MAIN_TITLE + title);
		width = w;
		height = h;

		initComponent();
	}

	private void initComponent() {

		this.setBounds(0, 0, width, height);
		this.setLocationRelativeTo(null);
	}

}
