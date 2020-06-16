package com.fajar.shopkeeping.component;

import java.awt.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import com.fajar.shopkeeping.util.Log;

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

				if (component == null) {

					continue loop;
				}
				add(component);
			}
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
				componentsMap.get(key).getComponents().get(i).setLocation(x, y);

			}
			lastComponentEachRow.add(componentsMap.get(key).getComponents().get(components.size() - 1));
			currentHeight = currentHeight + margin + rowHeight;
		}
		 
		calculateWidthAndHeight();

		Log.log("-----------------end--------------- size", customWidth, "x", customHeight);
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
		for (Component component : lastComponentEachRow) {
			int componentWidth = component.getX() + component.getWidth();
			if (componentWidth > width) {
				width = componentWidth;
			}
		}

//		for (int colSize : colSizes) {
//			width += margin + colSize + margin;
////			if (!centerAligment) {
////				width += margin * 2;
////			}
//		}

		return width + margin;
	}

	public static int getMaxHeight(List<Component> components) {
		int maxHeight = Integer.MIN_VALUE;

		for (Component component : components) {
			if (null == component) {
				continue;
			}
			int componentHeight = component.getHeight();
			if (componentHeight > maxHeight) {
				maxHeight = componentHeight;
			}
		}

		return maxHeight;
	}

}
