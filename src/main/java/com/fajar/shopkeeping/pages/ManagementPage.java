package com.fajar.shopkeeping.pages;

import java.awt.Color;

import com.fajar.shopkeeping.component.ComponentBuilder;
import com.fajar.shopkeeping.model.PanelRequest;

public class ManagementPage extends BasePage { 
 

	public ManagementPage() {
		super("Management", BASE_WIDTH, BASE_HEIGHT);
	} 

	@Override
	public void initComponent() {
		 
		
		PanelRequest panelRequest = new PanelRequest(1, 670, 20, 15, Color.WHITE, 30, 30, 0, 0, false, true); 
		
		mainPanel = ComponentBuilder.buildPanelV2(panelRequest,

				title("Management Page", 50) 
				 ); 

		parentPanel.add(mainPanel);
		exitOnClose();

	}

	@Override
	protected void initEvent() {
		super.initEvent(); 
		 
	}

}
