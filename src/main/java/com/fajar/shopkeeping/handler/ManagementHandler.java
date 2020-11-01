package com.fajar.shopkeeping.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.springframework.http.ResponseEntity;

import com.fajar.shopkeeping.callbacks.ApplicationException;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.Dialogs;
import com.fajar.shopkeeping.component.Loadings;
import com.fajar.shopkeeping.constant.ReportType;
import com.fajar.shopkeeping.model.ReportResponse;
import com.fajar.shopkeeping.pages.ManagementPage;
import com.fajar.shopkeeping.util.Log;
import com.fajar.shopkeeping.util.MapUtil;
import com.fajar.shoppingmart.dto.Filter;
import com.fajar.shoppingmart.dto.WebRequest;

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
	 * 
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
	 * 
	 * @return
	 */
	public ActionListener submit() { 
		return (ActionEvent e)-> { 
			submitEntity(); 
		};
	}

	/**
	 * submit update / add new record
	 */
	private void submitEntity() {
		int confirm = JOptionPane.showConfirmDialog(null, "Continue submit?");

		if (confirm == 0) {

			Map<String, Object> managedObject = getPage().getManagedObject();
//			String idField = getPage().getIdFieldName();

			Log.log("Submit managedObject: ", managedObject);

			getPage().validateEntity(); 
			entityService.updateEntity(managedObject, 
					getPage().isEditMode(), 
					getPage().getEntityClass(),
					getPage()::callbackUpdateEntity );

		} else {
			Log.log("Operation aborted");
		}
	}

	/**
	 * when filter entity button pressed
	 * 
	 * @return
	 */
	public ActionListener filterEntity() { 
		return  (ActionEvent e)->{ getEntities();  };
	}

	/**
	 * get entities for populating data table
	 * 
	 * @param fieldFilter
	 * @param callback
	 */
	public void getEntities() {

		Log.log("Page: ", getPage().getSelectedPage(), "Limit: ", getPage().getSelectedLimit());

		Filter filter = new Filter();
		filter.setPage(getPage().getSelectedPage());
		filter.setLimit(getPage().getSelectedLimit());
		filter.setFieldsFilter(getPage().getFieldsFilter());
		filter.setOrderBy(getPage().getOrderBy());
		filter.setOrderType(getPage().getOrderType());

		entityService.getEntityList(filter, getPage().getEntityClass(), getPage()::callbackGetFilteredEntities );
	}

	/**
	 * get entity by ID
	 */
	public void getSingleEntity(String idFieldName, Object idValue) {

		entityService.getSingleEntityByID(idFieldName, idValue, getPage().getEntityClass(),
				getPage().callbackGetSingleEntity());
	}

	public ActionListener printExcel() {

		return  (ActionEvent event)-> { 

			WebRequest webRequest = getExcelReportRequest();
			MyCallback<ReportResponse> myCallback =  (ReportResponse reportResponse)-> {
				Log.log("Response daily excel: ", reportResponse.getReportType());
				ResponseEntity<byte[]> response = reportResponse.getFileResponse();
				Loadings.end();

				String fileName = getFileName(response);
				try {
					saveFile(response.getBody(), fileName);
				} catch (Exception e) {
					throw new ApplicationException(e);
				} 
			};
			reportService.downloadReportExcel(webRequest, myCallback, ReportType.ENTITY); 

		};
	}

	private WebRequest getExcelReportRequest() {
		Filter filter = new Filter();
		filter.setPage(getPage().getSelectedPage());

		String limit = Dialogs.input("please input limit");
		try {
			filter.setLimit(Integer.valueOf(limit));
		} catch (Exception e) {
			e.printStackTrace();
			filter.setLimit(5);
		}
		filter.setFieldsFilter(getPage().getFieldsFilter());
		filter.setOrderBy(getPage().getOrderBy());
		filter.setOrderType(getPage().getOrderType());
		WebRequest webRequest = new WebRequest();
		webRequest.setFilter(filter);
		webRequest.setEntity(getPage().getEntityClass().getSimpleName().toLowerCase());
		return webRequest;
	}

}
