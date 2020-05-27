package com.fajar.shopkeeping.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import org.springframework.http.ResponseEntity;

import com.fajar.dto.Filter;
import com.fajar.dto.WebRequest;
import com.fajar.dto.WebResponse;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.Loadings;
import com.fajar.shopkeeping.constant.ContextConstants;
import com.fajar.shopkeeping.constant.PageConstants;
import com.fajar.shopkeeping.constant.ReportType;
import com.fajar.shopkeeping.model.SharedContext;
import com.fajar.shopkeeping.pages.DailyCashflowPage;
import com.fajar.shopkeeping.pages.DashboardPage;
import com.fajar.shopkeeping.service.AppContext;
import com.fajar.shopkeeping.util.Log;

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

						WebResponse response = (WebResponse) params[0];
						handleResponseDailyCashflow(response);
					}
				});
			}
		};
	}

	private void handleResponseDailyCashflow(WebResponse WebResponse) {

		Filter filter = WebResponse.getFilter();

		AppContext.setContext(ContextConstants.CTX_DETAIL_CASHFLOW,
				new SharedContext(filter.getDay(), filter.getMonth(), filter.getYear()));

		DailyCashflowPage dailyCashflowPage = new DailyCashflowPage(filter.getDay(), filter.getMonth(),
				filter.getYear());
		dailyCashflowPage.setAppHandler(this);
		dailyCashflowPage.setDailyCashflowResponse(WebResponse);
		dailyCashflowPage.update();
		dailyCashflowPage.show();
	}
	
	public ActionListener generateMonthlyReport( ) {
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				generateExcelReportMontly(getPage().getSelectedYear());
			}
		};
	}
	
	public void generateExcelReportMontly( final int year) {

		Filter filter = Filter.builder().year(year).build();
		WebRequest webRequest =  WebRequest.builder().filter(filter).build(); 

		MyCallback myCallback = new MyCallback() {

			@Override
			public void handle(Object... params) throws Exception {
				Log.log("Response daily excel: ", params[0]);
				ResponseEntity<byte[]> response = (ResponseEntity<byte[]>) params[0];
				Loadings.end();
				 
				String fileName = getFileName(response);
				saveFile(response.getBody(), fileName);
			}
		};
		reportService.downloadReportExcel(webRequest, myCallback, ReportType.MONTHLY);

	}

	public ActionListener getMonthlyCashflow(final MyCallback callback) {

		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Object month = getPage().getSelectedMonth();
				Object year = getPage().getSelectedYear();
				reportService.getMonthlyCashflowDetail(toInt(month), toInt(year), callback);
			}
		};
	}
	
	private DashboardPage getPage() {
		return (DashboardPage) page;
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
	
	public ActionListener managementNavigationListener( final Class<?> entityClass) {
		return new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				SharedContext context = SharedContext.builder().entityClass(entityClass).build();
				AppContext.setContext(ContextConstants.CTX_MANAGEMENT_PAGE, context );
				APP_HANDLER.navigate(PageConstants.PAGE_MANAGEMENT);
			}
		};
	}

}
