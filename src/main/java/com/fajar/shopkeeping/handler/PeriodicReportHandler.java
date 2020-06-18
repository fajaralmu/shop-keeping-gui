package com.fajar.shopkeeping.handler;

import org.springframework.http.ResponseEntity;

import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.Loadings;
import com.fajar.shopkeeping.constant.ReportType;
import com.fajar.shopkeeping.model.ReportResponse;
import com.fajar.shopkeeping.pages.PeriodicReportPage;
import com.fajar.shopkeeping.util.Log;
import com.fajar.shoppingmart.dto.Filter;
import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;

public class PeriodicReportHandler extends MainHandler {

	public PeriodicReportHandler() {
		super();
	}

	@Override
	protected void init() {
		super.init();
		page = new PeriodicReportPage();
	}

	public void getPeriodicCashflow(Filter filter, MyCallback<WebResponse> callback) {
		Log.log("filter: ", filter);
		reportService.getPeriodicCashflow(filter, callback);

	}

	public void generateExcelReportDaily(final int month, final int year) {

		Filter filter = Filter.builder().month(month).year(year).build();
		WebRequest webRequest = WebRequest.builder().filter(filter).build(); 

		MyCallback<ReportResponse> myCallback = new MyCallback<ReportResponse>() {

			@Override
			public void handle(ReportResponse reportResponse) throws Exception {
				Log.log("Response daily excel: ", reportResponse.getReportType());
				ResponseEntity<byte[]> response = reportResponse.getFileResponse();
				Loadings.end();
				 
				String fileName = getFileName(response);
				saveFile(response.getBody(), fileName);
			}
		};
		reportService.downloadReportExcel(webRequest, myCallback, ReportType.DAILY);

	}  
	
	private PeriodicReportPage getPage() {

		return (PeriodicReportPage) page;
	}
	
	

}
