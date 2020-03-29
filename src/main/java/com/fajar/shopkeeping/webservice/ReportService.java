package com.fajar.shopkeeping.webservice;

import static com.fajar.shopkeeping.constant.WebServiceConstants.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activity.InvalidActivityException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fajar.dto.Filter;
import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.User;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.Dialogs;
import com.fajar.shopkeeping.component.Loadings;
import com.fajar.shopkeeping.util.MapUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReportService {

	protected static final String HEADER_LOGIN_KEY = "loginKey";

	private RestTemplate restTemplate = RestComponent.getRestTemplate();

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

		Thread thread = new Thread(new Runnable() {

			public void run() {

				try { 
					ShopApiRequest shopApiRequest = ShopApiRequest.builder().
							filter(Filter.builder().
									year(year).
									month(month).
									build()).
							build();
					
					ResponseEntity<ShopApiResponse> response = restTemplate.postForEntity(URL_MONTHLY_CASHFOW,
							RestComponent.buildAuthRequest(shopApiRequest, true), ShopApiResponse.class);
					
					ShopApiResponse jsonResponse = response.getBody();
					
					callback.handle(jsonResponse);
				} catch (Exception e) {
					e.printStackTrace();
					Dialogs.showErrorDialog("Error requesting app id: " + e.getMessage());
				} finally {
					Loadings.end();
				}
			}
		});
		thread.start();
	}

	 

}
