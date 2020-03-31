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

import com.fajar.dto.Filter;
import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.custom.CashFlow;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.handler.DashboardHandler;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.service.AppSession;

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
		if (responseTodayCashflow == null) {
			getHandler().getTodayMonthlyCashflow(new MyCallback() {

				@Override
				public void handle(Object... params) throws Exception {
					ShopApiResponse jsonResponse = (ShopApiResponse) params[0];
					handleResponseMonthlyCashflow(jsonResponse);
				}
			});
		}
	}

	/**
	 * generate cash flow info in the card
	 * 
	 * @param count
	 * @param amount
	 * @param title
	 * @return
	 */
	private JPanel todayCashflowCard(long count, long amount, String title) {

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
		panelMonthlySummary = buildMonthlySummaryTable(response);

		initComponent();
		initEvent();

	}

	/**
	 * build table of cash flow list in this month
	 * 
	 * @param response
	 * @return
	 */
	private JPanel buildMonthlySummaryTable(ShopApiResponse response) {

		Set<Integer> keys = response.getMonthlyDetailCost().keySet();
		Map<Integer, CashFlow> cashflowMap = response.getMonthlyDetailIncome();
		Map<Integer, CashFlow> costflowMap = response.getMonthlyDetailCost();
		Component[] components = new Component[keys.size() + 2];
		CashFlow totalCashflow = new CashFlow();
		CashFlow totalCostflow = new CashFlow();
		// header
		components[0] = cashflowSummaryHeader();
		int index = 1;

		for (Integer key : keys) {

			CashFlow cashflow = cashflowMap.get(key);
			CashFlow costflow = costflowMap.get(key);

			JPanel panelRow = buildCashflowSummaryTableRow(key, cashflow, costflow);

			components[index] = panelRow;

			updateCountAndAmount(totalCostflow, costflow);
			updateCountAndAmount(totalCashflow, cashflow);

			index++;
		}

		// footer
		components[components.length - 1] = cashflowSummaryFooter(totalCashflow, totalCostflow);

		PanelRequest panelRequest = PanelRequest.autoPanelScroll(1, 500, 1, Color.LIGHT_GRAY, 260);
		JPanel panel = buildPanelV2(panelRequest, components);
		return panel;
	}

	/**
	 * increment count and amount
	 * 
	 * @param totalCashflow
	 * @param flow
	 */
	private static void updateCountAndAmount(CashFlow totalCashflow, CashFlow flow) {
		if (null == totalCashflow) {
			totalCashflow = new CashFlow();
		}
		totalCashflow.setAmount(flow.getAmount() + totalCashflow.getAmount());
		totalCashflow.setCount(flow.getCount() + totalCashflow.getCount());

	}

	/**
	 * construct table header
	 * 
	 * @return
	 */
	private JPanel cashflowSummaryHeader() {
		return rowPanelHeader(5, 100, "Tanggal", "Jenis Aliran Kas", "Jumlah", "Nominal", "Opsi");
	}
 

	/**
	 * create table summary
	 * 
	 * @return
	 */
	private Component cashflowSummaryFooter(CashFlow totalCashFlow, CashFlow totalCostFlow) {
		return rowPanelHeader(5, 100, "TOTAL", "Pemasukan", totalCashFlow.getAmount(), totalCashFlow.getCount(), "", "",
				"Pengeluaran", totalCostFlow.getAmount(), totalCostFlow.getCount());
	}

	/**
	 * build data for each row
	 * 
	 * @param day or number
	 * @param income
	 * @param cost
	 * @return
	 */
	private JPanel buildCashflowSummaryTableRow(int day, CashFlow income, CashFlow cost) {

		Color color = day % 2 == 0 ? Color.WHITE : Color.WHITE; 
		Filter filter = responseTodayCashflow.getFilter(); 

		JButton buttonDetail = button("Detail");
		buttonDetail.addActionListener(getHandler().getDailyCashflow(day, filter.getMonth(), filter.getYear()));

		JPanel panel = rowPanel(5, 100, color, day, "Pemasukan", income.getAmount(), income.getCount(), buttonDetail, "",
				"Pengeluaran", cost.getAmount(), cost.getCount());
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

		JPanel panelCashflow = todayCashflowCard(cashflow.getCount(), cashflow.getAmount(), "Pengeluaran");
		JPanel panelCostflow = todayCashflowCard(costflow.getCount(), costflow.getAmount(), "Pemasukan");

		PanelRequest panelRequest = PanelRequest.autoPanelNonScroll(2, 200, 10, Color.WHITE);
		panelRequest.setCenterAligment(true);

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
		PanelRequest panelRequest = PanelRequest.autoPanelNonScroll(1, 700, 10, Color.WHITE);
		panelRequest.setCenterAligment(true);
		return panelRequest;
	}

	private PanelRequest panelCashflowRequest() {
		PanelRequest panelCashflowRequest = PanelRequest.autoPanelNonScroll(2, 250, 15, Color.white);
		return panelCashflowRequest;
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		buttonLogout.addActionListener(getHandler().logout());

	}

	private DashboardHandler getHandler() {
		return ((DashboardHandler) appHandler);
	}

	@Override
	public void show() {
		super.show();
		labelUserInfo.setText("Welcome, " + AppSession.getUser().getDisplayName());
	}

}
