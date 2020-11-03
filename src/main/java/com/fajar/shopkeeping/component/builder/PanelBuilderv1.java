package com.fajar.shopkeeping.component.builder;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.fajar.shopkeeping.component.BlankComponent;
import com.fajar.shopkeeping.model.PanelRequest;

public class PanelBuilderv1 {
	private final PanelRequest panelRequest;
	private final Component[] components;
	private final int COLUMN_COUNT;
	private final int width;
	private final int height;
	private final int margin;
	private final Color color;
	private final int size;

	public PanelBuilderv1(PanelRequest panelRequest, Component[] components) {
		this.panelRequest = panelRequest;
		this.components = components;
		this.COLUMN_COUNT = panelRequest.column;
		this.width = panelRequest.width;
		this.height = panelRequest.height;
		this.margin = panelRequest.margin;
		this.color = panelRequest.getColor();
		this.size = components.length;
		init();
	}

	private void init() {

	}

	public JPanel buildPanel() {

		int panelX = panelRequest.panelX;
		int panelY = panelRequest.panelY;
		int panelW = panelRequest.panelW;
		int panelH = panelRequest.panelH;
		boolean autoScroll = panelRequest.isAutoScroll();

		JPanel panel = new JPanel();
		int currentColumn = 0;
		int currentRow = 0;

		for (int i = 0; i < size; i++) {

			Component component = components[i];

			if (null == component) {
				component = new JLabel();
			}

			int x = currentColumn * margin + (width * currentColumn);
			int y = currentRow * margin + height * currentRow;

			component.setLocation(x, y);
			component.setSize(width, height);

			component = processBlankComponent(component, panel, i);

			currentColumn++;

			if (currentColumn == COLUMN_COUNT) {
				currentColumn = 0;
				currentRow++;

			}
			// printComponentLayout(C);
			panel.add(component);
		}

		final int X = panelX == 0 ? margin : panelX;
		final int Y = panelY == 0 ? margin : panelY;
		final int finalW = panelW != 0 ? panelW : COLUMN_COUNT * width + COLUMN_COUNT * margin;
		final int finalH = panelH != 0 ? panelH : (currentRow + 1) * height + (currentRow + 1) * margin;

		panel.setBackground(color);
		panel.setBounds(X, Y, finalW, finalH);
//		panel.setLayout(null);
		panel.setBounds(X, Y, finalW, finalH);
		panel.setSize(finalW, finalH);

//		if (autoScroll) {
		panel.setAutoscrolls(false);
		panel.setAutoscrolls(autoScroll);
//		}
		System.out.println("Generated Panel x:" + X + ", y:" + Y + ", width:" + finalW + ", height:" + finalH);
		return panel;

	}

	private Component processBlankComponent(Component component, JPanel panel, int i) {
		if (!component.getClass().equals(BlankComponent.class)) {
			return component;
		}
		BlankComponent blankComponent = (BlankComponent) component;

		switch (blankComponent.reserved) {

		case BEFORE_HOR:

			Component beforeContHor = components[i - 1];

			beforeContHor.setBounds(beforeContHor.getX(), beforeContHor.getY(),
					beforeContHor.getWidth() + blankComponent.getWidth(), beforeContHor.getHeight());

			panel.remove(beforeContHor);
			components[i] = beforeContHor;
			component = beforeContHor;
			break;

		case BEFORE_VER:
			Component beforeContVer = components[i - COLUMN_COUNT];

			beforeContVer.setBounds(beforeContVer.getX(), beforeContVer.getY(), beforeContVer.getWidth(),
					beforeContVer.getHeight() + blankComponent.getHeight());

			panel.remove(beforeContVer);
			components[i] = beforeContVer;
			component = beforeContVer;
			break;

		case AFTER_HOR:
		case AFTER_VER:
		default:
			break;
		}
		return component;
	}

}
