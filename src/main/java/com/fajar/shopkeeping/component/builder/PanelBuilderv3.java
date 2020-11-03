package com.fajar.shopkeeping.component.builder;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.fajar.shopkeeping.component.MyCustomPanelv2;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.pages.BasePage;
import com.fajar.shopkeeping.util.Log;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PanelBuilderv3 extends BasePanelBuilder<Object>{ 
	private final int[] colSizes;
	
	private List<Component> tempComponents = new ArrayList<Component>();
	private int currentColumn = 0;
	private int currentRow = 0; 
	private int margin = 0; 
	private int panelX = 0;
	private int panelY = 0;
	private int panelW = 0;
	private int panelH = 0;
	private boolean autoScroll = false;
	private boolean heightSpecified = false;
	private MyCustomPanelv2 customPanel;


	public PanelBuilderv3(PanelRequest panelRequest, Object[] components) {
		super(panelRequest, panelRequest.getColor(), components);
		boolean useColSizes = panelRequest.column == 0; 
		/**
		 * set column sizes
		 */
		this.colSizes = useColSizes ? panelRequest.colSizes : ComponentBuilder.fillArray(COLUMN_COUNT, panelRequest.width);
//		log.debug("COLUMN SIZES: {} - {}", colSizes,  panelRequest.colSizes );
		init();
	}
	
	@Override
	protected int getColumnCount() {
		boolean useColSizes = panelRequest.column == 0;
		return useColSizes ? panelRequest.colSizes.length : panelRequest.column;
	}

	private void init() { 
		
//		int height = panelRequest.height;
		this.margin = panelRequest.margin; 

		this.panelX = panelRequest.panelX;
		this.panelY = panelRequest.panelY;
		this.panelW = panelRequest.panelW;
		this.panelH = panelRequest.panelH;
		this.heightSpecified = panelH > 0;
		this.autoScroll = panelRequest.isAutoScroll();
		
		this.customPanel = new MyCustomPanelv2(colSizes);
		customPanel.setMargin(margin);
		customPanel.setCenterAlignment(panelRequest.isCenterAligment());

	}

	public JPanel buildPanel() {
		
		for (int i = 0; i < components.length; i++) {

			Component currentComponent; 
			try {
				currentComponent = (Component) components[i] != null ? (Component) components[i] : new JLabel();
			} catch (Exception e) {
				currentComponent = components[i] != null ? new JLabel(String.valueOf(components[i])) : new JLabel();
			}
			
			addToTemporaryComponent(currentComponent); 
			updateCurrentColumnAndCurrentRow();
//			printComponentLayout(currentComponent);
		}

		addRemainingComponents();
		customPanel.update(); 
		return generatePanelPhysical();
	}
	
	private void addToTemporaryComponent(Component currentComponent) {
		if(null == currentComponent) {
			log.info( "currentComponent IS NULL");
			currentComponent = new JLabel();
		}
		tempComponents.add(currentComponent);
	}

	private void addRemainingComponents() { 
		/**
		 * adding remaining components
		 */
		for (Component component : tempComponents) {
			customPanel.addComponent(component, currentRow);
		}
	}

	private void updateCurrentColumnAndCurrentRow() {
		currentColumn++;
		if (currentColumn == COLUMN_COUNT) {
			currentColumn = 0;
			for (Component component : tempComponents) {
				customPanel.addComponent(component, currentRow);
			}
			currentRow++; 
			tempComponents.clear(); 
		}
	}

	private JPanel generatePanelPhysical() {
		/**
		 * setting panel physical appearance
		 */
		final int xPos = panelX == 0 ? margin : panelX;
		final int yPos = panelY == 0 ? margin : panelY;

		final int finalWidth = customPanel.getCustomWidth();
		final int finalHeight = customPanel.getCustomHeight(); 

//		customPanel.setLayout(null);
		customPanel.setBounds(xPos, yPos, finalWidth, finalHeight);
		customPanel.setBackground(color);
		
		if (autoScroll && heightSpecified) {

			customPanel.setPreferredSize(new Dimension(customPanel.getCustomWidth(), customPanel.getCustomHeight()));
			customPanel.setSize(new Dimension());
			BasePage.printSize(customPanel);

			JPanel scrolledPanel = ComponentBuilder.buildScrolledPanel(customPanel, (panelW > 0 ? panelW : finalWidth),
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
