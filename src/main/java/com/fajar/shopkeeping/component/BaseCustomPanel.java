package com.fajar.shopkeeping.component;

import java.awt.Component;
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
public class BaseCustomPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 726668275747908912L;
	protected final transient Map<Integer, PanelRow> componentsMap;
	protected int row, margin, customHeight, customWidth;
	protected int currentHeight;
	@Setter(value = AccessLevel.NONE)
	protected final int[] colSizes;
	protected final int columnCount;
	
	protected boolean centerAligment;
	
	public BaseCustomPanel(int... colSizes) {
		super();
		this.colSizes = colSizes;
		this.columnCount = colSizes.length;
		this.componentsMap = new HashMap<Integer, PanelRow>();
	}

	public void setCenterAlignment(boolean centerAligment) {
		this.centerAligment = centerAligment;
	}
	
	public void addComponent(Component component, int row) {
		if (componentsMap.get(row) == null) {
			componentsMap.put(row, new PanelRow());
		}

		componentsMap.get(row).getComponents().add(component);
	}
	
	protected int getTotalComponents(){
		int total = 0;
		 
		for (Integer key : componentsMap.keySet()) {
			PanelRow panelRow = componentsMap.get(key); 
			total+=panelRow.getComponentCount(); 
		}
		return total;
	}
	
	protected void addComponents(List<Component> components) {
		for (Component component : components) {
			add(component);
		}
	}

	protected void allAllComponents() {
		this.removeAll();

		final Set<Integer> rows = componentsMap.keySet();

		for (Integer row : rows) {
			addComponents(componentsMap.get(row).getComponents());
		}
	}
}
