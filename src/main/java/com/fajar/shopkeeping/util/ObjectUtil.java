package com.fajar.shopkeeping.util;

import java.util.HashMap;

import com.fajar.shoppingmart.dto.FieldType;

public class ObjectUtil { 
	
	private final static Object HASHMAP_EMPTY = new HashMap<Object, Object>();


	@SuppressWarnings("unchecked")
	private static <T> Class<T> getClass(Object o){
		return (Class<T>) o.getClass();
	}
	
	public static <T> Class<T> getEmptyHashMapClass() {
		return getClass(HASHMAP_EMPTY );
	}
	
	public static FieldType getFieldTypeEnum(String value) {
		FieldType[] values = FieldType.values();
		for (FieldType fieldType : values) {
			if(fieldType.value.equals(value)) {
				return fieldType;
			}
		}
		return null;
	}
	 
	
	public static void main(String[] sdd) {
		System.out.println(getFieldTypeEnum("textarea"));
	}

}
