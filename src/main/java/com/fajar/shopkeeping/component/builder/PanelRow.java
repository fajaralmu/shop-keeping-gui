package com.fajar.shopkeeping.component.builder;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * used when constructing dynamic JPanel
 * @author Republic Of Gamers
 *
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PanelRow {

	private int row;
//	private int height;
//	private int width;
	@Builder.Default
//	@Getter(value = AccessLevel.NONE)
	private final List<Component> components = new ArrayList<>();
	
	public int getHeight() {
		return getMaxHeight(components);
	}
	
	private Component getLastComponent() {
		return components.get(components.size()-1);
	}
	
	public int getMaxRightOffset() {
		Component lastComponent = getLastComponent();
		return lastComponent.getX()+lastComponent.getWidth();
	}
	
	public static int getMaxRightOffset(List<PanelRow> panelRows) {
		int maxRightOffet = 0;
		
		for (PanelRow panelRow : panelRows) {
			if(panelRow.getMaxRightOffset()>maxRightOffet) {
				maxRightOffet = panelRow.getMaxRightOffset();
			}
		}
		return maxRightOffet;
	}
	
	private int getMaxHeight(List<Component> components) {
		int maxHeight = 0;
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

	public int getComponentCount() {
		return components.size();
	}

	public void addComponent(Component component) { 
		if(null == component) {
			return;
		}
		components.add(component);
	}

}
