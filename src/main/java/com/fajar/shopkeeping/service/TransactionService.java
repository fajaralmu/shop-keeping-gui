package com.fajar.shopkeeping.service;

import static com.fajar.shopkeeping.constant.WebServiceConstants.URL_TRAN_SUPPLY;

import java.util.List;

import javax.activity.InvalidActivityException;

import org.springframework.http.ResponseEntity;

import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.ProductFlow;
import com.fajar.entity.Supplier;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.Dialogs;
import com.fajar.shopkeeping.component.Loadings;
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
					Dialogs.showErrorDialog("Transaction Success!");

				} catch (Exception e) {
					Dialogs.showErrorDialog("Error performing transaction :" +e.getMessage());
				} finally {
					Loadings.end();
				}
				
			}
		});
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
	

	 
	
}
