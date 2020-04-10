package com.fajar.shopkeeping.pages;

import java.awt.Color;
import java.awt.Component;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import com.fajar.dto.Filter;
import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.Category;
import com.fajar.entity.Cost;
import com.fajar.entity.CostFlow;
import com.fajar.entity.Customer;
import com.fajar.entity.Product;
import com.fajar.entity.ProductFlow;
import com.fajar.entity.Supplier;
import com.fajar.entity.Transaction;
import com.fajar.entity.Unit;
import com.fajar.entity.custom.CashFlow;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.ComponentBuilder;
import com.fajar.shopkeeping.constant.PageConstants;
import com.fajar.shopkeeping.handler.DashboardHandler;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.model.SharedContext;
import com.fajar.shopkeeping.service.AppContext;
import com.fajar.shopkeeping.service.AppSession;
import com.fajar.shopkeeping.util.DateUtil;
import com.fajar.shopkeeping.util.ThreadUtil;

import lombok.Data;

@Data
public class DashboardPage extends BasePage {

	
	private JLabel labelUserInfo;
	 
	private JButton buttonGotoPeriodicReport;
	private JButton buttonLoadMonthlyCashflow; 
	
	private JPanel panelTodayCashflow;
	private JPanel panelMonthlySummary;
	private JPanel panelPeriodFilter;
	
	private JComboBox comboBoxMonth;
	private JComboBox comboBoxYear; 
	
	private int minTransactionYear;
	private int selectedMonth = DateUtil.getCurrentMonth();
	private int selectedYear = DateUtil.getCurrentYear();
	
	private JMenuItem menuItemLogout;
	private JMenuItem menuItemProduct;
	private JMenuItem menuItemUnit;
	private JMenuItem menuItemSupplier;
	private JMenuItem menuItemCustomer;
	private JMenuItem menuItemCategory;
	private JMenuItem menuItemTransaction;
	private JMenuItem menuItemCostFlow;
	private JMenuItem menuItemCostType;
	private JMenuItem menuItemProductFlow;
	
	private JMenuItem menuItemTransactionSupply;
	private JMenuItem menuItemTransactionSelling;
	
	private ShopApiResponse responseTodayCashflow;

	public DashboardPage() {
		super("Dashboard", BASE_WIDTH, BASE_HEIGHT);

	}

	@Override
	public void onShow() {
		if (responseTodayCashflow == null) {
			getHandler().getTodayMonthlyCashflow(callbackUpdateMonthlyCashflow());
		}
	}
	
	@Override
	public void initComponent() {

		PanelRequest mainPanelRequest = mainPanelRequest(); 

		if (labelUserInfo == null) {
			labelUserInfo = title("Welcome to Dasboard!");
		}
		if (panelTodayCashflow == null) {
			panelTodayCashflow = buildPanelV2(panelCashflowRequest(), label("Please wait..."));
		}
		if (panelMonthlySummary == null) {
			panelMonthlySummary = buildPanelV2(panelCashflowRequest(), label("Please wait..."));
		}
		
		setPanelPeriodFilter(buildPanelPeriodFilter());  
		
		mainPanel = buildPanelV2(mainPanelRequest, 
				title("BUMDES \"MAJU MAKMUR\""), labelUserInfo,  
				label("ALIRAN KAS HARI INI "+DateUtil.todayString()), 
				panelTodayCashflow, panelPeriodFilter, 
				panelMonthlySummary);

		parentPanel.add(mainPanel);

		exitOnClose();

	}

	
	@Override
	protected void constructMenu() { 
		if(menuBar.getMenuCount()>0) {
			return;
		}
		
		
		setMenuItemLogout(new JMenuItem("Logout"));
		setMenuItemProduct(new JMenuItem("Product"));
		setMenuItemUnit(new JMenuItem("Unit"));
		setMenuItemSupplier(new JMenuItem("Supplier"));
		setMenuItemCustomer(new JMenuItem("Customer"));
		setMenuItemCategory(new JMenuItem("Category"));
		setMenuItemTransaction(new JMenuItem("List Transction"));
		setMenuItemCostFlow(new JMenuItem("Cost Flow"));
		setMenuItemCostType(new JMenuItem("Cost Type"));
		setMenuItemTransactionSupply(new JMenuItem("Supply"));
		setMenuItemProductFlow(new JMenuItem("Product Flow"));
		setMenuItemTransactionSelling(new JMenuItem("Selling"));
		
        JMenu managementMenu = new JMenu("Management"); 
        managementMenu.add(menuItemProduct);
        
        managementMenu.add(menuItemSupplier);
        managementMenu.add(menuItemCustomer);
        managementMenu.add(menuItemCostFlow);
        
        JMenu settingMenu = new JMenu("Setting");
        settingMenu.add(menuItemCostType);
        settingMenu.add(menuItemUnit);
        settingMenu.add(menuItemCategory);
        
        JMenu accountMenu = new JMenu("Account"); 
		accountMenu.add(menuItemLogout);
		
		JMenu transactionMenu = new JMenu("Transaction");
		transactionMenu.add(menuItemTransactionSupply);
		transactionMenu.add(menuItemTransactionSelling);
		transactionMenu.add(menuItemTransaction); 
		transactionMenu.add(menuItemProductFlow);
		
		menuBar.add(accountMenu ); 
		menuBar.add(settingMenu);
        menuBar.add(managementMenu); 
        menuBar.add(transactionMenu);
		
 
	}
	
