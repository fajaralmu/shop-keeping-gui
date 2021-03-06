package com.fajar.shopkeeping.service;

import static com.fajar.shopkeeping.constant.WebServiceConstants.URL_DAILY_CASHFOW;
import static com.fajar.shopkeeping.constant.WebServiceConstants.URL_MONTHLY_CASHFOW;
import static com.fajar.shopkeeping.constant.WebServiceConstants.URL_PERIODIC_CASHFOW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.Dialogs;
import com.fajar.shopkeeping.constant.ReportType;
import com.fajar.shopkeeping.constant.WebServiceConstants;
import com.fajar.shopkeeping.model.ReportResponse;
import com.fajar.shopkeeping.util.Log;
import com.fajar.shopkeeping.util.MapUtil;
import com.fajar.shopkeeping.util.ObjectUtil;
import com.fajar.shopkeeping.util.ThreadUtil;
import com.fajar.shoppingmart.dto.Filter;
import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.entity.custom.CashFlow;

public class ReportService extends BaseService{ 

	private static ReportService app;

	public static ReportService getInstance() {
		if (null == app) {
			app = new ReportService();
		}
		return app;
	}

	private ReportService() {

	}

	/**
	 * 
	 * @param month
	 * @param year
	 * @param callback parameter #1  : JSON response (WebResponse.class)
	 */
	public void getMonthlyCashflowDetail(final int month, final int year, final MyCallback<WebResponse> callback) {
		ThreadUtil.runWithLoading(()->{ 
			try { 
				WebResponse jsonResponse = callGetMothlyCashflowDetail(month, year);

				callback.handle(jsonResponse);
			} catch (Exception e) {
				e.printStackTrace();
				Dialogs.error("Error getMonthlyCashflowDetail: " + e.getMessage());
			} finally { } 
		}); 
	}

	/**
	 * get daily product flow
	 * 
	 * @param day
	 * @param month
	 * @param year
	 * @param callback parameter #1 : JSON response (WebResponse.class)
	 */
	public void getDailyCashflowDetail(final int day, final int month, final int year, final MyCallback<WebResponse> callback) {
		ThreadUtil.runWithLoading (()->{ 
			try { 
				WebResponse jsonResponse = callDailyCashflow(day, month, year);

				callback.handle(jsonResponse);
			} catch (Exception e) {
				e.printStackTrace();
				Dialogs.error("Error getDailyCashflowDetail: " + e.getMessage());
			} finally { } 

		}); 
	}

	/**
	 * get cash flow from selected period to selected period
	 * 
	 * @param filter
	 * @param callback parameter #1 : JSON response (WebResponse.class)
	 */
	public void getPeriodicCashflow(final Filter filter, final MyCallback<WebResponse> callback) {
		ThreadUtil.runWithLoading( ()->{ 
			try { 

				HashMap<Object, Object> mapResponse = callPeriodicCashflow(filter);

				WebResponse jsonResponse = parseCashflowPeriodicResponse(mapResponse);

				callback.handle(jsonResponse);
			} catch (Exception e) {
				e.printStackTrace();
				Dialogs.error("Error getPeriodicCashflow: " + e.getMessage());
			} finally { } 
		}); 

	}

	/**
	 * convert from hashmap to object
	 * @param mapResponse
	 * @return
	 */
	private WebResponse parseCashflowPeriodicResponse(HashMap<Object, Object> mapResponse) {
		try {
			List supplies = (List) mapResponse.get("supplies");
			List purchases = (List) mapResponse.get("purchases");
			
			List<BaseEntity> suppliesList = mapListToCashflowList(supplies);
			List<BaseEntity> purchasesList = mapListToCashflowList(purchases); 
			 
			WebResponse parsedResponse = WebResponse.builder().supplies(suppliesList).purchases(purchasesList).build();
			return parsedResponse;
		}catch (Exception e) {
			Dialogs.error("Error getting response : ", e);
			throw e;
		}
	}
	
	/**
	 * convert list of hashMap to list of Cashflow
	 * @param mapList
	 * @return
	 */
	private List<BaseEntity> mapListToCashflowList(List mapList){
		List<BaseEntity> resultList = new ArrayList<BaseEntity>();
		for(Object item : mapList) {
			try {
				resultList.add((CashFlow)MapUtil.mapToObject((Map)item, CashFlow.class));
			}catch (Exception e) { 
				Log.log("Error converting map to cashflow: ", e);
			}
		}
		return resultList;
	}
	
