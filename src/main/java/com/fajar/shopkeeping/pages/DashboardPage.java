package com.fajar.shopkeeping.pages;

import static com.fajar.shopkeeping.component.builder.ComponentActionListeners.addActionListener;
import static com.fajar.shopkeeping.component.builder.ComponentBuilder.button;
import static com.fajar.shopkeeping.component.builder.ComponentBuilder.label;
import static com.fajar.shopkeeping.component.builder.ComponentBuilder.title;
import static com.fajar.shopkeeping.util.StringUtil.beautifyNominal;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.List;
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

import com.fajar.shopkeeping.callbacks.WebResponseCallback;
import com.fajar.shopkeeping.component.Loadings;
import com.fajar.shopkeeping.component.ManagementMenuItem;
import com.fajar.shopkeeping.component.builder.ComponentBuilder;
import com.fajar.shopkeeping.component.builder.ComponentModifier;
import com.fajar.shopkeeping.constant.ContextConstants;
import com.fajar.shopkeeping.constant.PageConstants;
import com.fajar.shopkeeping.handler.DashboardHandler;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.model.SharedContext;
import com.fajar.shopkeeping.service.AppContext;
import com.fajar.shopkeeping.service.AppSession;
import com.fajar.shopkeeping.util.DateUtil;
import com.fajar.shopkeeping.util.ThreadUtil;
import com.fajar.shoppingmart.dto.Filter;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.entity.Capital;
import com.fajar.shoppingmart.entity.CapitalFlow;
import com.fajar.shoppingmart.entity.CashBalance;
import com.fajar.shoppingmart.entity.Category;
import com.fajar.shoppingmart.entity.Cost;
import com.fajar.shoppingmart.entity.CostFlow;
import com.fajar.shoppingmart.entity.Customer;
import com.fajar.shoppingmart.entity.CustomerVoucher;
import com.fajar.shoppingmart.entity.Product;
import com.fajar.shoppingmart.entity.ProductFlow;
import com.fajar.shoppingmart.entity.Supplier;
import com.fajar.shoppingmart.entity.Transaction;
import com.fajar.shoppingmart.entity.Unit;
import com.fajar.shoppingmart.entity.Voucher;
import com.fajar.shoppingmart.entity.custom.CashFlow;
import com.fajar.shoppingmart.util.EntityUtil;

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
	
	private JComboBox<?> comboBoxMonth;
	private JComboBox<?> comboBoxYear; 
	
	private int minTransactionYear;
	private int selectedMonth;  
	private int selectedYear;  
	
	private JMenuItem menuItemLogout;
	private ManagementMenuItem menuItemProduct;
	private ManagementMenuItem menuItemUnit;
	private ManagementMenuItem menuItemSupplier;
	private ManagementMenuItem menuItemCustomer;
	private ManagementMenuItem menuItemCategory;
	private ManagementMenuItem menuItemTransaction;
	private ManagementMenuItem menuItemCostFlow;
	private ManagementMenuItem menuItemCostType;
	private ManagementMenuItem menuItemProductFlow;
	
	private ManagementMenuItem menuItemVoucher;
	private ManagementMenuItem menuItemCustomerVoucher;
	private ManagementMenuItem menuItemCapital;
	private ManagementMenuItem menuItemCapitalFLow;
	private ManagementMenuItem menuItemCashBalance;
	
	private JMenuItem menuItemTransactionSupply;
	private JMenuItem menuItemTransactionSelling;
	
	private WebResponse responseTodayCashflow;

	public DashboardPage() {
		super("Dashboard", BASE_WIDTH, BASE_HEIGHT);

	}

	@Override
	public void onShow() {
		if (responseTodayCashflow == null) {
			getHandler().getTodayMonthlyCashflow(this::callbackMonthlyCashflow);
			Loadings.end();
		}
	}
	
	@Override
	public void initComponent() {

		PanelRequest mainPanelRequest = mainPanelRequest(); 

		if (labelUserInfo == null) {
			labelUserInfo =  title("Welcome to Dasboard!");
		}
		if (panelTodayCashflow == null) {
			panelTodayCashflow = buildCashflowCardPanel(null, null);
		}
		if (panelMonthlySummary == null) {
			panelMonthlySummary = buildPanelV2(panelCashflowRequest(), label("Please wait..."));
		}
		
		setPanelPeriodFilter(buildPanelPeriodFilter());  
		
		mainPanel = buildPanelV2(mainPanelRequest, 
				title(AppSession.getApplicationProfile().getName()), labelUserInfo,  
				label("ALIRAN KAS HARI INI "+DateUtil.todayString()), 
				panelTodayCashflow, panelPeriodFilter, 
				panelMonthlySummary);

		parentPanel.add(mainPanel);

		exitOnClose();

	}

	
	public ManagementMenuItem menuItem(String text, Class<? extends BaseEntity> _class) {
		return new ManagementMenuItem(text, _class);
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
		setMenuItemProduct(menuItem("Product", Product.class));
		setMenuItemUnit(menuItem("Unit", Unit.class));
		setMenuItemSupplier(menuItem("Supplier", Supplier.class));
		setMenuItemCustomer(menuItem("Customer", Customer.class));
		setMenuItemCategory(menuItem("Product Category", Category.class));
		setMenuItemTransaction(menuItem("List Transaction", Transaction.class));
		setMenuItemCostFlow(menuItem("Cost Journal ", CostFlow.class));
		setMenuItemCostType(menuItem("Cost Type", Cost.class));
		setMenuItemTransactionSupply(menuItem("Purchasing"));
		setMenuItemProductFlow(menuItem("Product Flow", ProductFlow.class));
		setMenuItemTransactionSelling(menuItem("Selling"));
		setMenuItemVoucher(menuItem("Voucher Type", Voucher.class));
		setMenuItemCustomerVoucher(menuItem("Member Voucher Data", CustomerVoucher.class));
		setMenuItemCapital(menuItem("Capital Type", Capital.class));
		setMenuItemCapitalFLow(menuItem("Capital Journal", CapitalFlow.class));
		setMenuItemCashBalance(menuItem("Balance Journal", CashBalance.class));

		
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
		
		ComponentModifier.
			addMenuForMenuBar(menuBar, accountMenu, settingMenu, managementMenu, voucherMenu);
 
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
		ThreadUtil.run(()-> {
			setResponseTodayCashflow(response);
			setPanelTodayCashflow(buildTodayCashflowTable(response));
			setPanelMonthlySummary(buildMonthlySummaryTable(response));
			setMinTransactionYear(response.getTransactionYears()[0]);
			AppContext.setContext(ContextConstants.REPORT_STUFF, SharedContext.builder().minTransactionYear(minTransactionYear).build());
			
			preInitComponent();
			initEvent(); 
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
		
		ComponentModifier.synchronizeComponentWidth(components);
		
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

		Color color = Color.WHITE;//day % 2 == 0 ? Color.WHITE : Color.WHITE;
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

		JComboBox<?> _comboBoxMonth = ComponentBuilder.buildComboBox(selectedMonth, buildArray(1,12));
		JComboBox<?> _comboBoxYear = ComponentBuilder.buildComboBox(selectedYear, buildArray(minTransactionYear, Calendar.getInstance().get(Calendar.YEAR)));
		
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
	
	private void initManagementMenuEvents() {
		List<Field> fields = EntityUtil.getDeclaredFields(getClass());
		for (int i = 0; i < fields.size(); i++) {
			Field field = fields.get(i);
			if(field.getType().equals(ManagementMenuItem.class)) {
				field.setAccessible(true);
				try {
					ManagementMenuItem value = (ManagementMenuItem) field.get(this);
					addActionListener(value, managementListener(value.getEntityClass()));
				} catch (Exception e) { 
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void initEvent() { 
		/**
		 * menus
		 */
		//account
		addActionListener(menuItemLogout, getHandler().logout());
		//management
		initManagementMenuEvents();
		
		addActionListener(menuItemTransactionSupply, getHandler().navigationListener(PageConstants.PAGE_TRAN_SUPPLY)); 
		addActionListener(menuItemTransactionSelling, getHandler().navigationListener(PageConstants.PAGE_TRAN_SELLING));
		
		addActionListener(buttonLoadMonthlyCashflow, getHandler().getMonthlyCashflow(this::callbackMonthlyCashflow));
		addActionListener(buttonGotoPeriodicReport, getHandler().gotoPeriodicReportPage());
		addActionListener(buttonGenerateMontlyReport, getHandler().generateMonthlyReport());
		
		addActionListener(comboBoxMonth, comboBoxListener(comboBoxMonth,"selectedMonth"));  
		addActionListener(comboBoxYear, comboBoxListener(comboBoxYear,"selectedYear")); 
		
		super.initEvent();
	}
	
	private ActionListener managementListener(Class<? extends BaseEntity> _class) {
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
