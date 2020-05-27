package com.fajar.shopkeeping.pages;

import static com.fajar.shopkeeping.component.ComponentBuilder.label;
import static com.fajar.shopkeeping.util.StringUtil.beautifyNominal;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionListener;
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
import javax.swing.border.Border;

import com.fajar.dto.Filter;
import com.fajar.dto.WebResponse;
import com.fajar.entity.Capital;
import com.fajar.entity.CapitalFlow;
import com.fajar.entity.CashBalance;
import com.fajar.entity.Category;
import com.fajar.entity.Cost;
import com.fajar.entity.CostFlow;
import com.fajar.entity.Customer;
import com.fajar.entity.CustomerVoucher;
import com.fajar.entity.Product;
import com.fajar.entity.ProductFlow;
import com.fajar.entity.Supplier;
import com.fajar.entity.Transaction;
import com.fajar.entity.Unit;
import com.fajar.entity.Voucher;
import com.fajar.entity.custom.CashFlow;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.ComponentBuilder;
import com.fajar.shopkeeping.component.Loadings;
import com.fajar.shopkeeping.constant.ContextConstants;
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

	
	private static final int[] COLUMN_SIZES = new int[] {
			50, 100, 100, 100, 100
	};

	private JLabel labelUserInfo;
	 
	private JButton buttonGotoPeriodicReport;
	private JButton buttonLoadMonthlyCashflow; 
	private JButton buttonGenerateMontlyReport;
	
	private JPanel panelTodayCashflow;
	private JPanel panelMonthlySummary;
	private JPanel panelPeriodFilter;
	
	private JComboBox comboBoxMonth;
	private JComboBox comboBoxYear; 
	
	private int minTransactionYear;
	private int selectedMonth;  
	private int selectedYear;  
	
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
	
	private JMenuItem menuItemVoucher;
	private JMenuItem menuItemCustomerVoucher;
	private JMenuItem menuItemCapital;
	private JMenuItem menuItemCapitalFLow;
	private JMenuItem menuItemCashBalance;
	
	private JMenuItem menuItemTransactionSupply;
	private JMenuItem menuItemTransactionSelling;
	
	private WebResponse responseTodayCashflow;

	public DashboardPage() {
		super("Dashboard", BASE_WIDTH, BASE_HEIGHT);

	}

	@Override
	public void onShow() {
		if (responseTodayCashflow == null) {
			getHandler().getTodayMonthlyCashflow(callbackUpdateMonthlyCashflow());
			Loadings.end();
		}
	}
	
	@Override
	public void initComponent() {

		PanelRequest mainPanelRequest = mainPanelRequest(); 

		if (labelUserInfo == null) {
			labelUserInfo = title("Welcome to Dasboard!");
		}
		if (panelTodayCashflow == null) {
			panelTodayCashflow = buildCashflowCardPanel(null, null);
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

	
	public JMenuItem menuItem(String text) {
		return new JMenuItem(text);
	}
	
	@Override
	protected void constructMenu() { 
		if(menuBar.getMenuCount()>0) {
			return;
		}
		
		setMenuItemLogout(menuItem("Logout"));
		setMenuItemProduct(menuItem("Product"));
		setMenuItemUnit(menuItem("Unit"));
		setMenuItemSupplier(menuItem("Supplier"));
		setMenuItemCustomer(menuItem("Customer"));
		setMenuItemCategory(menuItem("Category"));
		setMenuItemTransaction(menuItem("List Transction"));
		setMenuItemCostFlow(menuItem("Cost Data"));
		setMenuItemCostType(menuItem("Cost Type"));
		setMenuItemTransactionSupply(menuItem("Supply"));
		setMenuItemProductFlow(menuItem("Product Flow"));
		setMenuItemTransactionSelling(menuItem("Selling"));
		setMenuItemVoucher(menuItem("Voucher Type"));
		setMenuItemCustomerVoucher(menuItem("Member Voucher Data"));
		setMenuItemCapital(menuItem("Capital Type"));
		setMenuItemCapitalFLow(menuItem("Capital Data"));
		setMenuItemCashBalance(menuItem("Balance Journal"));

		
        JMenu managementMenu = new JMenu("Management"); 
        managementMenu.add(menuItemProduct); 
        managementMenu.add(menuItemSupplier);
        managementMenu.add(menuItemCustomer); 
        
        JMenu settingMenu = new JMenu("Setting");
        settingMenu.add(menuItemCostType);
        settingMenu.add(menuItemUnit);
        settingMenu.add(menuItemCategory);
        settingMenu.add(menuItemCapital);
        
        JMenu accountMenu = new JMenu("Account"); 
		accountMenu.add(menuItemLogout);
		accountMenu.add(menuItemCashBalance);
		
		JMenu transactionMenu = new JMenu("Transaction");
		transactionMenu.add(menuItemTransactionSupply);
		transactionMenu.add(menuItemTransactionSelling);
		transactionMenu.add(menuItemTransaction); 
		transactionMenu.add(menuItemProductFlow);
		transactionMenu.add(menuItemCostFlow);
		transactionMenu.add(menuItemCapitalFLow);
		
		JMenu voucherMenu = new JMenu("Voucher"); 
		voucherMenu.add(menuItemVoucher);
		voucherMenu.add(menuItemCustomerVoucher);
		
		menuBar.add(accountMenu ); 
		menuBar.add(settingMenu);
        menuBar.add(managementMenu); 
        menuBar.add(transactionMenu);
        menuBar.add(voucherMenu);
 
	}
	
	private MyCallback callbackUpdateMonthlyCashflow() {
		 
		return new MyCallback() {

			@Override
			public void handle(Object... params) throws Exception {
				WebResponse jsonResponse = (WebResponse) params[0];
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

		PanelRequest panelRequest = new PanelRequest(1, 150, 50, 5, Color.LIGHT_GRAY, 0, 0, 0, 0, false);
		panelRequest.setCenterAligment(true);

		JPanel panel = ComponentBuilder.buildVerticallyInlineComponent( 150, 
				title(title), label("Jumlah"), label(count), label("Nominal"),
				label(amount));

		Border border = BorderFactory.createLineBorder(Color.GRAY, 2);

		panel.setBorder(border);
		return panel;
	}

	/**
	 * update UI when get cash flow data
	 * 
	 * @param response
	 */
	private void callbackMonthlyCashflow(final WebResponse response) {
		ThreadUtil.run(new Runnable() {
			
			@Override
			public void run() {
				setResponseTodayCashflow(response);
				setPanelTodayCashflow(buildTodayCashflowTable(response));
				setPanelMonthlySummary(buildMonthlySummaryTable(response));
				setMinTransactionYear(response.getTransactionYears()[0]);
				AppContext.setContext(ContextConstants.REPORT_STUFF, SharedContext.builder().minTransactionYear(minTransactionYear).build());
				
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
	private JPanel buildMonthlySummaryTable(WebResponse response) {

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

			JPanel panelRow = buildCashflowSummaryTableRow(key,(cashflow), costflow);

			 
			
			components[index] = panelRow;

			updateCountAndAmount(totalCostflow, costflow);
			updateCountAndAmount(totalCashflow, cashflow);

			index++;
		}

		// footer
		components[components.length - 1] = cashflowSummaryFooter(totalCashflow, totalCostflow);
		
		synchronizeComponentWidth(components);
		
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
		return rowPanel (COLUMN_SIZES, "Tanggal", "Jenis Aliran Kas", "Jumlah", "Nominal", "Opsi");
	}

	/**
	 * create table summary
	 * 
	 * @return
	 */
	private Component cashflowSummaryFooter(CashFlow totalCashFlow, CashFlow totalCostFlow) {
		return rowPanel (COLUMN_SIZES, 
				"TOTAL", "Pemasukan", 
				beautifyNominal(totalCashFlow.getAmount()), 
						beautifyNominal(totalCashFlow.getCount()),
						"", "", "Pengeluaran", 
						beautifyNominal(totalCostFlow.getAmount()), 
						beautifyNominal(totalCostFlow.getCount()));
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

		String amountCost = beautifyNominal(cost.getAmount());
		String amountCash = beautifyNominal(income.getAmount());
		String countCost = beautifyNominal(cost.getCount());
		String countCash = beautifyNominal(income.getCount());
		
		JPanel panel = rowPanel(COLUMN_SIZES, color, day, "Pemasukan", amountCash, countCash, buttonDetail,
				"", "Pembelian", amountCost, countCost);
		return panel;
	}

	/**
	 * build cashFlow info card for today's income & spent cost
	 * 
	 * @param response
	 * @return
	 */
	private JPanel buildTodayCashflowTable(WebResponse response) {
		
		if(selectedMonth != DateUtil.getCurrentMonth() || selectedYear != DateUtil.getCurrentYear()) {
			return panelTodayCashflow;
		}

		int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		CashFlow cashflow = response.getMonthlyDetailIncome().get(today);
		CashFlow costflow = response.getMonthlyDetailCost().get(today);

		return buildCashflowCardPanel(cashflow, costflow);
	}
	
	private JPanel buildCashflowCardPanel(CashFlow cashflow, CashFlow costflow) {
		if(null == cashflow) {
			cashflow = new CashFlow();
		}
		if(null == costflow) {
			costflow = new CashFlow();
		}
		JPanel panelCashflow = todayCashflowCard(cashflow.getCount(), cashflow.getAmount(), "Pemasukan");
		JPanel panelCostflow = todayCashflowCard(costflow.getCount(), costflow.getAmount(), "Pembelian"); 
		return ComponentBuilder.buildInlineComponent(200, panelCashflow, panelCostflow); 
	}
 
	/**
	 * build panel for select period
	 * @return
	 */
	private JPanel buildPanelPeriodFilter() {
		

		if(selectedMonth == 0)
		 selectedMonth  = DateUtil.getCurrentMonth();
		if(selectedYear == 0)
		 selectedYear = DateUtil.getCurrentYear();
		if(minTransactionYear == 0)
		 minTransactionYear = DateUtil.getCurrentYear();

		JComboBox _comboBoxMonth = ComponentBuilder.buildComboBox(selectedMonth, buildArray(1,12));
		JComboBox _comboBoxYear = ComponentBuilder.buildComboBox(selectedYear, buildArray(minTransactionYear, Calendar.getInstance().get(Calendar.YEAR)));
		
		setComboBoxMonth(_comboBoxMonth);
		setComboBoxYear(_comboBoxYear);
		setButtonLoadMonthlyCashflow(button("Search/Refresh")); 
		setButtonGotoPeriodicReport(button("Report Page"));
		setButtonGenerateMontlyReport(button("Monthly Report"));
		
		PanelRequest panelRequest = PanelRequest.autoPanelNonScroll(2, 200, 3, Color.WHITE  );
		panelRequest.setCenterAligment(true);
		
		JPanel panel = buildPanelV2(panelRequest ,
					ComponentBuilder.buildInlineComponent(60, label("Month"), comboBoxMonth),
					ComponentBuilder.buildInlineComponent(60, label("Year"), comboBoxYear),
				 
					buttonLoadMonthlyCashflow, 
					buttonGotoPeriodicReport, 
					buttonGenerateMontlyReport);
		 
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
		addActionListener(menuItemProduct, managementListener(Product.class));
		addActionListener(menuItemUnit, managementListener(Unit.class));
		addActionListener(menuItemSupplier, managementListener(Supplier.class));
		addActionListener(menuItemCategory, managementListener(Category.class));
		addActionListener(menuItemCustomer, managementListener(Customer.class));
		addActionListener(menuItemCostFlow, managementListener(CostFlow.class)); 
		addActionListener(menuItemCostType, managementListener(Cost.class)); 
		
		addActionListener(menuItemTransactionSupply, getHandler().navigationListener(PageConstants.PAGE_TRAN_SUPPLY)); 
		addActionListener(menuItemTransactionSelling, getHandler().navigationListener(PageConstants.PAGE_TRAN_SELLING));
		addActionListener(menuItemProductFlow, managementListener(ProductFlow.class));
		addActionListener(menuItemTransaction, managementListener(Transaction.class));
		
		addActionListener(menuItemCashBalance, managementListener(CashBalance.class));
		
		addActionListener(menuItemVoucher, managementListener(Voucher.class)); 
		addActionListener(menuItemCustomerVoucher, managementListener(CustomerVoucher.class)); 
		
		addActionListener(menuItemCapital, managementListener(Capital.class)); 
		addActionListener(menuItemCapitalFLow, managementListener(CapitalFlow.class)); 
		
		addActionListener(buttonLoadMonthlyCashflow, getHandler().getMonthlyCashflow(callbackUpdateMonthlyCashflow()));
		addActionListener(buttonGotoPeriodicReport, getHandler().gotoPeriodicReportPage());
		addActionListener(buttonGenerateMontlyReport, getHandler().generateMonthlyReport());
		
		addActionListener(comboBoxMonth, comboBoxListener(comboBoxMonth,"selectedMonth"));  
		addActionListener(comboBoxYear, comboBoxListener(comboBoxYear,"selectedYear")); 
		
		super.initEvent();
	}
	
	private ActionListener managementListener(Class _class) {
		return getHandler().managementNavigationListener(_class);
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
