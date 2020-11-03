package com.fajar.shopkeeping.component.builder;

import java.awt.Color;
import java.awt.Component;
import java.util.List;

import com.fajar.shopkeeping.model.PanelRequest;

import lombok.Data;

@Data
public abstract class BasePanelBuilder<T> {
	 
	protected final T[] components; 
	protected final PanelRequest panelRequest; 
	protected final Color color; 
	protected final int COLUMN_COUNT;
	
	public BasePanelBuilder(PanelRequest panelRequest, Color color, T[] components) {
		this.components = components;
		this.panelRequest = panelRequest;
		this.color = color;
		this.COLUMN_COUNT = getColumnCount();
	}

	protected abstract int getColumnCount();
	
	protected int getComponentsSize() {
		return getComponents().length;
	}
	
	protected T[] getComponents() {
		return components;
	}
	
}