	/**
	 * generate excel report
	 * @param WebRequest
	 * @param myCallback handle ResponseEntity<byte[]>
	 * @param reportType parameter #1 : reportFile (ResponseEntity<byte[]>)
	 * 					 parameter #2 : reportType (ReportType.class)
	 */
	public void downloadReportExcel(final WebRequest WebRequest, final MyCallback<ReportResponse> myCallback, final ReportType reportType) {
		ThreadUtil.runWithLoading( ()->{
				 
			Log.log("Will generate report: ", reportType.toString());
			
			try {
				ResponseEntity<byte[]> response = null;
				switch (reportType) {
					case DAILY:
						response = callDownloadExcelDaily(WebRequest);
						break;
					case MONTHLY:
						response = callDownloadExcelMonthly(WebRequest);
						break;
					case ENTITY:
						response = callDownloadEntityReport(WebRequest);
						break;
					default:
						throw new IllegalArgumentException("Invalid Report Type");
				}
				
				ReportResponse reportResponse = new ReportResponse(reportType, response);
				
				myCallback.handle(reportResponse);
				
			}catch (Exception e) {
				Dialogs.error("Error generating report: ", e.getMessage());
			} finally {  } 
		});
	}
	
	
	/***
	 *  ========================================
	 *                 WEBSERVICE CALLS
	 *  ========================================
	 *  
	 */
	
	private WebResponse callGetMothlyCashflowDetail(int month, int year) {
		
		try {
			WebRequest webRequest = WebRequest.builder()
					.filter(Filter.builder().year(year).month(month).build()).build();
	
			ResponseEntity<WebResponse> response = restTemplate.postForEntity(URL_MONTHLY_CASHFOW,
					RestComponent.buildAuthRequest(webRequest, true), WebResponse.class);
			return response.getBody();
			
		}catch (Exception e) { 
			e.printStackTrace();
			throw e;
		}
	}
	

	private HashMap<Object, Object> callPeriodicCashflow(Filter filter) {
		
		try {
			WebRequest webRequest = WebRequest.builder().filter(filter).build();
	
			ResponseEntity<HashMap<Object, Object> > response = restTemplate.postForEntity(URL_PERIODIC_CASHFOW,
					RestComponent.buildAuthRequest(webRequest, true), ObjectUtil.getEmptyHashMapClass());
	
			return response.getBody();
			 
		}catch (Exception e) { 
			e.printStackTrace();
			throw e;
		}
	}  
	private WebResponse callDailyCashflow(int day, int month, int year) {
		try {
			WebRequest webRequest = WebRequest.builder()
					.filter(Filter.builder().day(day).year(year).month(month).build()).build();
	
			ResponseEntity<WebResponse> response = restTemplate.postForEntity(URL_DAILY_CASHFOW,
					RestComponent.buildAuthRequest(webRequest, true), WebResponse.class);
	
			return response.getBody();
			
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	
	/**
	 * excel report
	 */
	
	private ResponseEntity< byte[] > callDownloadExcelDaily(WebRequest WebRequest) {
		try { 
	
			ResponseEntity< byte[] > response = restTemplate.postForEntity(WebServiceConstants.URL_REPORT_DAILY,
					RestComponent.buildAuthRequest(WebRequest, true),  byte[] .class);
	
			return response;
			
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	private  ResponseEntity<byte[]>  callDownloadExcelMonthly(WebRequest WebRequest) {
		try { 
	
			ResponseEntity< byte[] > response = restTemplate.postForEntity(WebServiceConstants.URL_REPORT_MONTHLY,
					RestComponent.buildAuthRequest(WebRequest, true),  byte[] .class);
	
			return response;
			
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	
	private  ResponseEntity<byte[]>  callDownloadEntityReport(WebRequest WebRequest) {
		try { 
	
			ResponseEntity< byte[] > response = restTemplate.postForEntity(WebServiceConstants.URL_REPORT_ENTITY,
					RestComponent.buildAuthRequest(WebRequest, true),  byte[] .class);
	
			return response;
			
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
}
