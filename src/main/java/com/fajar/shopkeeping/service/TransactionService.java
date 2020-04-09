package com.fajar.shopkeeping.service;

import static com.fajar.shopkeeping.constant.WebServiceConstants.URL_TRAN_SUPPLY;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activity.InvalidActivityException;

import org.springframework.http.ResponseEntity;

import com.fajar.dto.Filter;
import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.Product;
import com.fajar.entity.ProductFlow;
import com.fajar.entity.Supplier;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.Dialogs;
import com.fajar.shopkeeping.component.Loadings;
import com.fajar.shopkeeping.constant.WebServiceConstants;
import com.fajar.shopkeeping.util.MapUtil;
import com.fajar.shopkeeping.util.ThreadUtil;

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
	  * @param myCallback handles json response
	  */
	public void transactionSupply(final List<ProductFlow> productFlows, final Supplier supplier, final MyCallback myCallback) {
		Loadings.start();
		ThreadUtil.run(new Runnable() {
			
			@Override
			public void run() {
				try {
					ShopApiResponse response = callTransactionSupply(productFlows, supplier);
					
					if("00".equals(response.getCode()) == false) {
						throw new InvalidActivityException(response.getCode());
					}
					
					myCallback.handle(response);
					Dialogs.error("Transaction Success!");

				} catch (Exception e) {
					Dialogs.error("Error performing transaction :" +e.getMessage());
				} finally {
					Loadings.end();
				}
				
			}
		});
	}
	
	
	public void getProductDetail(final String productCode, final MyCallback callback) {
		
		Loadings.start();
		ThreadUtil.run(new Runnable() {
			
			@Override
			public void run() {
				try {
					ShopApiResponse response = getProductDetail(productCode); 
					callback.handle(response);
				} catch (Exception e) {
					Dialogs.error("Error getting product detail: "+e.getMessage());
					e.printStackTrace();
				} finally {
					Loadings.end();
				}
			}
		});
	}
	
	private ShopApiResponse getProductDetail(String productCode) {

		try {
			HashMap response = callProductDetail(productCode);
	
			if (response.get("code").equals("00") == false) {
				return ShopApiResponse.failed();
			}
	
			List rawEntityList = (List) response.get("entities"); 
			List<BaseEntity> resultList = MapUtil.convertMapList(rawEntityList, Product.class); 
			ShopApiResponse jsonResponse = new ShopApiResponse(); 
			jsonResponse.setEntities(resultList);  
			return jsonResponse;
			
		}catch (Exception e) {
			
			return ShopApiResponse.failed();
		}

	}
	
	/***
	 *  ========================================
	 *                 WEBSERVICE CALLS
	 *  ========================================
	 *  
	 */
	
	private ShopApiResponse callTransactionSupply(List<ProductFlow> productFlows, Supplier supplier) {
		
		try {
			ShopApiRequest shopApiRequest = ShopApiRequest.builder().productFlows(productFlows).supplier(supplier).build();
	
			ResponseEntity<ShopApiResponse> response = restTemplate.postForEntity(URL_TRAN_SUPPLY,
					RestComponent.buildAuthRequest(shopApiRequest, true), ShopApiResponse.class);
			return response.getBody();
			
		}catch (Exception e) { 
			e.printStackTrace();
			throw e;
		}
	}
	
	
	private HashMap callProductDetail(final String productCode) {
		
		try {
			Map<String, Object> fieldsFilter = new HashMap<String, Object>(){
				{
					put("code", productCode);
					put("withStock", true);
					put("withSupplier", false);
				}
			};
			Filter filter = Filter.builder().exacts(true).contains(false).limit(1).fieldsFilter(fieldsFilter ).build();
			ShopApiRequest shopApiRequest = ShopApiRequest.builder().entity("product").filter(filter ).build();
	
			ResponseEntity<HashMap> response = restTemplate.postForEntity(WebServiceConstants.URL_PUBLIC_GET,
					RestComponent.buildAuthRequest(shopApiRequest, true), HashMap.class);
			return response.getBody();
			
		}catch (Exception e) { 
			e.printStackTrace();
			throw e;
		}
	}
	

	 
	
}
