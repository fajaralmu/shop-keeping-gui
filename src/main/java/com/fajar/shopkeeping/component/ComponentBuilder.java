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

		int Col = panelRequest.column;
		int W = panelRequest.width;
		int H = panelRequest.height;
		int Margin = panelRequest.margin;
		Color color = panelRequest.color;

		int panelX = panelRequest.panelX;
		int panelY = panelRequest.panelY;
		int panelW = panelRequest.panelW;
		int panelH = panelRequest.panelH;
		boolean autoScroll = panelRequest.autoScroll;

		JPanel Panel = new JPanel();
		int CurrentCol = 0;
		int CurrentRow = 0;
		int Size = components.length;
		Component[] ControlsClone = components;

		for (int i = 0; i < Size; i++) {

			Component C = ControlsClone[i];

			if (null != C) {

				// C.setBounds(CurrentCol * Margin + (W * CurrentCol), CurrentRow * Margin + H *
				// CurrentRow, W, H);

				C.setLocation(CurrentCol * Margin + (W * CurrentCol), CurrentRow * Margin + H * CurrentRow);
				C.setSize(W, H);
				if (C.getClass().equals(BlankComponent.class)) {
					BlankComponent blankC = (BlankComponent) C;

					switch (blankC.reserved) {

					case BEFORE_HOR:

						Component beforeContHor = components[i - 1];

						beforeContHor.setBounds(beforeContHor.getX(), beforeContHor.getY(),
								beforeContHor.getWidth() + blankC.getWidth(), beforeContHor.getHeight());

						Panel.remove(beforeContHor);
						components[i] = beforeContHor;
						C = beforeContHor;
						break;

					case BEFORE_VER:
						Component beforeContVer = components[i - Col];

						beforeContVer.setBounds(beforeContVer.getX(), beforeContVer.getY(), beforeContVer.getWidth(),
								beforeContVer.getHeight() + blankC.getHeight());

						Panel.remove(beforeContVer);
						components[i] = beforeContVer;
						C = beforeContVer;
						break;

					case AFTER_HOR:
					case AFTER_VER:
					default:
						break;
					}
				}

			} else {
				C = new JLabel();
			}
			CurrentCol++;

			if (CurrentCol == Col) {
				CurrentCol = 0;
				CurrentRow++;

			}
			//printComponentLayout(C);
			Panel.add(C);
		}

		Panel.setBackground(color);
		int X = panelX == 0 ? Margin : panelX;
		int Y = panelY == 0 ? Margin : panelY;
		int finalW = panelW != 0 ? panelW : Col * W + Col * Margin;
		int finalH = panelH != 0 ? panelH : (CurrentRow + 1) * H + (CurrentRow + 1) * Margin;
		Panel.setBounds(X, Y, finalW, finalH);
		Panel.setLayout(null);
		Panel.setBounds(X, Y, finalW, finalH);
		Panel.setSize(finalW, finalH);
		if (autoScroll) {
			Panel.setAutoscrolls(false);
			Panel.setAutoscrolls(true);
		}
		System.out.println("Generated Panel x:" + X + ", y:" + Y + ", width:" + finalW + ", height:" + finalH);

		return Panel;
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
		customPanel.setBackground(color);
		
		final int xPos = panelX == 0 ? margin : panelX;
		final int yPos = panelY == 0 ? margin : panelY;
		
		final int finalWidth = customPanel.getWidth();
		final int finalHeight = customPanel.getHeight();
		
		customPanel.setBounds(xPos, yPos, finalWidth, finalHeight);
		customPanel.setLayout(null);
		customPanel.setBounds(xPos, yPos, finalWidth, finalHeight);
		customPanel.setSize(finalWidth, finalHeight);
		
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