	private MyCallback callbackUpdateMonthlyCashflow() {
		 
		return new MyCallback() {

			@Override
			public void handle(Object... params) throws Exception {
				ShopApiResponse jsonResponse = (ShopApiResponse) params[0];
				callbackMonthlyCashflow(jsonResponse);
			}
		};
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

		JPanel panel = buildPanelV2(panelRequest, title(title), label("Jumlah"), label(count), label("Nominal"),
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
	private void callbackMonthlyCashflow(final ShopApiResponse response) {
		ThreadUtil.run(new Runnable() {
			
			@Override
			public void run() {
				setResponseTodayCashflow(response);
				setPanelTodayCashflow(buildTodayCashflowTable(response));
				setPanelMonthlySummary(buildMonthlySummaryTable(response));
				setMinTransactionYear(response.getTransactionYears()[0]);
				AppContext.setContext(REPORT_STUFF, SharedContext.builder().minTransactionYear(minTransactionYear).build());
				
				preInitComponent();
				initEvent();
			}
		});

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
	 * @param day    or number
	 * @param income
	 * @param cost
	 * @return
	 */
	private JPanel buildCashflowSummaryTableRow(int day, CashFlow income, CashFlow cost) {

		Color color = day % 2 == 0 ? Color.WHITE : Color.WHITE;
		Filter filter = responseTodayCashflow.getFilter();

		JButton buttonDetail = button("Detail");
		buttonDetail.addActionListener(getHandler().getDailyCashflow(day, filter.getMonth(), filter.getYear()));

		JPanel panel = rowPanel(5, 100, color, day, "Pemasukan", income.getAmount(), income.getCount(), buttonDetail,
				"", "Pembelian", cost.getAmount(), cost.getCount());
		return panel;
	}

	/**
	 * build cashFlow info card for today's income & spent cost
	 * 
	 * @param response
	 * @return
	 */
	private JPanel buildTodayCashflowTable(ShopApiResponse response) {
		
		if(selectedMonth != DateUtil.getCurrentMonth() || selectedYear != DateUtil.getCurrentYear()) {
			return panelTodayCashflow;
		}

		int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		CashFlow cashflow = response.getMonthlyDetailIncome().get(today);
		CashFlow costflow = response.getMonthlyDetailCost().get(today);

		JPanel panelCashflow = todayCashflowCard(cashflow.getCount(), cashflow.getAmount(), "Pemasukan");
		JPanel panelCostflow = todayCashflowCard(costflow.getCount(), costflow.getAmount(), "Pembelian");

		PanelRequest panelRequest = PanelRequest.autoPanelNonScroll(2, 200, 10, Color.WHITE);
		panelRequest.setCenterAligment(true);

		return buildPanelV2(panelRequest, panelCashflow, panelCostflow);
	}
 
	/**
	 * build panel for select period
	 * @return
	 */
	private JPanel buildPanelPeriodFilter() {

		JComboBox _comboBoxMonth = ComponentBuilder.buildComboBox(selectedMonth, buildArray(1,12));
		JComboBox _comboBoxYear = ComponentBuilder.buildComboBox(selectedYear, buildArray(minTransactionYear, Calendar.getInstance().get(Calendar.YEAR)));
		
		setComboBoxMonth(_comboBoxMonth);
		setComboBoxYear(_comboBoxYear);
		setButtonLoadMonthlyCashflow(button("Search/Refresh")); 
		setButtonGotoPeriodicReport(button("Report Page"));
		
		PanelRequest panelRequest = PanelRequest.autoPanelNonScroll(4, 60, 3, Color.WHITE);
		
		JPanel panel = buildPanelV2(panelRequest ,
					label("Month"), comboBoxMonth,
					label("Year"), comboBoxYear,
				 
					buttonLoadMonthlyCashflow,
					BLANK_LABEL,
					buttonGotoPeriodicReport,
					BLANK_LABEL);
		 
		return panel;
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
		
		
		/**
		 * menus
		 */
		//account
		addActionListener(menuItemLogout, getHandler().logout());
		//management
		addActionListener(menuItemProduct, getHandler().managementNavigationListener(Product.class));
		addActionListener(menuItemUnit, getHandler().managementNavigationListener(Unit.class));
		addActionListener(menuItemSupplier, getHandler().managementNavigationListener(Supplier.class));
		addActionListener(menuItemCategory, getHandler().managementNavigationListener(Category.class));
		addActionListener(menuItemCustomer, getHandler().managementNavigationListener(Customer.class));
		addActionListener(menuItemCostFlow, getHandler().managementNavigationListener(CostFlow.class)); 
		addActionListener(menuItemCostType, getHandler().managementNavigationListener(Cost.class)); 
		
		addActionListener(menuItemTransactionSupply, getHandler().navigationListener(PageConstants.PAGE_TRAN_SUPPLY)); 
		addActionListener(menuItemTransactionSelling, getHandler().navigationListener(PageConstants.PAGE_TRAN_SELLING));
		addActionListener(menuItemProductFlow, getHandler().managementNavigationListener(ProductFlow.class));
		addActionListener(menuItemTransaction, getHandler().managementNavigationListener(Transaction.class)); 
		
		addActionListener(buttonLoadMonthlyCashflow, getHandler().getMonthlyCashflow(callbackUpdateMonthlyCashflow()));
		addActionListener(buttonGotoPeriodicReport, getHandler().gotoPeriodicReportPage());
		
		addActionListener(comboBoxMonth, comboBoxListener(comboBoxMonth,"selectedMonth"));  
		addActionListener(comboBoxYear, comboBoxListener(comboBoxYear,"selectedYear")); 
		
		super.initEvent();
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
