package com.fajar.shopkeeping.component;

import javax.swing.JOptionPane;

public class Dialogs {
	private static void endLoading() {
		if (Loadings.isVisible()) {
			Loadings.end();
		}
	}

	public static void showInfoDialog(Object... messages) {
		endLoading();
		StringBuilder sb = new StringBuilder();
		for (Object string : messages) {
			if(null == string) {
				string = "";
			}
			sb.append(string.toString());
		}
		JOptionPane.showMessageDialog(null, sb.toString());
	}

	public static void showErrorDialog(Object... messages) {
		endLoading();
		StringBuilder sb = new StringBuilder();
		for (Object string : messages) {
			if(null == string) {
				string = "";
			}
			sb.append(string.toString());
		}
		JOptionPane.showMessageDialog(null, sb.toString());
	}

}
