package com.fajar.shopkeeping.component;

import javax.swing.JFrame;

public class Loadings {

	private static final JFrame loadingFrame = loadingFrame();

	private static JFrame loadingFrame() {
		JFrame jframe = new JFrame("loading... ");
		jframe.setBounds(0, 0, 400, 100);
		jframe.setLocationRelativeTo(null);
		return jframe;
	}

	public static boolean isVisible() {
		return loadingFrame.isVisible();
	}
	
	public static void start() {
		loadingFrame.setVisible(true);
	}

	public static void end() {
		if (loadingFrame.isVisible())
			loadingFrame.setVisible(false);
	}

}
