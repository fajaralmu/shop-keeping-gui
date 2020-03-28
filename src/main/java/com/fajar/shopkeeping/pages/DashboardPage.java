package com.fajar.shopkeeping.pages;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.fajar.shopkeeping.component.BlankComponent;
import com.fajar.shopkeeping.handler.DashboardHandler;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.model.ReservedFor;
import com.fajar.shopkeeping.webservice.AppSession;

public class DashboardPage extends BasePage {

	JLabel labelUserInfo;
	JButton buttonLogout;

	public DashboardPage() {
		super("Dashboard", 700, 600);
	}

	@Override
	public void initComponent() {

		PanelRequest panelRequest = new PanelRequest(2, 150, 20, 15, Color.WHITE, 30, 30, 0, 0, true);

		labelUserInfo = new JLabel();
		buttonLogout = new JButton("logout");

		JPanel mainPanel = buildPanel(panelRequest,

				title("Welcome to Dasboard!"), new BlankComponent(ReservedFor.BEFORE_HOR, 150, 20),
				labelUserInfo,
				buttonLogout);

		parentPanel.add(mainPanel);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

	}

	@Override
	protected void initEvent() {
		super.initEvent();
		buttonLogout.addActionListener(((DashboardHandler) appHandler).logout());

	}

	@Override
	public void show() { 
		super.show();
		labelUserInfo.setText("User: " + AppSession.getUser().getDisplayName());
	}

}
