package com.fajar.shopkeeping.pages;

import static com.fajar.shopkeeping.component.ComponentBuilder.label;
import static com.fajar.shopkeeping.util.StringUtil.beautifyNominal;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.fajar.dto.Filter;
import com.fajar.dto.WebResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.custom.CashFlow;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.ComponentBuilder;
import com.fajar.shopkeeping.handler.PeriodicReportHandler;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.model.SharedContext;
import com.fajar.shopkeeping.service.AppContext;
import com.fajar.shopkeeping.util.DateUtil;
import com.fajar.shopkeeping.util.Log;
import com.fajar.shopkeeping.util.ThreadUtil;

public class PeriodicReportPage extends BasePage {
	
	private static final int COLUMN_WIDTH = 160; 
	private static final int COLUMN = 5;
	private static final int TABLE_WIDTH = (COLUMN_WIDTH) *  COLUMN  ;

	private JComboBox comboBoxMonthFrom;
	private JComboBox comboBoxYearFrom;
	private JComboBox comboBoxMonthTo;
	private JComboBox comboBoxYearTo;
	
	private JButton buttonSearch;
	private JButton buttonRefresh;
	
	private JPanel panelFilterPeriod;
	private JPanel panelCashflowListTable;

	private int selectedMonthTo = DateUtil.getCurrentMonth();
	private int selectedYearTo = DateUtil.getCurrentYear();
	private int selectedMonthFrom = DateUtil.getCurrentMonth();
	private int selectedYearFrom = DateUtil.getCurrentYear();
	
	private WebResponse periodicCashflowResponse;

	public PeriodicReportPage() {
		super("Periodic Report", BASE_WIDTH, BASE_HEIGHT);
		doNotCloseOtherPage();
	}

	@Override
	public void initComponent() {

		PanelRequest panelRequest = new PanelRequest(1, 670, 20, 15, Color.WHITE, 30, 30, 0, 0, false, true);

		panelFilterPeriod = buildPanelPeriodFilter();
		if(null == panelCashflowListTable) {
			panelCashflowListTable = buildPanelV2(panelRequest, label("Please Select The Period"));
		}
		
		mainPanel = ComponentBuilder.buildPanelV2(panelRequest,

				title("PERIODIC REPORT", 30),
				panelFilterPeriod,
				null,
				panelCashflowListTable);

		parentPanel.add(mainPanel); 
//		exitOnClose();

	}

	/**
	 * building panel for period selection
	 * @return
	 */
	private JPanel buildPanelPeriodFilter() {
		// getting context value
		SharedContext context = AppContext.getContext(REPORT_STUFF);
		int minTransactionYear = context.getMinTransactionYear();

		comboBoxMonthFrom = buildComboBox(selectedMonthFrom, buildArray(1, 12));
		comboBoxYearFrom = buildComboBox(selectedYearFrom,
				buildArray(minTransactionYear, Calendar.getInstance().get(Calendar.YEAR)));

		comboBoxMonthTo = buildComboBox(selectedMonthTo, buildArray(1, 12));
		comboBoxYearTo = buildComboBox(selectedYearTo,
				buildArray(minTransactionYear, Calendar.getInstance().get(Calendar.YEAR)));

		buttonSearch = button("Search");
		buttonRefresh = button("Refresh");

		PanelRequest panelRequest = PanelRequest.autoPanelNonScroll(3, 70, 5, Color.WHITE);

		JPanel panel = buildPanelV2(panelRequest, 
					BLANK_LABEL,	label("From"),		label("To") , 
					label("Month"), comboBoxMonthFrom, 	comboBoxMonthTo,
					label("Year"), 	comboBoxYearFrom, 	comboBoxYearTo, buttonSearch, buttonRefresh);

		return panel;
	}

	private JComboBox buildComboBox(int defaultValue, Object... buildArray) { 
		JComboBox comboBox =  ComponentBuilder.buildComboBox(defaultValue, buildArray);
		comboBox.setSize(60, 20);
		return comboBox;
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		addActionListener(comboBoxMonthFrom, comboBoxListener(comboBoxMonthFrom, "selectedMonthFrom"));
		addActionListener(comboBoxYearFrom, comboBoxListener(comboBoxYearFrom, "selectedYearFrom"));
		addActionListener(comboBoxMonthTo, comboBoxListener(comboBoxMonthTo, "selectedMonthTo"));
		addActionListener(comboBoxYearTo, comboBoxListener(comboBoxYearTo, "selectedYearTo"));
		addActionListener(buttonSearch, buttonSearchListener());
		addActionListener(buttonRefresh, buttonRefreshListener());

	}
	  
	
	@Override
	public void refresh() {
		callbackPeriodicCashflow(periodicCashflowResponse);
		Log.log("refresh done...");
		super.refresh();
	}

