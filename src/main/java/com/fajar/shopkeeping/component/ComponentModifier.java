package com.fajar.shopkeeping.component;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JScrollPane;

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
	
	/**
	 * update scrollable content
	 * @param scrollPane
	 * @param component
	 * @param newDimension
	 */
	public static void updateScrollPane(JScrollPane scrollPane, Component component, Dimension newDimension) {
		component.setPreferredSize(newDimension);
		component.setSize(newDimension);
		scrollPane.setViewportView(component);
		scrollPane.validate(); 
	}
	
	public static void changeSize(Component component, int width, int height) {

		component.setBounds(component.getX(), component.getY(), width, height);
	}

	public static void changeSizeHeight(Component component, int height) {

		component.setBounds(component.getX(), component.getY(), component.getWidth(), height);
	}
}
