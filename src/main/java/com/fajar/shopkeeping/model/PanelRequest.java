package com.fajar.shopkeeping.model;

import java.awt.Color;

import lombok.Data;
  
public class PanelRequest {
	public final int column;
	public final int width;
	public final int height;
	public final int margin;
	public final Color color;
	public final boolean autoScroll;

	public final int panelX;
	public final int panelY;
	public final int panelW;
	public final int panelH;
	private boolean centerAligment;
	
	public PanelRequest(int col, int w, int h, int margin, Color color, int panelX, int panelY, int panelW,
			int panelH, boolean autoScrool) {
		super();
		column = col;
		width = w;
		height = h;
		this.margin = margin;
		this.color = color;
		this.panelX = panelX;
		this.panelY = panelY;
		this.panelW = panelW;
		this.panelH = panelH;
		this.autoScroll = autoScrool;
	}

	public boolean isCenterAligment() {
		return centerAligment;
	}

	public void setCenterAligment(boolean centerAligment) {
		this.centerAligment = centerAligment;
	}
	
	

}
