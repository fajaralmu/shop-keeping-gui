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

		int Col = panelRequest.Col;
		int W = panelRequest.W;
		int H = panelRequest.H;
		int Margin = panelRequest.Margin;
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

		int Col = panelRequest.Col;
		int W = panelRequest.W;
		int H = panelRequest.H;
		int Margin = panelRequest.Margin;
		Color color = panelRequest.color;

		int panelX = panelRequest.panelX;
		int panelY = panelRequest.panelY;
		int panelW = panelRequest.panelW;
		int panelH = panelRequest.panelH;
		boolean autoScroll = panelRequest.autoScroll;

		int[] colSizes = new int[Col];
		for (int i = 1; i <= Col; i++) {
			colSizes[i-1] = W;
		}

		MyCustomPanel customPanel = new MyCustomPanel(colSizes);
		customPanel.setMargin(Margin);
		customPanel.setCenterAlignment(panelRequest.isCenterAligment());
		
		int CurrentCol = 0;
		int currentRow = 0;
		int Size = components.length;

		List<Component> tempComponents = new ArrayList<Component>();

		for (int i = 0; i < Size; i++) {

			Component C = components[i];

			if (null != C) {

			} else {
				C = new JLabel();
			}
			CurrentCol++;

			tempComponents.add(C);

			if (CurrentCol == Col) {
				CurrentCol = 0;
				for (Component component : tempComponents) {
					customPanel.addComponent(component, currentRow);
				}
				currentRow++;

				tempComponents.clear();

			}
			printComponentLayout(C); 
		}
		
		/**
		 * remaining components
		 */
		for (Component component : tempComponents) {
			customPanel.addComponent(component, currentRow);
		}
		
		customPanel.update();
		customPanel.setBackground(color);
		
		int X = panelX == 0 ? Margin : panelX;
		int Y = panelY == 0 ? Margin : panelY;
		
		int finalW = customPanel.getWidth();
		int finalH = customPanel.getHeight();
		
		customPanel.setBounds(X, Y, finalW, finalH);
		customPanel.setLayout(null);
		customPanel.setBounds(X, Y, finalW, finalH);
		customPanel.setSize(finalW, finalH);
		
		if (autoScroll) {
			customPanel.setAutoscrolls(false);
			customPanel.setAutoscrolls(true);
		}
		System.out.println("Generated Panel V2 x:" + X + ", y:" + Y + ", width:" + finalW + ", height:" + finalH);

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
