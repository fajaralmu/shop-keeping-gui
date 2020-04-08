package com.fajar.shopkeeping.component;

import java.awt.Component;

public class ComponentModifier {

	/**
	 * set new X and new Y only
	 * @param newX
	 * @param newY
	 * @param component
	 */
	public static void updatePosition(int newX, int newY, Component component) {
		component.setBounds(newX, newY, component.getWidth(), component.getHeight());
	}
	
	/**
	 * set new X and Y based on refference component
	 * @param toBeModifiedComponent
	 * @param refferenceComponent
	 */
	public static void updatePosition(Component toBeModifiedComponent, Component refferenceComponent) {
		toBeModifiedComponent.setBounds(refferenceComponent.getX(), refferenceComponent.getY(), toBeModifiedComponent.getWidth(), toBeModifiedComponent.getHeight());
	}
	
}
