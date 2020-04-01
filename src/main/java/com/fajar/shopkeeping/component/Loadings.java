package com.fajar.shopkeeping.component;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;

import com.fajar.shopkeeping.model.PanelRequest;

public class Loadings {

	private static final JFrame loadingFrame = loadingFrame();

	private static JFrame loadingFrame() {
		JFrame jframe = new JFrame("loading... ");
		jframe.setBounds(0, 0, 400, 100);
		jframe.setLocationRelativeTo(null);
		jframe.setBackground(Color.LIGHT_GRAY);
		jframe.setUndecorated(true);
		jframe.setAlwaysOnTop(true);
		
		JLabel loadingContent = ComponentBuilder.title("Please Wait", 20); 
		loadingContent.setForeground(Color.white);
		
		PanelRequest panelRequest = PanelRequest.autoPanelNonScroll(1, 300, 20, Color.LIGHT_GRAY);
		MyCustomPanel loadingPanel = ComponentBuilder.buildPanelV2(panelRequest , loadingContent);
		jframe.setContentPane(loadingPanel);
		return jframe;
	}
	
	public static void main(String[] args) {
		start();
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
