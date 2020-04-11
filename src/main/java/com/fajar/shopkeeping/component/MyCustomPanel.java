package com.fajar.shopkeeping.component;

import java.awt.Component;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JLabel;
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
	int row;
	int margin;
	@Setter(value = AccessLevel.NONE)
	final int[] colSizes;

	int customHeight;
	int customWidth;
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

				if (component == null) {

					continue loop;
				}
				add(component);
			}
		}
	}
	
	private int getXPosition(int columnIndex, Component component) {
		int xPosition = margin;
		for(int i = 0; i<  columnIndex; i++) {
			xPosition = xPosition + colSizes[i];
			xPosition += margin;
			 
		}
		if(centerAligment) {
			int columnWidth = colSizes[columnIndex];
			int gap = columnWidth - component.getWidth();
			xPosition +=  new BigDecimal(gap /2 ).intValue();
		} 
		return xPosition;
	}

	private void calculateComponentsPosition() {

		int currentHeight = 0;
		final Set<Integer> rows = componentsMap.keySet();
		Log.log("----------------------start-------------------");
		for (Integer key : rows) {
			Log.log(">>>>>>>>>>>>>>>ROW:", key);
			PanelRow panelRow = componentsMap.get(key);
			
			int rowHeight = panelRow.getHeight();
			List<Component> components = panelRow.getComponents();

			loop: for (int i = 0; i < components.size(); i++) {
				
				final boolean fristElement = i == 0;
				final Component component = components.get(i);
				if (component == null) {
					continue loop;
				}

				int y = currentHeight + margin;
				int x = 0;
				final int columnSize = colSizes[i];
				
				try {
					
					final int previousColumnSize = fristElement ? 0 : colSizes[i - 1];
					
					Log.log("columnSize: ", columnSize);
					x =  getXPosition(i, component);
//					x = x + margin;
//
//					if (this.centerAligment) {
//						int gap = columnSize - component.getWidth();
//						x = (i * previousColumnSize) + new BigDecimal(gap / 2).intValue();
//					}
					
				} catch (Exception e) {
					e.printStackTrace();
					continue loop;
				}
				try {
					Log.log("label: ", ((JLabel) component).getText());
				} catch (Exception e) {
					// TODO: handle exception
				}
				Log.log("x:",x,"columnSize:",columnSize);
				// update location
				componentsMap.get(key).getComponents().get(i).setLocation(x, y);

			}

			currentHeight = currentHeight + margin + rowHeight; 
		}
		customHeight = currentHeight + margin;
		customWidth = calculateWidth();

		Log.log("-----------------end--------------- size", customWidth, "x", customHeight);
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

		int width =  margin;

		for (int colSize : colSizes) {
			width += colSize + margin;
//			if (!centerAligment) {
//				width += margin * 2;
//			}
		}

		return width + margin;
	}

	public static int getMaxHeight(List<Component> components) {
		int maxHeight = Integer.MIN_VALUE;

		for (Component component : components) {
			if(null == component) {
				continue;
			}
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
