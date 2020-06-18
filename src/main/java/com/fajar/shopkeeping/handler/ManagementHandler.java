package com.fajar.shopkeeping.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.callbacks.WebResponseCallback;
import com.fajar.shopkeeping.pages.ManagementPage;
import com.fajar.shopkeeping.util.Log;
import com.fajar.shopkeeping.util.MapUtil;
import com.fajar.shoppingmart.dto.Filter;
import com.fajar.shoppingmart.dto.WebResponse;

public class ManagementHandler extends MainHandler<ManagementPage> {

	public ManagementHandler() {
		super();
	}

	@Override
	protected void init() {
		super.init();
		page = new ManagementPage();
	}

	public List<Map<Object, Object>> getAllEntity(Class<?> fieldType) {

		List<Map<Object, Object>> resultList = entityService.getAllEntityOnlyList(fieldType);
		return resultList;
	}

	/**
	 * populate dynamic comboBox items
	 * @param entityClass
	 * @param key
	 * @param value
	 * @param callback
	 */
	public void getEnitiesFormDynamicDropdown(Class<?> entityClass, final String key, final Object value,
			MyCallback<Map<Object, Object>> callback) {

		Map<String, Object> fieldsFilter = MapUtil.singleMap(key, value);
		Filter filter = Filter.builder().page(0).limit(10).fieldsFilter(fieldsFilter).build();
		entityService.getEntityListHashMapResponse(filter, entityClass, callback);
	}
	
	public static Map<Object, Object> getMapFromList(String key, Object selectedValue, List<Map<Object, Object>> list) {
		return MapUtil.getMapFromList(key, selectedValue, list);
	}

	/**
	 * when button submit clicked
	 * @return
	 */
	public ActionListener submit() {
		 
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Log.log("ACTION PERFORMED:",e.getActionCommand());
				submitEntity();
				
			} 
			
		};
	} 
	
	/**
	 * submit update / add new record
	 */
	private void submitEntity() {
		int confirm = JOptionPane.showConfirmDialog(null, "Continue submit?");
		
		if(confirm == 0) { 
		
			Map<String, Object> managedObject = getPage().getManagedObject();
//			String idField = getPage().getIdFieldName();
			
			Log.log("Submit managedObject: ", managedObject); 
			 
			getPage().validateEntity();
			entityService.updateEntity(managedObject, getPage().isEditMode(), getPage().getEntityClass(), new MyCallback<Map<Object, Object>>() {
				
				@Override
				public void handle(Map<Object, Object> response) throws Exception { 
					
					getPage().callbackUpdateEntity(response);
				}
			});
			 
		}  
		else {
			Log.log("Operation aborted"); 
		}
	}
	
	/**
	 * when filter entity button pressed
	 * @return
	 */
	public ActionListener filterEntity() {
		
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				getEntities();				
			}
		};
	}
	
	/**
	 * get entities for populating data table
	 * @param fieldFilter
	 * @param callback
	 */
	public void getEntities( ) {	
		
		Log.log("Page: ",getPage().getSelectedPage(), "Limit: ", getPage().getSelectedLimit());
		
		Filter filter = new Filter();
		filter.setPage(getPage().getSelectedPage());
		filter.setLimit(getPage().getSelectedLimit());
		filter.setFieldsFilter(getPage().getFieldsFilter());
		filter.setOrderBy(getPage().getOrderBy());
		filter.setOrderType(getPage().getOrderType());
		
		entityService.getEntityList(
				filter,
				getPage().getEntityClass(),
				new WebResponseCallback() {
					
					@Override
					public void handle(WebResponse response) throws Exception { 
						
						getPage().callbackGetFilteredEntities(response);
					}
				});
	}
	
	/**
	 * get entity by ID
	 */
	public void getSingleEntity(String idFieldName, Object idValue) {
		
		entityService.getSingleEntityByID(
				idFieldName, 
				idValue, 
				getPage().getEntityClass(), 
				getPage().callbackGetSingleEntity());
	}


}
