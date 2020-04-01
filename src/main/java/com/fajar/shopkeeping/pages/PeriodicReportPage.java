package com.fajar.shopkeeping.pages;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.fajar.dto.Filter;
import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.custom.CashFlow;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.ComponentBuilder;
import com.fajar.shopkeeping.handler.PeriodicReportHandler;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.model.SharedContext;
import com.fajar.shopkeeping.service.AppContext;
import com.fajar.shopkeeping.util.DateUtil;

public class PeriodicReportPage extends BasePage {
	
	private static final int COLUMN_WIDTH = 160; 
	private static final int COLUMN = 4;
	private static final int TABLE_WIDTH = (COLUMN_WIDTH) *  COLUMN  ;

	private JComboBox comboBoxMonthFrom;
	private JComboBox comboBoxYearFrom;
	private JComboBox comboBoxMonthTo;
	private JComboBox comboBoxYearTo;
	
	private JButton buttonSearch;
	
	private JPanel panelFilterPeriod;
	private JPanel panelCashflowListTable;

	private int selectedMonthTo = DateUtil.getCurrentMonth();
	private int selectedYearTo = DateUtil.getCurrentYear();
	private int selectedMonthFrom = DateUtil.getCurrentMonth();
	private int selectedYearFrom = DateUtil.getCurrentYear();
	
	private ShopApiResponse periodicCashflowResponse;

	public PeriodicReportPage() {
		super("Periodic Report", BASE_WIDTH, BASE_HEIGHT);
	}

	@Override
	public void initComponent() {

		PanelRequest panelRequest = new PanelRequest(1, 670, 20, 15, Color.WHITE, 30, 30, 0, 0, false, true);

		panelFilterPeriod = buildPanelPeriodFilter();
		if(null == panelCashflowListTable) {
			panelCashflowListTable = buildPanelV2(panelRequest, label("Please wait..."));
		}
		
		mainPanel = ComponentBuilder.buildPanelV2(panelRequest,

				title("HALAMAN PERIODIC REPORT", 30),
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

		PanelRequest panelRequest = PanelRequest.autoPanelNonScroll(3, 70, 5, Color.WHITE);

		JPanel panel = buildPanelV2(panelRequest, 
					BLANK_LABEL,	label("From"),		label("To") , 
					label("Month"), comboBoxMonthFrom, 	comboBoxMonthTo,
					label("Year"), 	comboBoxYearFrom, 	comboBoxYearTo, buttonSearch);

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
		comboBoxMonthFrom.addActionListener(comboBoxListener(comboBoxMonthFrom, "selectedMonthFrom"));
		comboBoxYearFrom.addActionListener(comboBoxListener(comboBoxYearFrom, "selectedYearFrom"));
		comboBoxMonthTo.addActionListener(comboBoxListener(comboBoxMonthTo, "selectedMonthTo"));
		comboBoxYearTo.addActionListener(comboBoxListener(comboBoxYearTo, "selectedYearTo"));
		buttonSearch.addActionListener(buttonSearchListener());

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
				 ShopApiResponse response = (ShopApiResponse) params[0];
				 handlePeriodicCashflow(response);
			} 
			
		};
	}
	
	private void handlePeriodicCashflow(ShopApiResponse response) {
		
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
		
		for (int i = 0; i < arraySize; i++) {
			
			CashFlow cashflow = (CashFlow) productSold.get(i);
			CashFlow costflow = (CashFlow) productSupplied.get(i);
			
			int month = cashflow.getMonth();
			int year = cashflow.getYear();
			
			String periodLabel = (DateUtil.dateString(month, year));
			
			JPanel rowPanel = rowPanel(COLUMN, COLUMN_WIDTH, Color.white, 
					
					periodLabel, 	"Pemasukan", 	cashflow.getCount(), 	cashflow.getAmount(),
					null,	"Pengeluaran", 	costflow.getCount(), 	costflow.getAmount() );
			
			components[i + 1] = rowPanel;
		}
		
		log("ARRAYSIZE: "+ arraySize);
		PanelRequest panelRequest = PanelRequest.autoPanelScroll(1, TABLE_WIDTH, 1, Color.LIGHT_GRAY, 400); 
		
		JPanel panel = buildPanelV2(panelRequest, components);
		return panel;
	}

	private Component periodicCashflowHeader() {
		return rowPanelHeader(COLUMN, COLUMN_WIDTH,  "Periode", "Jenis", "Jumlah", "Nilai" );
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