package com.fajar.shopkeeping.component;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.util.StringUtil;

public class ComponentBuilder {

	public static JPanel buildPanel(PanelRequest panelRequest, Component... components) {

		int column = panelRequest.column;
		int width = panelRequest.width;
		int height = panelRequest.height;
		int margin = panelRequest.margin;
		Color color = panelRequest.color;

		int panelX = panelRequest.panelX;
		int panelY = panelRequest.panelY;
		int panelW = panelRequest.panelW;
		int panelH = panelRequest.panelH;
		boolean autoScroll = panelRequest.autoScroll;

		JPanel panel = new JPanel();
		int currentColumn = 0;
		int currentRow = 0;
		int size = components.length; 

		for (int i = 0; i < size; i++) {

			Component component = components[i];

			if (null != component) {

				// C.setBounds(CurrentCol * Margin + (W * CurrentCol), CurrentRow * Margin + H *
				// CurrentRow, W, H);

				component.setLocation(currentColumn * margin + (width * currentColumn), currentRow * margin + height * currentRow);
				component.setSize(width, height);
				
				if (component.getClass().equals(BlankComponent.class)) {
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
						Component beforeContVer = components[i - column];

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
				}

			} else {
				component = new JLabel();
			}
			currentColumn++;

			if (currentColumn == column) {
				currentColumn = 0;
				currentRow++;

			}
			//printComponentLayout(C);
			panel.add(component);
		}

		
		final int X = panelX == 0 ? margin : panelX;
		final int Y = panelY == 0 ? margin : panelY;
		final int finalW = panelW != 0 ? panelW : column * width + column * margin;
		final int finalH = panelH != 0 ? panelH : (currentRow + 1) * height + (currentRow + 1) * margin;
		
		panel.setBackground(color);
		panel.setBounds(X, Y, finalW, finalH);
		panel.setLayout(null);
		panel.setBounds(X, Y, finalW, finalH);
		panel.setSize(finalW, finalH);
		
		if (autoScroll) {
			panel.setAutoscrolls(false);
			panel.setAutoscrolls(true);
		}
		System.out.println("Generated Panel x:" + X + ", y:" + Y + ", width:" + finalW + ", height:" + finalH);

		return panel;
	}

	public static MyCustomPanel buildPanelV2(PanelRequest panelRequest, Component... components) {
		System.out.println("======v2=======");

		int column = panelRequest.column;
		int width = panelRequest.width;
		int height = panelRequest.height;
		int margin = panelRequest.margin;
		Color color = panelRequest.color;

		int panelX = panelRequest.panelX;
		int panelY = panelRequest.panelY;
		int panelW = panelRequest.panelW;
		int panelH = panelRequest.panelH;
		boolean autoScroll = panelRequest.autoScroll;

		/**
		 * set column sizes
		 */
		int[] colSizes = new int[column];
		for (int i = 1; i <= column; i++) {
			colSizes[i-1] = width;
		}

		MyCustomPanel customPanel = new MyCustomPanel(colSizes);
		customPanel.setMargin(margin);
		customPanel.setCenterAlignment(panelRequest.isCenterAligment());
		
		int currentColumn = 0;
		int currentRow = 0;
		int Size = components.length;

		List<Component> tempComponents = new ArrayList<Component>();

		for (int i = 0; i < Size; i++) {

			Component currentComponent = components[i] != null ? components[i]: new JLabel();
			
			currentColumn++;

			tempComponents.add(currentComponent);

			if (currentColumn == column) {
				currentColumn = 0;
				for (Component component : tempComponents) {
					customPanel.addComponent(component, currentRow);
				}
				currentRow++;

				tempComponents.clear();

			}
			printComponentLayout(currentComponent); 
		}
		
		/**
		 * adding remaining components
		 */
		for (Component component : tempComponents) {
			customPanel.addComponent(component, currentRow);
		}
		
		customPanel.update();
		
		/**
		 * setting panel physical appearance
		 */
		
		final int xPos = panelX == 0 ? margin : panelX;
		final int yPos = panelY == 0 ? margin : panelY;
		
		final int finalWidth = customPanel.getWidth();
		final int finalHeight = customPanel.getHeight();
		
		customPanel.setBounds(xPos, yPos, finalWidth, finalHeight);
		customPanel.setLayout(null);
		customPanel.setBounds(xPos, yPos, finalWidth, finalHeight);
		customPanel.setSize(finalWidth, finalHeight);
		customPanel.setBackground(color);
		
		if (autoScroll) {
			customPanel.setAutoscrolls(false);
			customPanel.setAutoscrolls(true);
		}
		System.out.println("Generated Panel V2 x:" + xPos + ", y:" + yPos + ", width:" + finalWidth + ", height:" + finalHeight);

		return customPanel;
	}

	public static void printComponentLayout(Component component) {
		if (null == component) {
			return;
		}
		System.out.println(component.getClass().getName() + "built--" +

				StringUtil.buildString("x:", component.getX(), " y:", component.getY(), "width:", component.getWidth(),
						"height:", component.getHeight()));
	}
}
