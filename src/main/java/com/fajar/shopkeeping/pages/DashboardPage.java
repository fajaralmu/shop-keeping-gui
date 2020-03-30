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
import com.fajar.shopkeeping.handler.DashboardHandler;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.webservice.AppSession;

public class DashboardPage extends BasePage {

	JLabel labelUserInfo;
	JButton buttonLogout;
	JPanel panelCashflowInfo;

	public DashboardPage() {
		super("Dashboard", 800, 700);

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

	private JPanel cashflowItemPanel(long count, long amount, String title) {

		PanelRequest panelRequest = new PanelRequest(1, 100, 50, 5, Color.yellow, 0, 0, 0, 0, false);
		panelRequest.setCenterAligment(true);
		JPanel panel = buildPanelV2(panelRequest, label(title), label("Quantity"), label(count), label("Amount"), label(amount)

		);
		return panel;
	}

	private void handleResponseMonthlyCashflow(ShopApiResponse response) {

		int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		CashFlow cashflow = response.getMonthlyDetailIncome().get(today);
		CashFlow costflow = response.getMonthlyDetailCost().get(today);

		JPanel panelCashflow = cashflowItemPanel(cashflow.getCount(), cashflow.getAmount(), "Income");
		JPanel panelCostflow = cashflowItemPanel(costflow.getCount(), costflow.getAmount(), "Cost"); 

		PanelRequest panelRequest = new PanelRequest(2, 200, 10, 10, Color.blue, 0, 10, 0, 0, false);
//		panelRequest.setCenterAligment(true);
		System.out.println("===========");
		panelCashflowInfo = buildPanelV2(panelRequest, panelCashflow, panelCostflow );
		System.out.println("===========");
		initComponent();	
		initEvent();

		parentPanel.revalidate();
		parentPanel.repaint();
	}

	@Override
	public void initComponent() {

		parentPanel.removeAll();

		PanelRequest mainPanelRequest = mainPanelRequest();

		buttonLogout = button("logout");

		if (labelUserInfo == null) {
			labelUserInfo = title("Welcome to Dasboard!");
		}
		if (panelCashflowInfo == null) {
			panelCashflowInfo = buildPanelV2(panelCashflowRequest(), label("Please wait..."));
		}

		mainPanel = buildPanelV2(mainPanelRequest, title("BUMDES \"MAJU MAKMUR\""), labelUserInfo, buttonLogout,
				panelCashflowInfo);

		parentPanel.add(mainPanel);
		exitOnClose();

	}

	private PanelRequest mainPanelRequest() {
		PanelRequest panelRequest = new PanelRequest(1, 700, 20, 15, Color.WHITE, 30, 30, 0, 0, true);
		panelRequest.setCenterAligment(true);
		return panelRequest;
	}

	private PanelRequest panelCashflowRequest() {
		PanelRequest panelCashflowRequest = new PanelRequest(2, 250, 20, 15, Color.WHITE, 30, 30, 0, 0, true);
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
		labelUserInfo.setText("Welcome, " + AppSession.getUser().getDisplayName());
	}

}
