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
public class MyCustomPanelv2 extends JPanel {

	
	private final transient Map<Integer, PanelRow> componentsMap;

	/**
	 * 
	 */
	private static final long serialVersionUID = -9045915483807077198L;

	int row, margin, customHeight, customWidth;
	int currentHeight;
	
	private boolean centerAligment;
	@Setter(value = AccessLevel.NONE)
	private final int[] colSizes;
	private final int columnCount;
	private final GridLayout gridLayout ;
	
	final List<Component> lastComponentEachRow = new ArrayList<Component>();

	public MyCustomPanelv2(int... colSizes) {
		super();
		
		this.colSizes = colSizes;
		this.columnCount = colSizes.length;
		this.componentsMap = new HashMap<Integer, PanelRow>();
		
		gridLayout = new GridLayout();
		gridLayout.setHgap(margin);
		gridLayout.setVgap(margin); 

	} 

	public void addComponent(Component component, int row) {
		if (componentsMap.get(row) == null) {
			componentsMap.put(row, new PanelRow());
		}

		componentsMap.get(row).addComponent(component);
	}

	private int getTotalComponents(){
		int total = 0;
		 
		for (Integer key : componentsMap.keySet()) {
			PanelRow panelRow = componentsMap.get(key); 
			total+=panelRow.getComponentCount(); 
		}
		return total;
	}
	
	private int getGridRows() {
		int totalComponent = getTotalComponents();
		int addition = totalComponent % columnCount > 0 ? 1 : 0;
		return totalComponent/columnCount + addition;
	}
	
	public void update() {
		setGridLayout();
		drawComponents();
		calculateWidthAndHeight();
	}

	private void setGridLayout() {
		gridLayout.setColumns(columnCount);
		gridLayout.setRows(getGridRows());
		setLayout(gridLayout);
	}

	public void setCenterAlignment(boolean centerAligment) {
		this.centerAligment = centerAligment;
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


	private void drawComponents() {
		this.removeAll();

		final Set<Integer> rows = componentsMap.keySet();

		for (Integer row : rows) {
			addComponents(componentsMap.get(row).getComponents());
		}
	} 
	
	private void addComponents(List<Component> components) {
		for (Component component : components) {
			add(component);
		}
	}
 

}
