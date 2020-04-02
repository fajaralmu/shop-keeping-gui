package com.fajar.shopkeeping.service;

import static com.fajar.shopkeeping.constant.WebServiceConstants.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.fajar.dto.Filter;
import com.fajar.dto.ShopApiRequest;
import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.Dialogs;
import com.fajar.shopkeeping.component.Loadings;
import com.fajar.shopkeeping.util.Log;
import com.fajar.shopkeeping.util.MapUtil;

public class EntityService extends BaseService {

	private static EntityService app;

	public static EntityService getInstance() {
		if (null == app) {
			app = new EntityService();
		}
		return app;
	}

	private EntityService() {

	}

	public void getEntityList(final int page, final int limit, final Class entityClass, final MyCallback callback) {
		Loadings.start();

		Thread thread = new Thread(new Runnable() {

			public void run() {

				try {
					ShopApiResponse response = getEntityList(page, limit, entityClass);
					callback.handle(response);
				} catch (Exception e) {
					e.printStackTrace();
					Dialogs.showErrorDialog("Error getEntityList: " + e.getMessage());
				} finally {
					Loadings.end();
				}
			}

		});
		thread.start();
	} 
	
	public void getEntityList(final Filter filter, final Class entityClass, final MyCallback callback) {
		Loadings.start();

		Thread thread = new Thread(new Runnable() {

			public void run() {

				try {
					HashMap response = callGetEntity(filter, entityClass);
					callback.handle(response);
				} catch (Exception e) {
					e.printStackTrace();
					Dialogs.showErrorDialog("Error getEntityList: " + e.getMessage());
				} finally {
					Loadings.end();
				}
			}

		});
		thread.start();
	}
	
	public void getEntityListHashMap(final Filter filter, final Class entityClass, final MyCallback callback) {
		Loadings.start();

		Thread thread = new Thread(new Runnable() {

			public void run() {

				try {
					ShopApiResponse response = getEntityList(filter, entityClass);
					callback.handle(response);
				} catch (Exception e) {
					e.printStackTrace();
					Dialogs.showErrorDialog("Error getEntityList: " + e.getMessage());
				} finally {
					Loadings.end();
				}
			}

		});
		thread.start();
	}

	private ShopApiResponse getEntityList(Filter filter, Class entityClass) {

		HashMap response = callGetEntity(filter, entityClass);

		if (response.get("code").equals("00") == false) {
			return ShopApiResponse.failed();
		}

		List rawEntityList = (List) response.get("entities");

		List<BaseEntity> resultList = MapUtil.convertMapList(rawEntityList, entityClass);

		ShopApiResponse jsonResponse = new ShopApiResponse();

		jsonResponse.setEntities(resultList);
		jsonResponse.setTotalData((Integer) response.get("totalData"));

		return jsonResponse;

	}
	
	public List< Map> getAllEntityList(  Class entityClass) {

		HashMap response = callGetEntity(Filter.builder().page(1).limit(0).build(), entityClass); 
		List rawEntityList = (List) response.get("entities");

		return rawEntityList;

	}

	private ShopApiResponse getEntityList(int page, int limit, Class entityClass) {

		return getEntityList(Filter.builder().page(page).limit(limit).build(), entityClass);
	}

	private HashMap callGetEntity(Filter filter, Class entityClass) {
		try {

			ShopApiRequest shopApiRequest = ShopApiRequest.builder().entity(entityClass.getSimpleName().toLowerCase())
					.filter(filter).build();
			ResponseEntity<HashMap> response = restTemplate.postForEntity(URL_ENTITY_GET,
					RestComponent.buildAuthRequest(shopApiRequest, true), HashMap.class);
			Log.log("response: ",response);
			return response.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
