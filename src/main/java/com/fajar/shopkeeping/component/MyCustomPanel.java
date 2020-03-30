package com.fajar.shopkeeping.component;

import java.awt.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class MyCustomPanel extends JPanel {

	private Map<Integer, PanelRow> componentsMap;

	/**
	 * 
	 */
	private static final long serialVersionUID = -9045915483807077198L;
	int row;
	int margin;
	@Setter(value = AccessLevel.NONE)
	final int[] colSizes;

	int height;
	int width;
	boolean centerAligment;

	public MyCustomPanel(int... colSizes) {
		super();
		this.colSizes = colSizes;
		componentsMap = new HashMap<Integer, PanelRow>();

	}

	public void addComponent(Component component, int row) {
		if (componentsMap.get(row) == null) {
			componentsMap.put(row, new PanelRow());
		}

		componentsMap.get(row).getComponents().add(component);
	}

	public void update() { 
		 
		setHeightsForEachRows();
		calculateComponentsPosition();
		drawComponents();
	}
	
	public void setCenterAlignment(boolean centerAligment) {
		this.centerAligment = centerAligment; 
	}

	private void drawComponents() {
		this.removeAll();
		
		final Set<Integer> rows = componentsMap.keySet(); 
		
		for (Integer key : rows) {
			PanelRow panelRow = componentsMap.get(key);
			loop: for (Component component : panelRow.getComponents()) {
				 
				if(component == null) {
					
					continue loop;
				}
				add(component);
			}
		} 
	}

	private void calculateComponentsPosition() {
		
		int currentHeight = 0; 
		final Set<Integer> rows = componentsMap.keySet(); 
		
		for (Integer key : rows) {
			
			PanelRow panelRow = componentsMap.get(key);
			int rowHeight = panelRow.getHeight(); 
			List<Component> components = panelRow.getComponents(); 
			
			loop: for (int i = 0; i < components.size(); i++) {
				
				Component component = components.get(i); 
				if(component == null) {
					continue loop;
				}
				
				int y = currentHeight;
				int x = 0;
				
				try {
					int columnSize = colSizes[i ];
					x = i == 0 ? 0 : columnSize ;
					x = x + margin;
					
					if(this.centerAligment) {
						int gap = columnSize - component.getWidth();
						x = new BigDecimal(gap/2).intValue();
					}
					
				} catch (Exception e) { 
					e.printStackTrace();
					continue loop;
				} 
				
				//update location 
				componentsMap.get(key).getComponents().get(i).setLocation(x, y); 
				
			}
			
			currentHeight = currentHeight + margin + rowHeight;  
		}
		height = currentHeight;
		width = calculateWidth(); 
		
	}

	/**
	 * getColumnSize
	 * @param columnIndex begin at 0
	 * @return
	 */
	private int getColumnSize(int columnIndex) {
		return colSizes[columnIndex];
	}
	
	private void setHeightsForEachRows() {
		final Set<Integer> rows = componentsMap.keySet(); 
		/**
		 * setting the height
		 */
		for (Integer key : rows) {
			PanelRow panelRow = componentsMap.get(key);
			List<Component> components = panelRow.getComponents();
			int maxHeight = getMaxHeight(components); 
			
			componentsMap.get(key).setHeight(maxHeight);
		}
		
	}

	private int calculateWidth() {
		int width = 0;

		for (int colSize : colSizes) {
			width += colSize;
		}

		return width;
	}

	public static int getMaxHeight(List<Component> components) {
		int maxHeight = Integer.MIN_VALUE;

		for (Component component : components) {
			int componentHeight = component.getHeight();
			if (componentHeight > maxHeight) {
				maxHeight = componentHeight;
			}
		}

		return maxHeight;
	}

	@Override
	public Component add(Component comp) {
		return super.add(comp);
	}

	@Override
	public void removeAll() {
		super.removeAll(); 
	}

}
