package com.fajar.shopkeeping.component;

import java.awt.Component;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import com.fajar.shopkeeping.component.builder.PanelRow;
import com.fajar.shopkeeping.util.Log;
import com.fajar.shoppingmart.util.CollectionUtil;

import lombok.Data;

@Data
public class MyCustomPanel extends BaseCustomPanel { 

	/**
	 * 
	 */
	private static final long serialVersionUID = -9145915483807077198L;   

	public MyCustomPanel(int... colSizes) {
		super(colSizes); 

	} 
	public void update() {
 
		calculateComponentsPosition();
		calculateWidthAndHeight();
		allAllComponents();
	}
	
	private int getXPosition(int columnIndex, Component component) {
		int xPosition = margin;
		for (int i = 0; i < columnIndex; i++) {
			xPosition = xPosition + colSizes[i];
			xPosition += margin;

		}
		if (centerAligment) {
			int columnWidth = colSizes[columnIndex];
			int gap = columnWidth - component.getWidth();
			xPosition += new BigDecimal(gap / 2).intValue();
		}
		return xPosition;
	}

	private void calculateComponentsPosition() {

		resetDimensions(); 
		
		final Set<Integer> rows = componentsMap.keySet();
		for (Integer key : rows) {
			PanelRow panelRow = componentsMap.get(key);

			int rowHeight = panelRow.getHeight();
			List<Component> components = panelRow.getComponents();

			loop: for (int i = 0; i < components.size(); i++) {

				final Component component = components.get(i);
				if (component == null) {
					continue loop;
				}

				int y = currentHeight + margin;
				int x = 0;
				final int columnSize = colSizes[i];

				try {
					Log.log("columnSize: ", columnSize);
					x = getXPosition(i, component);

				} catch (Exception e) {
					e.printStackTrace();
					continue loop;
				}
				updateComponentBounds(key, i, x, y);

			} 
			currentHeight = currentHeight + margin + rowHeight;
		} 
		Log.log("-----------------end--------------- size", customWidth, "x", customHeight);
	}
	
	private void updateComponentBounds(int key, int listIndex, int x, int y) { 
		componentsMap.get(key).getComponents().get(listIndex).setLocation(x, y);
	} 

	private void resetDimensions() {
		 
		currentHeight = 0;
		customHeight = 0;
		customWidth = 0;
	}

	private void calculateWidthAndHeight() {
		 
		customHeight = calculatePanelHeight();
		customWidth = calculatePanelWidth();
	}

	private int calculatePanelHeight() { 
		final Set<Integer> rows = componentsMap.keySet();  
		int totalHeight = 0; 
		for (Integer key : rows) { 
			PanelRow panelRow = componentsMap.get(key);
			totalHeight+=panelRow.getHeight()+margin;
		}
		return totalHeight;
	}

	private int calculatePanelWidth() { 
		return PanelRow.getMaxRightOffset(CollectionUtil.mapToList(componentsMap));
	}

	
}
