package com.fajar.shopkeeping.service;

public class EntityService extends BaseService{
 

	private static EntityService app;

	public static EntityService getInstance() {
		if (null == app) {
			app = new EntityService();
		}
		return app;
	}

	private EntityService() {

	}
}
