package com.fajar.shopkeeping.handler;

import java.util.List;
import java.util.Map;

import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.pages.BaseTransactionPage;
import com.fajar.shopkeeping.pages.PurchasingTransactionPage;
import com.fajar.shopkeeping.pages.SellingTransactionPage;
import com.fajar.shopkeeping.util.MapUtil;
import com.fajar.shoppingmart.dto.Filter;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.entity.Customer;
import com.fajar.shoppingmart.entity.ProductFlow;
import com.fajar.shoppingmart.entity.Supplier;

public class TransactionHandler extends MainHandler<BaseTransactionPage> {
 
	
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
	public void getEntitiesFromDynamicDropdown(Class<? extends BaseEntity> entityClass, final String key, final Object value,
			MyCallback<WebResponse> callback) {

		Map<String, Object> fieldsFilter = MapUtil.singleMap(key, value);
		
		Filter filter = Filter.builder().page(0).limit(10).fieldsFilter(fieldsFilter).build();
		entityService.getEntityListJsonResponse(filter, entityClass, callback);
	}
	
	public void transactionPurchasing(List<ProductFlow> productFlows, Supplier supplier) {
		MyCallback<WebResponse> myCallback =  getSupplyPage()::callbackTransactionPurchasing;
		transactionService.transactionSupply(productFlows, supplier, myCallback );
	}
	
	private PurchasingTransactionPage getSupplyPage() {
		return (PurchasingTransactionPage) page;
	}
	
	private SellingTransactionPage getSellingPage() {
		return (SellingTransactionPage) page;
	}
	 
	
	public void getProductDetail(final String productCode) {
		MyCallback<WebResponse> callback = getSellingPage()::callbackGetProductDetail; 
		transactionService.getProductDetail(productCode, callback );
	}
	
	public void transactionSell(List<ProductFlow> productFlows, Customer customer) {
		MyCallback<WebResponse> myCallback =  getSellingPage()::callbackTransactionSell;
		transactionService.transactionSell(productFlows, customer, myCallback);
	}
}
