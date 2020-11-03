package com.fajar.shopkeeping.component;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import com.fajar.shopkeeping.component.builder.PanelRow;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class MyCustomPanelv2 extends BaseCustomPanel {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -9045915483807077198L;

	
	
	private final GridLayout gridLayout ;
	
	final List<Component> lastComponentEachRow = new ArrayList<Component>();

	public MyCustomPanelv2(int... colSizes) {
		super(colSizes); 
		
		gridLayout = new GridLayout();
		gridLayout.setHgap(margin);
		gridLayout.setVgap(margin); 

	} 
	
	private int getGridRows() {
		int totalComponent = getTotalComponents();
		int addition = totalComponent % columnCount > 0 ? 1 : 0;
		return totalComponent/columnCount + addition;
	}
	
	public void update() {
		setGridLayout();
		allAllComponents();
		calculateWidthAndHeight();
	}

	private void setGridLayout() {
		gridLayout.setColumns(columnCount);
		gridLayout.setRows(getGridRows());
		setLayout(gridLayout);
	}
	
	private void calculateWidthAndHeight() {
		customHeight = calculatePanelHeight();
		customWidth = calculatePanelWidth();
	}

	private int calculatePanelHeight() { 
		int height = 0;
		for (Integer key : componentsMap.keySet()) {
			PanelRow panelRow = componentsMap.get(key);
			height+=margin+panelRow.getHeight();
		}
		return height;
	}
	
	private int calculatePanelWidth() {

		int width = 0;
		for(int size:colSizes) {
			width+=size+margin;
		}

		return width + margin;
	}
 

}
