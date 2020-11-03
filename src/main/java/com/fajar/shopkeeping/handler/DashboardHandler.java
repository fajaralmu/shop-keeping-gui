package com.fajar.shopkeeping.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import org.springframework.http.ResponseEntity;

import com.fajar.shopkeeping.callbacks.ApplicationException;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.Loadings;
import com.fajar.shopkeeping.constant.ContextConstants;
import com.fajar.shopkeeping.constant.PageConstants;
import com.fajar.shopkeeping.constant.ReportType;
import com.fajar.shopkeeping.model.ReportResponse;
import com.fajar.shopkeeping.model.SharedContext;
import com.fajar.shopkeeping.pages.DailyCashflowPage;
import com.fajar.shopkeeping.pages.DashboardPage;
import com.fajar.shopkeeping.service.AppContext;
import com.fajar.shoppingmart.dto.Filter;
import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.BaseEntity;

public class DashboardHandler extends MainHandler<DashboardPage> {

	public DashboardHandler() {
		super();
	}

	@Override
	protected void init() {
		super.init();
		page = new DashboardPage();
	}

	public ActionListener logout() {
		return  (ActionEvent e)->{

			accountService.logout((Boolean success) -> { 
				if (success) {
					APP_HANDLER.navigate(PageConstants.PAGE_LOGIN);
				} 
			}); 
		};
	}

	public void getTodayMonthlyCashflow(MyCallback<WebResponse> callback) {

		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH) + 1; // January is 1
		int year = calendar.get(Calendar.YEAR);

		reportService.getMonthlyCashflowDetail(month, year, callback);

	}

	public ActionListener getDailyCashflow(final int day, final int month, final int year) {

		return  (ActionEvent e)-> {

			reportService.getDailyCashflowDetail(day, month, year,  this::handleResponseDailyCashflow); 
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
		return (ActionEvent e)->{ 
				generateExcelReportMontly(getPage().getSelectedYear()); 
		};
	}
	
	public void generateExcelReportMontly( final int year) {

		Filter filter = Filter.builder().year(year).build();
		WebRequest webRequest =  WebRequest.builder().filter(filter).build(); 

		MyCallback<ReportResponse> myCallback =  (ReportResponse reportResponse) ->{
				 
			ResponseEntity<byte[]> response = reportResponse.getFileResponse();
			Loadings.end();
			 
			String fileName = getFileName(response);
			try {
			saveFile(response.getBody(), fileName);
			}catch (Exception e) {
				// TODO: handle exception
				throw new ApplicationException(e);
			} 
		};
		reportService.downloadReportExcel(webRequest, myCallback, ReportType.MONTHLY);

	}

	public ActionListener getMonthlyCashflow(final MyCallback<WebResponse> callback) {

		return (ActionEvent e)-> {
			Object month = getPage().getSelectedMonth();
			Object year = getPage().getSelectedYear();
			reportService.getMonthlyCashflowDetail(toInt(month), toInt(year), callback); 
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
	
	public ActionListener managementNavigationListener( final Class<? extends BaseEntity> entityClass) {
		return  (ActionEvent e)-> {
			SharedContext context = SharedContext.builder().entityClassForManagement(entityClass).build();
			AppContext.setContext(ContextConstants.CTX_MANAGEMENT_PAGE, context );
			APP_HANDLER.navigate(PageConstants.PAGE_MANAGEMENT); 
		};
	}

}
