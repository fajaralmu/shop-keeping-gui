package com.fajar.shopkeeping.pages;

import java.awt.Color;
import java.awt.Component;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.Product;
import com.fajar.entity.custom.CashFlow;
import com.fajar.shopkeeping.component.ComponentBuilder;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.model.SharedContext;
import com.fajar.shopkeeping.service.AppContext;
import com.fajar.shopkeeping.util.StringUtil;

import lombok.Data;

@Data
public class DailyCashflowPage extends BasePage {

	public static final String CTX_DETAIL_CASHFLOW = "detailCashflow";

	private ShopApiResponse dailyCashflowResponse;
	private JPanel dailyCashflowPanel;

	public DailyCashflowPage() {
		super("Daily Cashflow", BASE_WIDTH, BASE_HEIGHT);
	}

	public DailyCashflowPage(int day, int month, int year) {
		super("Daily Cashflow", BASE_WIDTH, BASE_HEIGHT);

	}

	@Override
	public void initComponent() { 
		
		PanelRequest panelRequest = new PanelRequest(1, 670, 20, 15, Color.WHITE, 30, 30, 0, 0, false, true);

		SharedContext context = AppContext.getContext(CTX_DETAIL_CASHFLOW);
		int day = context.getDay();
		int month = context.getMonth();
		int year = context.getYear();

		if (dailyCashflowPanel == null) { 
			dailyCashflowPanel = buildPanelV2(panelRequest, label("Please wait..."));
			
		}else {
//			dailyCashflowPanel = null;
		}
		
		mainPanel = ComponentBuilder.buildPanelV2(panelRequest,

				title("Detail " + day + " " + StringUtil.months[month - 1] + " " + year, 30),
				dailyCashflowPanel

		);  
		 
		parentPanel.add(mainPanel);
		 
		System.out.println("____________ INIT COMPONENT______________");
	}

	public void update() {
		
		dailyCashflowPanel = buildDetailTable();
		System.out.println("dailyCashflowPanel: "+dailyCashflowPanel.getComponentCount());
		preInitComponent();
	}

	private JPanel buildDetailTable() {

		if (null == dailyCashflowResponse) {
			return new JPanel();
		}

		Map<String, CashFlow> dailyCashflowMap = dailyCashflowResponse.getDailyCashflow();
		Set<String> keys = dailyCashflowMap.keySet();
		Component[] components = new Component[keys.size() + 1];
		components[0] = dailyCashflowHeader();
		
		int index = 1;

		for (String key : keys) {
			
			CashFlow cashflow = dailyCashflowMap.get(key); 
			Product product = cashflow.getProduct();
			String productName = product.getName().length() > 20 ? product.getName().substring(0, 20) :  product.getName();
			components[index] = rowPanel(4, 100, index, productName, cashflow.getCount(), cashflow.getAmount());
			
			index++;
		}
		PanelRequest panelRequest = PanelRequest.autoPanelScroll(1, 410, 1, Color.LIGHT_GRAY, 260); 
		
		JPanel panel = buildPanelV2(panelRequest, components);
		
		return panel;

	}

	private Component dailyCashflowHeader() {
		 
		return rowPanelHeader(4, 100, "No", "Product", "Penjualan", "Nilai");
	}

	@Override
	protected void initEvent() {
		super.initEvent();

	}

}
