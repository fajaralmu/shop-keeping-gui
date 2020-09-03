package com.fajar.shopkeeping.component;

import javax.swing.JOptionPane;

import com.fajar.shopkeeping.util.Log;

public class Dialogs {
	private static void endLoading() {
		if (Loadings.isVisible()) {
			Loadings.end();
		}
	}

	public static void info(Object... messages) {
		endLoading();
		StringBuilder sb = new StringBuilder();
		for (Object string : messages) {
			if(null == string) {
				string = "";
			}
			sb.append(string.toString());
		}
		Log.log("INFO: ", sb.toString());
		JOptionPane.showMessageDialog(null, sb.toString());
	}

	public static void error(Object... messages) {
		endLoading();
		StringBuilder sb = new StringBuilder();
		for (Object string : messages) {
			if(null == string) {
				string = "";
			}
			sb.append(string.toString());
		}
		Log.log("ERROR: ",sb.toString());
		JOptionPane.showMessageDialog(null, sb.toString());
	}
	
	public static int confirm(String message) {
		int confirm = JOptionPane.showConfirmDialog(null, message); 
		 
		return confirm;
	}

	public static String input(String string) {
		return JOptionPane.showInputDialog(string); 
	}

}
