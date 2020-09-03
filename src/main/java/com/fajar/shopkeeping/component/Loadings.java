package com.fajar.shopkeeping.component;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;

import com.fajar.shopkeeping.component.builder.ComponentBuilder;
import com.fajar.shopkeeping.model.PanelRequest;

public class Loadings {

	private static final JFrame loadingFrame = loadingFrame();
	private static int loadingCount = 0;

	private static JFrame loadingFrame() {
		JFrame jframe = new JFrame("loading... ");
		jframe.setBounds(0, 0, 400, 100);
		jframe.setLocationRelativeTo(null); 
		jframe.setUndecorated(true);
		jframe.setAlwaysOnTop(true);
		
		JLabel loadingContent = ComponentBuilder.title("Please Wait " , 20); 
		loadingContent.setForeground(Color.white);
		
		PanelRequest panelRequest = PanelRequest.autoPanelNonScroll(1, 300, 20, new Color(212,212,212));
		MyCustomPanel loadingPanel = ComponentBuilder.buildPanelV2(panelRequest , loadingContent); 
		jframe.setContentPane(loadingPanel);
		return jframe;
	}
	
//	public static void main(String[] args) {
//		start();
//	}

	public static boolean isVisible() {
		return loadingFrame.isVisible();
	}
	
	public static void start() {
		loadingFrame.setVisible(true);
		loadingCount++;
	}

	public static void end() {
		loadingCount--;
		if (loadingFrame.isVisible() && loadingCount <= 0)
			loadingFrame.setVisible(false);
	}

}
