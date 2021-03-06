package com.fajar.shopkeeping.model;

import java.awt.Color;

import lombok.Data;

@Data
public class PanelRequest {
	public final int column;
	public final int width;
	public final int height;
	public final int margin;
	private Color color;
	private boolean autoScroll;

	public final int panelX;
	public final int panelY;
	public final int panelW;
	public final int panelH;
	public final int[] colSizes;
	private boolean centerAligment;

	public static PanelRequest autoPanelNonScrollXYSpecified(int col, int colWidth, int margin, Color color, int panelX,
			int panelY) {
		return new PanelRequest(col, colWidth, 0, margin, color, panelX, panelY, 0, 0, false);
	}
	public static PanelRequest autoPanelNonScrollXYSpecified(int[] colSizes, int margin, Color color, int panelX,
			int panelY) {
		return new PanelRequest(colSizes, 0, margin, color, panelX, panelY, 0, 0, false);
	}
	
	public static int[] intArray(int...ints) {
		
		int[] array = new int[ints.length];
		for (int i = 0; i < ints.length; i++) {
			array[i] = ints[i];
		}
		return array ;
	}
	
	public static PanelRequest autoPanelNonScroll(int col, int colWidth, int margin, Color color ) {
		return new PanelRequest(col, colWidth, 0, margin, color, 0, 0, 0, 0, false);
	}
	public static PanelRequest autoPanelNonScroll(int[] colSizes, int margin, Color color ) {
		return new PanelRequest(colSizes, 0, margin, color, 0, 0, 0, 0, false);
	}

	public static PanelRequest autoPanelScroll(int col, int colWidth, int margin, Color color, int panelH) {
		return new PanelRequest(col, colWidth, 0, margin, color, 0, 0, 0, panelH, true);
	}
	public static PanelRequest autoPanelScroll(int[] colSizes, int margin, Color color, int panelH) {
		return new PanelRequest(colSizes, 0, margin, color, 0, 0, 0, panelH, true);
	}
	
	public static PanelRequest autoPanelScrollWidthHeightSpecified(int col, int colWidth, int margin, Color color, int panelW, int panelH) {
		return new PanelRequest(col, colWidth, 0, margin, color, 0, 0, panelW, panelH, true);
	}
	public static PanelRequest autoPanelScrollWidthHeightSpecified(int[] colSizes, int margin, Color color, int panelW, int panelH) {
		return new PanelRequest(colSizes, 0, margin, color, 0, 0, panelW, panelH, true);
	}
	
	public static PanelRequest autoPanelScrollXYSpecified(int col, int colWidth, int margin, Color color, int panelX, int panelY, int panelH) {
		return new PanelRequest(col, colWidth, 0, margin, color, panelX, panelY, 0, panelH, true);
	}
	public static PanelRequest autoPanelScrollXYSpecified(int[] colSizes, int margin, Color color, int panelX, int panelY, int panelH) {
		return new PanelRequest(colSizes, 0, margin, color, panelX, panelY, 0, panelH, true);
	}
	protected static int[] reverseArray(int[] array) {
		
		int[] newArray= new int[array.length]; 
		for (int i = array.length - 1; i >= 0; i--) {
			newArray[array.length - i - 1] = array[i]; 
		}
		return array;
//		return newArray;
	}
 
	public PanelRequest(int col, int w, int h, int margin, Color color, int panelX, int panelY, int panelW, int panelH,
			boolean autoScrool) {
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
		this.colSizes = new int[] {};
	}
	
	public PanelRequest(int[] colsizes, int h, int margin, Color color, int panelX, int panelY, int panelW, int panelH,
			boolean autoScrool) {
		super();
		column = 0;
		width = 0;
		height = h;
		this.margin = margin;
		this.color = color;
		this.panelX = panelX;
		this.panelY = panelY;
		this.panelW = panelW;
		this.panelH = panelH;
		this.autoScroll = autoScrool;
		this.colSizes = reverseArray(colsizes);
	}

	public PanelRequest(int col, int w, int h, int margin, Color color, int panelX, int panelY, int panelW, int panelH,
			boolean autoScrool, boolean centerAligment) {
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
		this.centerAligment = centerAligment;
		this.colSizes = new int[] {};
	}
	
	 
	public PanelRequest(int[] colSizes, int h, int margin, Color color, int panelX, int panelY, int panelW, int panelH,
			boolean autoScrool, boolean centerAligment) {
		super();
		column = 0;
		width = 0;
		height = h;
		this.margin = margin;
		this.color = color;
		this.panelX = panelX;
		this.panelY = panelY;
		this.panelW = panelW;
		this.panelH = panelH;
		this.autoScroll = autoScrool;
		this.centerAligment = centerAligment;
		this.colSizes = reverseArray(colSizes);
	}

	public boolean isCenterAligment() {
		return centerAligment;
	}

	public void setCenterAligment(boolean centerAligment) {
		this.centerAligment = centerAligment;
	}

}
