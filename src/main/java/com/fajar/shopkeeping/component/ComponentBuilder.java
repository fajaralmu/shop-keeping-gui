package com.fajar.shopkeeping.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

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

				component.setLocation(currentColumn * margin + (width * currentColumn),
						currentRow * margin + height * currentRow);
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
			// printComponentLayout(C);
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

	public static MyCustomPanel buildPanelV2(PanelRequest panelRequest, Object... components) {
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
			colSizes[i - 1] = width;
		}

		MyCustomPanel customPanel = new MyCustomPanel(colSizes);
		customPanel.setMargin(margin);
		customPanel.setCenterAlignment(panelRequest.isCenterAligment());

		int currentColumn = 0;
		int currentRow = 0;
		int Size = components.length;

		List<Component> tempComponents = new ArrayList<Component>();

		for (int i = 0; i < Size; i++) {

			Component currentComponent = null;

			try {
				currentComponent = (Component) components[i];
			} catch (Exception e) {
				currentComponent = components[i] != null ? new JLabel(String.valueOf(components[i])) : new JLabel();
			}

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

		final int finalWidth = customPanel.getCustomWidth();
		final int finalHeight = customPanel.getCustomHeight();

		customPanel.setBounds(xPos, yPos, finalWidth, finalHeight);
		customPanel.setLayout(null);
		customPanel.setBounds(xPos, yPos, finalWidth, finalHeight);
		customPanel.setSize(finalWidth, finalHeight);
		customPanel.setBackground(color);

		if (autoScroll && panelH > 0) {

			customPanel.setPreferredSize(new Dimension(customPanel.getWidth(), customPanel.getHeight()));

			JScrollPane scrollPane = new JScrollPane(customPanel);
			scrollPane.setPreferredSize(new Dimension(customPanel.getCustomWidth(), panelH));

			MyCustomPanel panel = new MyCustomPanel();
			panel.setBounds(0, 0, finalWidth, panelH);
			panel.add(scrollPane);
			printComponentLayout(panel);
			return panel;

		}
		System.out.println(
				"Generated Panel V2 x:" + xPos + ", y:" + yPos + ", width:" + finalWidth + ", height:" + finalHeight);

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

	public static JComboBox buildComboBox(Object defaultValue, Object... values) {

		ComboBoxModel model = new DefaultComboBoxModel<>();
		JComboBox comboBox = new JComboBox();

		int maxSize = 0;

		for (Object object : values) {

			
			comboBox.addItem(object);
			
			JLabel label = label(object);
			if (label.getWidth() > maxSize) {
				maxSize = label.getWidth();
			}
		}

		comboBox.setSize(maxSize + 20, 20);
		if(null != defaultValue) {
			comboBox.setSelectedItem(defaultValue);
		}
		return comboBox;
	}

	public static JLabel label(Object title) {

		if (isNumber(title)) {
			try {
				title = StringUtil.beautifyNominal(Long.parseLong(title.toString()));
			}catch (Exception e) {
				title = StringUtil.beautifyNominal(Long.parseLong(title.toString()));
			}
		}
		 
		int width = title.toString().length() * 10;

		JLabel label = new JLabel(title.toString(), SwingConstants.CENTER);
		label.setSize(width, 20);
		return label;
	}

	public static boolean isNumber(Object o) {
		if (null == o) {
			return false;
		}

		Class objectType = o.getClass();

		if (objectType.equals(int.class) || objectType.equals(Integer.class)) {
			return true;
		}
		if (objectType.equals(double.class) || objectType.equals(Double.class)) {
			return true;
		}
		if (objectType.equals(long.class) || objectType.equals(Long.class)) {
			return true;
		}

		return false;
	}
}
