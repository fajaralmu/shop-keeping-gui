package com.fajar.shopkeeping.component;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;

import com.fajar.shopkeeping.component.builder.ComponentBuilder;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.util.ThreadUtil;

public class Loadings {

	private static JFrame loadingFrame = null;
	private static int loadingCount = 0;
	private static JLabel loadingContent = null; 
	
	private static void setLoadingFrame() {
		if(null == loadingFrame) {
			loadingFrame = loadingFrame();
		}
	}

	private static JLabel getLoadingContent() {
		if(loadingContent == null) {
			loadingContent = ComponentBuilder.title("Please Wait.." , 20);
		}
		loadingContent.setForeground(Color.white);
		return loadingContent;
	}
	
	private static JFrame loadingFrame() {
		getLoadingContent();
		JFrame jframe = new JFrame("loading... ");
		jframe.setBounds(0, 0, 400, 100);
		jframe.setLocationRelativeTo(null); 
		jframe.setUndecorated(true);
		jframe.setAlwaysOnTop(true); 
		
		
		PanelRequest panelRequest = PanelRequest.autoPanelNonScroll(1, 300, 20, new Color(212,212,212));
		JPanel loadingPanel = ComponentBuilder.buildPanelV2(panelRequest , loadingContent); 
		
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
		setLoadingFrame();
		loadingFrame.setVisible(true);
		loadingCount++;
		ThreadUtil.run(()->{
			animate();
		});
	}
	private static void animate() {
		getLoadingContent();
		int maxSpace = 10;
		loadingContent.setText("Please Wait "+StringUtils.repeat("  ", maxSpace));
		final long delta = 100;
		long currentTime = System.currentTimeMillis();
		int tick = 0;
		while(loadingFrame.isVisible()) {
			long now = System.currentTimeMillis();
			if(now-currentTime < delta) {
				continue;
			}
			tick =  (tick > maxSpace) ? 1:tick+1;
			
			currentTime = now;
			final String dots = StringUtils.repeat(". ", tick)+StringUtils.repeat("  ", maxSpace-tick);
			ThreadUtil.run(()->{
				loadingContent.setText("Please Wait "+dots);
			});
		}
	}
	

	public static void end() {
		loadingCount--;
		if (loadingFrame.isVisible() && loadingCount <= 0)
			loadingFrame.setVisible(false);
	}

}
