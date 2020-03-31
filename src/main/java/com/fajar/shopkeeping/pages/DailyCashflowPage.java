package com.fajar.shopkeeping.pages;

import java.awt.Color;

import javax.swing.JPanel;

import com.fajar.dto.ShopApiResponse;
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

		JPanel mainPanel = ComponentBuilder.buildPanelV2(panelRequest,

				title("Detail " + day + " " + StringUtil.months[month - 1] + " " + year, 30)

		);

		parentPanel.add(mainPanel);

	}

	@Override
	protected void initEvent() {
		super.initEvent();

	}

}
