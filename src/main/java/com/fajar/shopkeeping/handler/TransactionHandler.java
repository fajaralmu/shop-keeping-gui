package com.fajar.shopkeeping.handler;

import java.util.HashMap;
import java.util.Map;

import com.fajar.dto.Filter;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.pages.SupplyTransactionPage;

public class TransactionHandler extends MainHandler {

	public TransactionHandler() {
		super();
	}

	@Override
	protected void init() {
		super.init();  
		page = new SupplyTransactionPage();
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
	 
}
