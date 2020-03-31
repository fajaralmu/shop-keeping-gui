package com.fajar.shopkeeping.pages;

import java.awt.Color;
import java.awt.Component;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.custom.CashFlow;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.handler.DashboardHandler;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.webservice.AppSession;

public class DashboardPage extends BasePage {

	private JLabel labelUserInfo;
	private JButton buttonLogout;
	private JPanel panelTodayCashflow;
	private JPanel panelMonthlySummary;
	private ShopApiResponse responseTodayCashflow;

	public DashboardPage() {
		super("Dashboard", BASE_WIDTH, BASE_HEIGHT);

	}

	@Override
	public void onShow() {
		if(responseTodayCashflow == null) {
			((DashboardHandler) appHandler).getTodayMonthlyCashflow(new MyCallback() {
	
				@Override
				public void handle(Object... params) throws Exception {
					System.out.println(" onShow JSON RESPONSE: " + params[0]);
					ShopApiResponse jsonResponse = (ShopApiResponse) params[0];
					handleResponseMonthlyCashflow(jsonResponse);
				}
			});
		}
	}

	/**
	 * generate cashflow info in the card
	 * 
	 * @param count
	 * @param amount
	 * @param title
	 * @return
	 */
	private JPanel cashflowItemPanel(long count, long amount, String title) {

		PanelRequest panelRequest = new PanelRequest(1, 150, 50, 5, Color.yellow, 0, 0, 0, 0, false);
		panelRequest.setCenterAligment(true);

		Component titleLabel = title(title);

		JPanel panel = buildPanelV2(panelRequest, titleLabel, label("Jumlah"), label(count), label("Nominal"),
				label(amount));

		Border border = BorderFactory.createBevelBorder(BevelBorder.RAISED);

		panel.setBorder(border);
		return panel;
	}

	/**
	 * update UI when get cash flow data
	 * 
	 * @param response
	 */
	private void handleResponseMonthlyCashflow(ShopApiResponse response) {

		responseTodayCashflow = response;
		panelTodayCashflow = buildTodayCashflow(response);
		panelMonthlySummary = buildMonthlySummaryCashflow(response);

		initComponent();
		initEvent();

	}

	/**
	 * build table of cash flow list in this month
	 * @param response
	 * @return
	 */
	private JPanel buildMonthlySummaryCashflow(ShopApiResponse response) {

		Set<Integer> keys = response.getMonthlyDetailCost().keySet();
		Map<Integer, CashFlow> cashflowMap = response.getMonthlyDetailIncome();
		Map<Integer, CashFlow> costflowMap = response.getMonthlyDetailCost();
		Component[] components = new Component[keys.size() + 2  ]; 
		CashFlow totalCashflow = new CashFlow();
		CashFlow totalCostflow = new CashFlow();
		//header
		components[0] = cashflowSummaryHeader();
		int index = 1;

		for (Integer integer : keys) {

			CashFlow cashflow = cashflowMap.get(integer);
			CashFlow costflow = costflowMap.get(integer);
			
			JPanel panelRow = buildCashflowSummaryRow(integer, cashflow , costflow );
			
			components[index] = panelRow;
			
			updateCountAndAmount(totalCostflow, costflow);
			updateCountAndAmount(totalCashflow, cashflow);
			
			index++;
		}
		
		//footer
		components[components.length - 1] = cashflowSummaryFooter(totalCashflow, totalCostflow);
		
		PanelRequest panelRequest = new PanelRequest(1, 500, 10, 1, Color.LIGHT_GRAY, 0, 0, 0, 260, true);
		JPanel panel = buildPanelV2(panelRequest, components);
		return panel;
	}
	
	/**
	 * increment count and amount
	 * @param totalCashflow
	 * @param flow
	 */
	private static void updateCountAndAmount(CashFlow totalCashflow, CashFlow flow) {
		if(null == totalCashflow) {
			totalCashflow = new CashFlow();
		}
		totalCashflow.setAmount(flow.getAmount() + totalCashflow.getAmount());
		totalCashflow.setCount(flow.getCount() + totalCashflow.getCount());
		 
	}

