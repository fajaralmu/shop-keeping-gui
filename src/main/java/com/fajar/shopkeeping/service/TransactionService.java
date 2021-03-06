package com.fajar.shopkeeping.service;

import static com.fajar.shopkeeping.constant.WebServiceConstants.URL_TRAN_SUPPLY;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.RuntimeErrorException;

import org.springframework.http.ResponseEntity;

import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.Dialogs;
import com.fajar.shopkeeping.constant.WebServiceConstants;
import com.fajar.shopkeeping.util.MapUtil;
import com.fajar.shopkeeping.util.ObjectUtil;
import com.fajar.shopkeeping.util.ThreadUtil;
import com.fajar.shoppingmart.dto.Filter;
import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.entity.Customer;
import com.fajar.shoppingmart.entity.Product;
import com.fajar.shoppingmart.entity.ProductFlow;
import com.fajar.shoppingmart.entity.Supplier;
import com.fajar.shoppingmart.util.CollectionUtil;

public class TransactionService extends BaseService{ 

	private static TransactionService app;

	public static TransactionService getInstance() {
		if (null == app) {
			app = new TransactionService();
		}
		return app;
	}

	private TransactionService() {

	}

	 /**
	  * 
	  * @param productFlows
	  * @param supplier
	  * @param myCallback parameter #1 : JSON response (WebResponse.class)
	  */
	public void transactionSupply(final List<ProductFlow> productFlows, final Supplier supplier, final MyCallback<WebResponse> myCallback) {
		ThreadUtil.runWithLoading(()-> {
			try {
				WebResponse response = callTransactionSupply(productFlows, supplier);
				
				if("00".equals(response.getCode()) == false) {
					Error error = new Error(response.getMessage());
					throw new RuntimeErrorException(error );
				}
				
				myCallback.handle(response);
				Dialogs.error("Transaction Success!");

			} catch (Exception e) {
				Dialogs.error("Error performing transaction :" +e.getMessage());
			} finally { }
				 
		});
	}
	
	/**
	 * 
	 * @param productFlows
	 * @param customer
	 * @param myCallback parameter #1 : JSON response (WebResponse.class)
	 */
	public void transactionSell(final List<ProductFlow> productFlows, final Customer customer, final MyCallback<WebResponse> myCallback) {
		ThreadUtil.runWithLoading(()-> {
			try {
				WebResponse response = callTransactionSell(productFlows, customer);
				
				if("00".equals(response.getCode()) == false) {
					throw new Exception(response.getCode());
				}
				
				myCallback.handle(response);
				Dialogs.error("Transaction Success!");

			} catch (Exception e) {
				Dialogs.error("Error performing transaction :" +e.getMessage());
			} finally { } 
		});
	}
	
	/**
	 * 
	 * @param productCode
	 * @param callback parameter #1 : JSON response (WebResponse.class)
	 */
	public void getProductDetail(final String productCode, final MyCallback<WebResponse> callback) {
		
		ThreadUtil.runWithLoading(()->{
			try {
				WebResponse response = getProductDetail(productCode); 
				callback.handle(response);
			} catch (Exception e) {
				Dialogs.error("Error getting product detail: "+e.getMessage());
				e.printStackTrace();
			} finally { } 
		});
	}
	
	/**
	 * get product detail JSON parsed from HashMap
	 * @param productCode
	 * @return
	 */
	private WebResponse getProductDetail(final String productCode) {

		try {
			HashMap<Object, Object> response = callProductDetail(productCode);
	
			if (response.get("code").equals("00") == false) {
				return WebResponse.failed();
			}
	
			List<Map<?, ?>> rawEntityList = (List) response.get("entities"); 
			List<? extends BaseEntity> resultList = MapUtil.convertMapList(rawEntityList, Product.class); 
			WebResponse jsonResponse = new WebResponse(); 
			jsonResponse.setEntities(CollectionUtil.convertList(resultList));  
			return jsonResponse;
			
		}catch (Exception e) {
			
			return WebResponse.failed();
		}

	}
	
	/***
	 *  ========================================
	 *                 WEBSERVICE CALLS
	 *  ========================================
	 *  
	 *  
	 */
	
	private WebResponse callTransactionSupply(List<ProductFlow> productFlows, Supplier supplier) throws Exception {
		
		try {
			if(null == productFlows || productFlows.size() == 0 || supplier == null) {
				throw new Exception("Invalid Parameter");
			}
			
			WebRequest shopApiRequest = WebRequest.builder().productFlows(productFlows).supplier(supplier).build();
	
			ResponseEntity<WebResponse> response = restTemplate.postForEntity(URL_TRAN_SUPPLY,
					RestComponent.buildAuthRequest(shopApiRequest, true), WebResponse.class);
			return response.getBody();
			
		}catch (Exception e) { 
			e.printStackTrace();
			throw e;
		}
	}
	
	private WebResponse callTransactionSell(List<ProductFlow> productFlows, Customer customer) throws Exception {
		
		try {
			if(null == productFlows || productFlows.size() == 0 || customer == null) {
				throw new Exception("Invalid Parameter");
			}
			
			WebRequest shopApiRequest = WebRequest.builder().productFlows(productFlows).customer(customer).build();
	
			ResponseEntity<WebResponse> response = restTemplate.postForEntity(WebServiceConstants.URL_TRAN_SELL_V2,
					RestComponent.buildAuthRequest(shopApiRequest, true), WebResponse.class);
			return response.getBody();
			
		}catch (Exception e) { 
			e.printStackTrace();
			throw e;
		}
	}
	
	
	private HashMap<Object, Object> callProductDetail(final String productCode) throws Exception {
		
		try {
			Map<String, Object> fieldsFilter = new HashMap<String, Object>(){ 
				private static final long serialVersionUID = -2370303952097303347L;

				{
					put("code", productCode);
					put("withStock", true);
					put("withSupplier", false);
				}
			};
			Filter filter = Filter.builder().exacts(true).contains(false).limit(1).fieldsFilter(fieldsFilter ).build();
			WebRequest shopApiRequest = WebRequest.builder().entity("product").filter(filter ).build();
	
			ResponseEntity<HashMap<Object, Object>> response = restTemplate.postForEntity(WebServiceConstants.URL_PUBLIC_GET,
					RestComponent.buildAuthRequest(shopApiRequest, true), ObjectUtil.getEmptyHashMapClass());
			return response.getBody();
			
		}catch (Exception e) { 
			e.printStackTrace();
			throw e;
		}
	}
	

	 
	
}
