package com.fajar.shopkeeping.model;

import java.awt.Color;

import lombok.Builder;
import lombok.Data;

@Data 
public class PanelRequest {
	public final int Col;
	public final int W;
	public final int H;
	public final int Margin;
	public final Color color;
	public final boolean autoScroll;

	public final int panelX;
	public final int panelY;
	public final int panelW;
	public final int panelH;
	public boolean centerAligment;
	
	public PanelRequest(int col, int w, int h, int margin, Color color, int panelX, int panelY, int panelW,
			int panelH, boolean autoScrool) {
		super();
		Col = col;
		W = w;
		H = h;
		Margin = margin;
		this.color = color;
		this.panelX = panelX;
		this.panelY = panelY;
		this.panelW = panelW;
		this.panelH = panelH;
		this.autoScroll = autoScrool;
	}
	
	

}
