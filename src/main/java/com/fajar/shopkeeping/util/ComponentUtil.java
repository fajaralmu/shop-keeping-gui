package com.fajar.shopkeeping.util;

import java.awt.Component;
import java.util.List;

public class ComponentUtil {

	public static Component[] toArrayOfComponent(List<Component> formComponents) {

		Component[] components = new Component[formComponents.size()];
		for (int i = 0; i < formComponents.size(); i++) {
			components[i] = formComponents.get(i);
		}
		return components;
	}
}
