package com.fajar.shopkeeping.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JScrollPane;

import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.pages.BasePage;
import com.fajar.shopkeeping.util.Log;

public class PanelBuilderv2 {

	private final PanelRequest panelRequest;
	private final Object[] components;
	private final int Size;
	private final int COLUMN_COUNT;
	private final int[] colSizes;
	
	private List<Component> tempComponents = new ArrayList<Component>();
	private int currentColumn = 0;
	private int currentRow = 0; 
	private int margin = 0;
	private Color color = Color.white;
	private int panelX = 0;
	private int panelY = 0;
	private int panelW = 0;
	private int panelH = 0;
	private boolean autoScroll = false;
	private boolean heightSpecified = false;
	private MyCustomPanel customPanel;


	public PanelBuilderv2(PanelRequest panelRequest, Object[] components) {
		this.panelRequest = panelRequest;
		this.components = components;
		this.Size = components.length;
		boolean useColSizes = panelRequest.column == 0;
		this.COLUMN_COUNT = useColSizes ? panelRequest.colSizes.length : panelRequest.column;
		/**
		 * set column sizes
		 */
		this.colSizes = useColSizes ? panelRequest.colSizes : ComponentBuilder.fillArray(COLUMN_COUNT, panelRequest.width);

		init();
	}

	private void init() { 
		
//		int height = panelRequest.height;
		this.margin = panelRequest.margin;
		this.color = panelRequest.getColor();

		this.panelX = panelRequest.panelX;
		this.panelY = panelRequest.panelY;
		this.panelW = panelRequest.panelW;
		this.panelH = panelRequest.panelH;
		this.heightSpecified = panelH > 0;
		this.autoScroll = panelRequest.isAutoScroll();
		
		this.customPanel = new MyCustomPanel(colSizes);
		customPanel.setMargin(margin);
		customPanel.setCenterAlignment(panelRequest.isCenterAligment());

	}

	public MyCustomPanel buildPanel() {

		for (int i = 0; i < Size; i++) {

			Component currentComponent;

			try {
				currentComponent = (Component) components[i] == null ? (Component) components[i] : new JLabel();
			} catch (Exception e) {
				currentComponent = components[i] != null ? new JLabel(String.valueOf(components[i])) : new JLabel();
			}

			currentColumn++; 
			tempComponents.add(currentComponent);

			checkIfSwitchRow();
//			printComponentLayout(currentComponent);
		}

		addRemainingComponents();

		customPanel.update(); 
		return returnPanel();
	}
	
	private void addRemainingComponents() { 
		/**
		 * adding remaining components
		 */
		for (Component component : tempComponents) {
			customPanel.addComponent(component, currentRow);
		}
	}

	private void checkIfSwitchRow() {
		 
		if (currentColumn == COLUMN_COUNT) {
			currentColumn = 0;
			for (Component component : tempComponents) {
				customPanel.addComponent(component, currentRow);
			}
			currentRow++; 
			tempComponents.clear(); 
		}
	}

	private MyCustomPanel returnPanel() {
		/**
		 * setting panel physical appearance
		 */
		final int xPos = panelX == 0 ? margin : panelX;
		final int yPos = panelY == 0 ? margin : panelY;

		final int finalWidth = customPanel.getCustomWidth();
		final int finalHeight = customPanel.getCustomHeight(); 

		customPanel.setLayout(null);
		customPanel.setBounds(xPos, yPos, finalWidth, finalHeight);
		customPanel.setBackground(color);
		
		if (autoScroll && heightSpecified) {

			customPanel.setPreferredSize(new Dimension(customPanel.getCustomWidth(), customPanel.getCustomHeight()));
			customPanel.setSize(new Dimension());
			BasePage.printSize(customPanel);

			MyCustomPanel scrolledPanel = ComponentBuilder.buildScrolledPanel(customPanel, (panelW > 0 ? panelW : finalWidth),
					panelH);
			Log.log("scrollPane count: ", ((JScrollPane) scrolledPanel.getComponent(0)).getViewport().getView());
//			printComponentLayout(panel);
			return scrolledPanel;

		}
//		System.out.println(
//				"Generated Panel V2 x:" + xPos + ", y:" + yPos + ", width:" + finalWidth + ", height:" + finalHeight);
		return customPanel;
	}

}
