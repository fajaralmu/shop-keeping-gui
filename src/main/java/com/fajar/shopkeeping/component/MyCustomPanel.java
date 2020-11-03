package com.fajar.shopkeeping.component;

import java.awt.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import com.fajar.shopkeeping.component.builder.PanelRow;
import com.fajar.shopkeeping.util.Log;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class MyCustomPanel extends JPanel {

	
	private final transient Map<Integer, PanelRow> componentsMap;

	/**
	 * 
	 */
	private static final long serialVersionUID = -9045915483807077198L;

	int row, margin, customHeight, customWidth;
	int currentHeight;
	
	private boolean centerAligment;
	@Setter(value = AccessLevel.NONE)
	final int[] colSizes;
	
	final List<Component> lastComponentEachRow = new ArrayList<Component>();

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
 
		calculateComponentsPosition();
		drawComponents();
	}

	public void setCenterAlignment(boolean centerAligment) {
		this.centerAligment = centerAligment;
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
		lastComponentEachRow.clear();

//		Log.log("----------------------start-------------------");
		for (Integer key : rows) {
//			Log.log(">>>>>>>>>>>>>>>ROW:", key);
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

				// update location
				updateComponentLocation(key, i, x, y);

			}
			lastComponentEachRow.add(getPanelRowComponents(key).get(components.size() - 1));
			currentHeight = currentHeight + margin + rowHeight;
		}
		 
		calculateWidthAndHeight();

		Log.log("-----------------end--------------- size", customWidth, "x", customHeight);
	}
	
	private void updateComponentLocation(int key, int listIndex, int x, int y) { 
		componentsMap.get(key).getComponents().get(listIndex).setLocation(x, y);
	}

	private PanelRow getPanelRow(Integer key) {
		return componentsMap.get(key);
	}
	
	private List<Component> getPanelRowComponents(Integer key) {
		return getPanelRow(key).getComponents();
	}

	private void resetDimensions() {
		 
		currentHeight = 0;
		customHeight = 0;
		customWidth = 0;
	}

	private void calculateWidthAndHeight() {
		 
		customHeight = calculateHeight();
		customWidth = calculateWidth();
	}

	private int calculateHeight() { 
		return currentHeight + margin * 2; 
	}

	private int calculateWidth() {

		int width = 0;
		for (Component component : lastComponentEachRow) {
			int componentWidth = component.getX() + component.getWidth();
			if (componentWidth > width) {
				width = componentWidth;
			}
		} 

		return width + margin;
	}

	
}