	private ActionListener buttonSearchListener() { 
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				log("Button Click");
				
				 Filter filter = new Filter();
				 filter.setMonth(selectedMonthFrom);
				 filter.setYear(selectedYearFrom);
				 filter.setMonthTo(selectedMonthTo);
				 filter.setYearTo(selectedYearTo);
				 
				 getHandler().getPeriodicCashflow(filter, callbackGetPeriodicCashflow());
				
			} 
		};
	}

	private MyCallback callbackGetPeriodicCashflow() {
		 
		return new MyCallback() {
			
			@Override
			public void handle(Object... params) throws Exception {
				 WebResponse response = (WebResponse) params[0];
				 callbackPeriodicCashflow(response);
			} 
			
		};
	}
	
	private void callbackPeriodicCashflow(final WebResponse response) {
		
		ThreadUtil.run(new Runnable() {
			
			@Override
			public void run() {
				if(null == panelCashflowListTable) {
					log("panelCashflowListTable IS NULL");
					return;
				}
				
				periodicCashflowResponse = response;
				
				try {
					panelCashflowListTable = buildPeriodicCashflowTable();
				}catch (Exception e) {
					e.printStackTrace();
				}
				
				preInitComponent();
				initEvent();
				
			}
		});
		
	}
	
	private JPanel buildPeriodicCashflowTable() {
		
		if(null == periodicCashflowResponse) {
			log("periodicCashflowResponse IS NULL");
			return new JPanel();
		} 
		
		List<BaseEntity> productSupplied = periodicCashflowResponse.getSupplies();
		List<BaseEntity> productSold = periodicCashflowResponse.getPurchases();
		
		final int arraySize = productSupplied.size();
		
		Component[] components = new Component[arraySize + 2];
		components[0] = periodicCashflowHeader();
		
		CashFlow totalCashflow = new CashFlow();
		CashFlow totalCostflow = new CashFlow();
		
		for (int i = 0; i < arraySize; i++) {
			
			CashFlow cashflow = (CashFlow) productSold.get(i);
			CashFlow costflow = (CashFlow) productSupplied.get(i);
			
			int month = cashflow.getMonth();
			int year = cashflow.getYear();
			
			String periodLabel = (DateUtil.dateString(month, year));
			JButton buttonGenerateReport = button("Report", 150, generateDailyReportListener(month, year));
			
			JPanel rowPanel = rowPanel(COLUMN, COLUMN_WIDTH, Color.white, 
					
					periodLabel, 	"Pemasukan", 	
					beautifyNominal(cashflow.getCount()), 	
					beautifyNominal(cashflow.getAmount()),
					buttonGenerateReport, null,
					"Pengeluaran", 	
					beautifyNominal(costflow.getCount()), 
					beautifyNominal(costflow.getAmount()));
			
			updateCountAndAmount(totalCashflow, cashflow);
			updateCountAndAmount(totalCostflow, costflow);
			
			components[i + 1] = rowPanel;
		}
		
		components[components.length-1] = cashflowPeriodicFooter(totalCashflow,totalCostflow);
		
		log("ARRAYSIZE: "+ arraySize);
		PanelRequest panelRequest = PanelRequest.autoPanelScrollWidthHeightSpecified(1, TABLE_WIDTH  , 5, Color.LIGHT_GRAY, (BASE_WIDTH * 4)/5, 400); 
		
		synchronizeComponentWidth(components);
		
		JPanel panel = buildPanelV2(panelRequest, components);
		panel.setBorder(BorderFactory.createLineBorder(Color.blue));
		return panel;
	}

	private ActionListener generateDailyReportListener(final int month, final int year) {  
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) { 
				getHandler().generateExcelReportDaily(month, year);
			}
		};
	}

	private Component cashflowPeriodicFooter(CashFlow totalCashflow, CashFlow totalCostflow) { 
		return rowPanelHeader( COLUMN, COLUMN_WIDTH,  
				
				"TOTAL", 	"Pemasukan", 	
				beautifyNominal(totalCashflow.getCount()),
				beautifyNominal(totalCashflow.getAmount()),
				null, null,	"Pengeluaran", 	
				beautifyNominal(totalCostflow.getCount()), 	
				beautifyNominal(totalCostflow.getAmount())) ;
	}

	private Component periodicCashflowHeader() {
		return rowPanelHeader(COLUMN, COLUMN_WIDTH,  "Periode", "Jenis", "Jumlah", "Nilai", "Opsi" );
	}

	@Override
	public void show() {
		super.show();
		preInitComponent();
		initEvent();
	}
	
	private PeriodicReportHandler getHandler() {
		return (PeriodicReportHandler) appHandler;
	}

}
