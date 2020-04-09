package com.fajar.shopkeeping.service;

import static com.fajar.shopkeeping.constant.WebServiceConstants.URL_DAILY_CASHFOW;
import static com.fajar.shopkeeping.constant.WebServiceConstants.URL_MONTHLY_CASHFOW;
import static com.fajar.shopkeeping.constant.WebServiceConstants.URL_PERIODIC_CASHFOW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.fajar.dto.Filter;
import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.custom.CashFlow;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.Dialogs;
import com.fajar.shopkeeping.component.Loadings;
import com.fajar.shopkeeping.util.MapUtil;
import com.fajar.shopkeeping.util.ThreadUtil;

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
	 * @param callback params : JsonResponse
	 */
	public void getMonthlyCashflowDetail(final int month, final int year, final MyCallback callback) {
		Loadings.start();

		ThreadUtil.run(new Runnable() {

			public void run() {

				try { 
					ShopApiResponse jsonResponse = callGetMothlyCashflowDetail(month, year);

					callback.handle(jsonResponse);
				} catch (Exception e) {
					e.printStackTrace();
					Dialogs.error("Error getMonthlyCashflowDetail: " + e.getMessage());
				} finally {
					Loadings.end();
				}
			} 
		}); 
	}

	/**
	 * get daily product flow
	 * 
	 * @param day
	 * @param month
	 * @param year
	 * @param callback
	 */
	public void getDailyCashflowDetail(final int day, final int month, final int year, final MyCallback callback) {
		Loadings.start();

		Thread thread = new Thread(new Runnable() {

			public void run() {

				try { 
					ShopApiResponse jsonResponse = callDailyCashflow(day, month, year);

					callback.handle(jsonResponse);
				} catch (Exception e) {
					e.printStackTrace();
					Dialogs.error("Error getDailyCashflowDetail: " + e.getMessage());
				} finally {
					Loadings.end();
				}
			}

		});
		thread.start();
	}

	/**
	 * get cash flow from selected period to selected period
	 * 
	 * @param filter
	 * @param callback
	 */
	public void getPeriodicCashflow(final Filter filter, final MyCallback callback) {
		Loadings.start();

		Thread thread = new Thread(new Runnable() {

			public void run() {

				try { 

					HashMap mapResponse = callPeriodicCashflow(filter);

					ShopApiResponse jsonResponse = parseCashflowPeriodicResponse(mapResponse);

					callback.handle(jsonResponse);
				} catch (Exception e) {
					e.printStackTrace();
					Dialogs.error("Error getPeriodicCashflow: " + e.getMessage());
				} finally {
					Loadings.end();
				}
			}


		});
		thread.start();

	}

	/**
	 * convert from hashmap to object
	 * @param mapResponse
	 * @return
	 */
	private ShopApiResponse parseCashflowPeriodicResponse(HashMap mapResponse) {
		List supplies = (List) mapResponse.get("supplies");
		List purchases = (List) mapResponse.get("purchases");
		
		List<BaseEntity> suppliesList = mapListToCashflowList(supplies);
		List<BaseEntity> purchasesList = mapListToCashflowList(purchases); 
		 
		ShopApiResponse parsedResponse = ShopApiResponse.builder().supplies(suppliesList).purchases(purchasesList).build();
		return parsedResponse;
	}
	
	private List<BaseEntity> mapListToCashflowList(List mapList){
		List<BaseEntity> resultList = new ArrayList<BaseEntity>();
		for(Object item : mapList) {
			resultList.add((CashFlow)MapUtil.mapToObject((Map)item, CashFlow.class));
		}
		return resultList;
	}
	
	
	/***
	 *  ========================================
	 *                 WEBSERVICE CALLS
	 *  ========================================
	 *  
	 */
	
	private ShopApiResponse callGetMothlyCashflowDetail(int month, int year) {
		
		try {
			ShopApiRequest shopApiRequest = ShopApiRequest.builder()
					.filter(Filter.builder().year(year).month(month).build()).build();
	
			ResponseEntity<ShopApiResponse> response = restTemplate.postForEntity(URL_MONTHLY_CASHFOW,
					RestComponent.buildAuthRequest(shopApiRequest, true), ShopApiResponse.class);
			return response.getBody();
			
		}catch (Exception e) { 
			e.printStackTrace();
			throw e;
		}
	}
	

	private HashMap callPeriodicCashflow(Filter filter) {
		
		try {
			ShopApiRequest shopApiRequest = ShopApiRequest.builder().filter(filter).build();
	
			ResponseEntity<HashMap> response = restTemplate.postForEntity(URL_PERIODIC_CASHFOW,
					RestComponent.buildAuthRequest(shopApiRequest, true), HashMap.class);
	
			return response.getBody();
			 
		}catch (Exception e) { 
			e.printStackTrace();
			throw e;
		}
	}

	

	private ShopApiResponse callDailyCashflow(int day, int month, int year) {
		try {
			ShopApiRequest shopApiRequest = ShopApiRequest.builder()
					.filter(Filter.builder().day(day).year(year).month(month).build()).build();
	
			ResponseEntity<ShopApiResponse> response = restTemplate.postForEntity(URL_DAILY_CASHFOW,
					RestComponent.buildAuthRequest(shopApiRequest, true), ShopApiResponse.class);
	
			return response.getBody();
			
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
}
