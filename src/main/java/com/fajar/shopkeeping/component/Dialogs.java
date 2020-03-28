package com.fajar.shopkeeping.component;

import javax.swing.JOptionPane;

public class Dialogs {
	
	private static void endLoading() {
		if(Loadings.isVisible()) {
			Loadings.end();
		}
	}
	
	public static void showInfoDialog(String message) {
		endLoading();
		JOptionPane.showMessageDialog(null, message);
	}
	
	public static void showErrorDialog(String message) {
		endLoading();
		JOptionPane.showMessageDialog(null, message);
	}

}
