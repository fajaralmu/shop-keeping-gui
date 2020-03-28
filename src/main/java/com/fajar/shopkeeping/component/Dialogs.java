package com.fajar.shopkeeping.component;

import javax.swing.JOptionPane;

public class Dialogs {
	
	static JOptionPane jOptionPane;
	
	private static void endLoading() {
		if(Loadings.isVisible()) {
			Loadings.end();
		}
	}
	
	public static void showInfoDialog(String message) {
		endLoading();
		jOptionPane.showMessageDialog(null, message);
	}
	
	public static void showErrorDialog(String message) {
		endLoading();
		jOptionPane.showMessageDialog(null, message);
	}

}
