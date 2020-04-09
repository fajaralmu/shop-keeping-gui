package com.fajar.shopkeeping.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fajar.dto.Filter;
import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.ProductFlow;
import com.fajar.entity.Supplier;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.pages.BaseTransactionPage;
import com.fajar.shopkeeping.pages.SellingTransactionPage;
import com.fajar.shopkeeping.pages.SupplyTransactionPage;

public class TransactionHandler extends MainHandler {

	
	
	public TransactionHandler(BaseTransactionPage transactionPage) {
		super(transactionPage);
	}

	@Override
	protected void init() {
		super.init();   
	}
 
	/** populate dynamic comboBox items
	 * @param entityClass
	 * @param key
	 * @param value
	 * @param callback
	 */
	public void getEntitiesFromDynamicDropdown(Class<?> entityClass, final String key, final Object value,
			MyCallback callback) {

		Map<String, Object> fieldsFilter = new HashMap<String, Object>() {
			{
				put(key, value);
			}
		};
		Filter filter = Filter.builder().page(0).limit(10).fieldsFilter(fieldsFilter).build();
		entityService.getEntityListJsonResponse(filter, entityClass, callback);
	}
	
	public void transactionSupply(List<ProductFlow> productFlows, Supplier supplier) {
		MyCallback myCallback = new MyCallback() {
			
			@Override
			public void handle(Object... params) throws Exception {
				ShopApiResponse response = (ShopApiResponse) params[0];
				getSupplyPage().callbackTransactionSupply(response);
			}
		};
		transactionService.transactionSupply(productFlows, supplier, myCallback );
	}
	
	private SupplyTransactionPage getSupplyPage() {
		return (SupplyTransactionPage) page;
	}
	
	private SellingTransactionPage getSellingPage() {
		return (SellingTransactionPage) page;
	}
	 
	
	public void getProductDetail(final String productCode) {
		MyCallback callback = new MyCallback() {
			
			@Override
			public void handle(Object... params) throws Exception {
				ShopApiResponse response = (ShopApiResponse) params[0];
				getSellingPage().callbackGetProductDetail(response);
			}
		};
		transactionService.getProductDetail(productCode, callback );
	}
}
