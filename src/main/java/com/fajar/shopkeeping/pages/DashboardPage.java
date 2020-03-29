package com.fajar.shopkeeping.pages;

import java.awt.Color;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.custom.CashFlow;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.BlankComponent;
import com.fajar.shopkeeping.handler.DashboardHandler;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.model.ReservedFor;
import com.fajar.shopkeeping.util.Casting;
import com.fajar.shopkeeping.webservice.AppSession;

public class DashboardPage extends BasePage {

	JLabel labelUserInfo;
	JButton buttonLogout;
	JPanel panelCashflowInfo;

	public DashboardPage() {
		super("Dashboard", 700, 600);

	}

	@Override
	public void onShow() {
		((DashboardHandler) appHandler).getTodayMonthlyCashflow(new MyCallback() {

			@Override
			public void handle(Object... params) throws Exception {
				System.out.println(" onShow JSON RESPONSE: " + params[0]);
				ShopApiResponse jsonResponse = (ShopApiResponse) params[0];
				handleResponseMonthlyCashflow(jsonResponse);
			}
		});
	}

	private void handleResponseMonthlyCashflow(ShopApiResponse response) {
		// panelCashflowInfo.removeAll();

		int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		CashFlow cashflow = response.getMonthlyDetailIncome().get(today);

		System.out.println("Cash Flow: " + cashflow);
		JLabel label = (JLabel) panelCashflowInfo.getComponent(0);
		label.setText("Sold quantity: " + cashflow.getCount() + " Amount: " + cashflow.getAmount());
		System.out.println(panelCashflowInfo.getComponentCount());
	}

	@Override
	public void initComponent() {

		parentPanel.removeAll();

		PanelRequest mainPanelRequest = new PanelRequest(2, 150, 20, 15, Color.WHITE, 30, 30, 0, 0, true);

		labelUserInfo = new JLabel();
		buttonLogout = new JButton("logout");
		panelCashflowInfo = buildPanel(panelCashflowRequest(), label("Please wait..."));

		JPanel mainPanel = buildPanel(mainPanelRequest,

				title("Welcome to Dasboard!"), new BlankComponent(ReservedFor.BEFORE_HOR, 150, 20), labelUserInfo, null,
				panelCashflowInfo, buttonLogout);

		parentPanel.add(mainPanel);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

	}

	private PanelRequest panelCashflowRequest() {
		PanelRequest panelCashflowRequest = new PanelRequest(1, 150, 20, 15, Color.WHITE, 30, 30, 0, 0, true);
		return panelCashflowRequest;
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
