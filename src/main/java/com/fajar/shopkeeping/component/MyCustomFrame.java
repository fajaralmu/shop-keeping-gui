package com.fajar.shopkeeping.component;

import javax.swing.JFrame;

public class MyCustomFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4704693551064670453L;
	private static final String MAIN_TITLE = "Shop Keeping::";

	public MyCustomFrame(String title) {
		super(MAIN_TITLE + title);

		initComponent();
	}

	private void initComponent() {
		this.setLocationRelativeTo(null);
	}

}
