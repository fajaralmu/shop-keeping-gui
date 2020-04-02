package com.fajar.shopkeeping.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.JComboBox;

import com.fajar.dto.Filter;
import com.fajar.dto.ShopApiResponse;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.constant.PageConstants;
import com.fajar.shopkeeping.model.SharedContext;
import com.fajar.shopkeeping.pages.DailyCashflowPage;
import com.fajar.shopkeeping.pages.DashboardPage;
import com.fajar.shopkeeping.service.AppContext;

public class DashboardHandler extends MainHandler {

	public DashboardHandler() {
		super();
	}

	@Override
	protected void init() {
		super.init();
		page = new DashboardPage();
	}

	public ActionListener logout() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				accountService.logout(new MyCallback() {

					@Override
					public void handle(Object... params) throws Exception {

						boolean success = (Boolean) params[0];
						if (success) {
							APP_HANDLER.navigate(PageConstants.PAGE_LOGIN);
						}
					}
				});
			}
		};
	}

	public void getTodayMonthlyCashflow(MyCallback callback) {

		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH) + 1; // January is 1
		int year = calendar.get(Calendar.YEAR);

		reportService.getMonthlyCashflowDetail(month, year, callback);

	}

	public ActionListener getDailyCashflow(final int day, final int month, final int year) {

		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				reportService.getDailyCashflowDetail(day, month, year, new MyCallback() {

					@Override
					public void handle(Object... params) throws Exception {

						ShopApiResponse response = (ShopApiResponse) params[0];
						handleResponseDailyCashflow(response);
					}
				});
			}
		};
	}

	private void handleResponseDailyCashflow(ShopApiResponse shopApiResponse) {

		Filter filter = shopApiResponse.getFilter();

		AppContext.setContext(DailyCashflowPage.CTX_DETAIL_CASHFLOW,
				new SharedContext(filter.getDay(), filter.getMonth(), filter.getYear()));

		DailyCashflowPage dailyCashflowPage = new DailyCashflowPage(filter.getDay(), filter.getMonth(),
				filter.getYear());
		dailyCashflowPage.setAppHandler(this);
		dailyCashflowPage.setDailyCashflowResponse(shopApiResponse);
		dailyCashflowPage.update();
		dailyCashflowPage.show();
	}

	public ActionListener getMonthlyCashflow(final JComboBox comboBoxMonth, final JComboBox comboBoxYear,
			final MyCallback callback) {

		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Object month = comboBoxMonth.getSelectedItem();
				Object year = comboBoxYear.getSelectedItem();
				reportService.getMonthlyCashflowDetail(toInt(month), toInt(year), callback);
			}
		};
	}

	private int toInt(Object o) {
		try {
			return Integer.parseInt(o.toString());
		} catch (Exception e) { 
			return 0;
		}
	}

	public ActionListener gotoPeriodicReportPage() {
		return navigationListener(PageConstants.PAGE_PERIODIC_REPORT);
	}

	public ActionListener gotoManagementPage() {
		return navigationListener(PageConstants.PAGE_MANAGEMENT);
	}

}
