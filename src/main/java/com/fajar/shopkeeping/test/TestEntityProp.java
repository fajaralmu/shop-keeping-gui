package com.fajar.shopkeeping.test;

import com.fajar.shoppingmart.entity.Product;
import com.fajar.shoppingmart.entity.setting.EntityProperty;
import com.fajar.shoppingmart.util.EntityUtil;

public class TestEntityProp {

	public static void main(String[] args) {
		EntityProperty entityProp = EntityUtil.createEntityProperty(Product.class, null);
		System.out.println("entityPropID: "+entityProp.getIdField());
	}

}