	/**
	 * construct table header
	 * @return
	 */
	private Component cashflowSummaryHeader() {
		PanelRequest panelRequestHeader = new PanelRequest(5, 100, 10, 1, Color.orange, 0, 0, 0, 0, false, true);;
		JPanel panelHeader = buildPanelV2(panelRequestHeader , label("Tanggal"),  label("Jenis Aliran Kas"), label("Jumlah"), label("Nominal"), label("Opsi"));
		return panelHeader;
	}
	
	/**
	 * create table summary
	 * @return
	 */
	private Component cashflowSummaryFooter(CashFlow totalCashFlow, CashFlow totalCostFlow) {
		PanelRequest panelRequestHeader = new PanelRequest(5, 100, 10, 1, Color.orange, 0, 0, 0, 0, false, true);
		
		JPanel panelFooter = buildPanelV2(panelRequestHeader, label("TOTAL"), label("Pemasukan"), label(totalCashFlow.getAmount()),
				label(totalCashFlow.getCount()), label(""), label(""), label("Pengeluaran"), label(totalCostFlow.getAmount()),
				label(totalCostFlow.getCount()));
		return panelFooter;
	}

	/**
	 * build data for each row
	 * @param number
	 * @param income
	 * @param cost
	 * @return
	 */
	private JPanel buildCashflowSummaryRow(int number, CashFlow income, CashFlow cost) {

		Color color = number % 2 == 0 ? Color.WHITE : Color.WHITE;
		
		PanelRequest panelRequest = new PanelRequest(5, 100, 10, 1, color, 0, 0, 0, 0, false, true);
		
		JPanel panel = buildPanelV2(panelRequest, label(number), label("Pemasukan"), label(income.getAmount()),
				label(income.getCount()), button("Detail"), label(""),  label("Pengeluaran"), label(cost.getAmount()),
				label(cost.getCount()));
		return panel;
	}

	/**
	 * build cashFlow info card for today's income & spent cost
	 * 
	 * @param response
	 * @return
	 */
	private JPanel buildTodayCashflow(ShopApiResponse response) {

		int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		CashFlow cashflow = response.getMonthlyDetailIncome().get(today);
		CashFlow costflow = response.getMonthlyDetailCost().get(today);

		JPanel panelCashflow = cashflowItemPanel(cashflow.getCount(), cashflow.getAmount(), "Pengeluaran");
		JPanel panelCostflow = cashflowItemPanel(costflow.getCount(), costflow.getAmount(), "Pemasukan");

		PanelRequest panelRequest = new PanelRequest(2, 200, 10, 10, Color.WHITE, 0, 10, 0, 0, false, true);

		return buildPanelV2(panelRequest, panelCashflow, panelCostflow);
	}

	@Override
	public void initComponent() {

		parentPanel.removeAll();

		PanelRequest mainPanelRequest = mainPanelRequest();

		buttonLogout = button("logout");

		if (labelUserInfo == null) {
			labelUserInfo = title("Welcome to Dasboard!");
		}
		if (panelTodayCashflow == null) {
			panelTodayCashflow = buildPanelV2(panelCashflowRequest(), label("Please wait..."));
		}
		if (panelMonthlySummary == null) {
			panelMonthlySummary = buildPanelV2(panelCashflowRequest(), label("Please wait..."));
		}

		mainPanel = buildPanelV2(mainPanelRequest, title("BUMDES \"MAJU MAKMUR\""), labelUserInfo, buttonLogout,
				label("ALIRAN KAS HARI INI"), panelTodayCashflow, label("ALIRAN KAS BULAN INI"), panelMonthlySummary);

		parentPanel.add(mainPanel);
		parentPanel.revalidate();
		parentPanel.repaint();
		exitOnClose();

	}

	private PanelRequest mainPanelRequest() {
		PanelRequest panelRequest = new PanelRequest(1, 700, 20, 10, Color.WHITE, 10, 10, 0, 0, true);
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
